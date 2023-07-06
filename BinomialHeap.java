/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap
{
	public int size;
	public HeapNode last;
	public HeapNode min;

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 *
	 */
	public HeapItem insert(int key, String info) 
	{
        HeapItem item = new HeapItem(key, info);
        HeapNode node = new HeapNode(item, null, null, null, 0);
		node.next = node;
        BinomialHeap tempHeap = new BinomialHeap();
        tempHeap.size = 1;
        tempHeap.last = node;
        tempHeap.min = node;
        this.meld(tempHeap);
		return item;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		if(this.min == null) return;
		HeapNode min = this.min;
		int treeSize = (int) Math.pow(2, min.rank);
		if(last == last.next) { // if there is only one node
			last = null;
			this.min = null;
		}
		else {
			if (last == min) {
				while (last.next != min) {
					last = last.next;
				}
			}

			HeapNode iter = last;
			while (iter.next != min) {
				iter = iter.next;
			}
			iter.next = iter.next.next; // delete the old minimum from the linked list

			this.min = last;
			iter = last;
			while (iter.next != last) {
				iter = iter.next;
				if (iter.item.key < this.min.item.key) this.min = iter;
			}
		}
		size -= treeSize;

		// We removed the old minimum's tree from the heap, so we need to meld its children back
		BinomialHeap tempHeap = new BinomialHeap();
		tempHeap.size = treeSize - 1;
		tempHeap.last = min.child;
		tempHeap.min = tempHeap.last;
		if(tempHeap.last != null) {
			tempHeap.last.parent = null;
			HeapNode iter = tempHeap.last;
			while (iter.next != tempHeap.last) {
				iter = iter.next;
				iter.parent = null;
				if (iter.item.key < tempHeap.min.item.key) tempHeap.min = iter;
			}
		}
		this.meld(tempHeap);
	}

	/**
	 * 
	 * Return the minimal HeapItem
	 *
	 */
	public HeapItem findMin()
	{
		if(min == null) return null;
		return min.item;
	} 

	/**
	 * 
	 * pre: 0<diff<item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{
		item.key -= diff;
		while(item.node.parent != null && item.key < item.node.parent.item.key) {
			HeapItem parentItem = item.node.parent.item;
			item.node.parent.item = item;
			parentItem.node = item.node;
			item.node.item = parentItem;
			item.node = item.node.parent;
		}
		if(item.key < min.item.key) {
			min = item.node;
		}
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{
		decreaseKey(item, item.key + 1); // set key to -1
		deleteMin();
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		if(heap2.empty()) return;
		this.size += heap2.size;
		if(this.last == null) {
			this.last = heap2.last;
			this.min = heap2.min;
			return;
		}
		HeapNode iter1 = this.last;
		HeapNode iter2 = heap2.last.next;
		heap2.last.next = null;
		HeapNode carry = null;
		int maxRank = Math.max(this.last.rank, heap2.last.rank) + 1;
		for(int rank = 0; rank <= maxRank; rank++) {
			// the third condition is there to prevent iter1 from going back to the start of the loop but allow it to do so at the start (since it starts as this.last)
			if(iter1.next != null && iter1.next.rank < rank && (iter1.next.next.rank >= rank || iter1.rank < iter1.next.rank)) iter1 = iter1.next;
			if(iter2 != null && iter2.rank < rank) iter2 = iter2.next;
			HeapNode current2 = iter2;
			HeapNode resNoCarry = null; // result of 1 bit binary addition of one to three trees of the same rank
			HeapNode nextCarry = null; // If at least two trees are added, carry the rank+1 sized tree to the next rank
			if(iter1.next != null && iter1.next.rank == rank && iter2 != null && iter2.rank == rank) {
				// both heaps had trees of the current rank
				iter2 = iter2.next;
				current2.next = null;
				nextCarry = link(removeNext(iter1), current2);
			}
			else if(iter1.next != null && iter1.next.rank == rank) {
				// Only this had a tree of the current rank
				resNoCarry = removeNext(iter1);
			}
			else if(iter2 != null && iter2.rank == rank) {
				// Only heap2 had a tree of the current rank
				iter2 = iter2.next;
				current2.next = null;
				resNoCarry = current2;
			}
			if(carry != null) {
				// Add carry from the previous rank
				if(resNoCarry == null) {
					resNoCarry = carry;
				}
				else {
					nextCarry = link(carry, resNoCarry);
					resNoCarry = null;
				}
			}
			if(resNoCarry != null) {
				// Add the result to this (heap)
				if(this.last != null) {
					resNoCarry.next = iter1.next;
					iter1.next = resNoCarry;
					if(this.last.rank < resNoCarry.rank) this.last = resNoCarry;
				}
				else {
					resNoCarry.next = resNoCarry;
					last = resNoCarry;
					iter1 = last;
				}
				// Every new root goes through here, so update the minimum if needed
				if(resNoCarry.item.key <= min.item.key) {
					min = resNoCarry;
				}
			}
			carry = nextCarry;
		}
	}

	/**
	 * Links two binomial trees of the same degree
	 * Returns the new root of the tree so that the previous tree can be updated to point to it
	 */
	private HeapNode link(HeapNode node1, HeapNode node2) {
		// We want the lesser number to be the root, so make sure node1 is less than node2
		if(node1.item.key > node2.item.key) {
			HeapNode temp = node1;
			node1 = node2;
			node2 = temp;
		}
		if(node1.child != null) {
			node2.next = node1.child.next;
			node1.child.next = node2;
		}
		else {
			node2.next = node2;
		}
		node1.child = node2;
		node2.parent = node1;
		node1.rank++;
		return node1;
	}

	/**
	 * Removes the node after root from the root linked list, WITHOUT removing it from min or changing size
	 */
	private HeapNode removeNext(HeapNode root) {
		HeapNode removed = root.next;
		if(root == root.next) {
			last = null;
		}
		else {
			if(last == root.next) last = root;
			root.next = root.next.next;
		}
		removed.next = null;
		return removed;
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return size == 0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		if(empty()) return 0;
		HeapNode iter = last;
		int count = 1;
		while(iter.next != last) {
			iter = iter.next;
			count++;
		}
		return count;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;

        public HeapNode(HeapItem item, HeapNode child, HeapNode next, HeapNode parent, int rank) {
            this.item = item;
            item.node = this;
            this.child = child;
            this.next = next;
            this.parent = parent;
            this.rank = rank;
        }

		public HeapNode() {

		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;

        public HeapItem(int key, String info) {
            this.key = key;
            this.info = info;
        }

		public HeapItem() {

		}
	}

}
