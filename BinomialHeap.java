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
        min = node;
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
		HeapNode min = this.min;
		int treeSize = (int) Math.pow(2, min.rank);
		if(last == min) {
			while(last.next != min) {
				last = last.next;
			}
		}

		HeapNode iter = last;
		while(iter.next != min) {
			iter = iter.next;
		}
		iter.next = iter.next.next; // delete the old minimum from the linked list

		this.min = last;
		iter = last;
		while(iter.next != last) {
			iter = iter.next;
			if(iter.item.key < this.min.item.key) this.min = iter;
		}
		size -= treeSize;

		// We removed the old minimum's tree from the heap, so we need to meld its children back
		BinomialHeap tempHeap = new BinomialHeap();
		tempHeap.size = treeSize;
		tempHeap.last = min.child;
		tempHeap.min = tempHeap.last;
		tempHeap.last.parent = null;
		iter = tempHeap.last;
		while(iter.next != tempHeap.last) {
			iter = iter.next;
			iter.parent = null;
			if(iter.item.key < tempHeap.min.item.key) tempHeap.min = iter;
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
		while(item.key < item.node.parent.item.key) {
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
		decreaseKey(item, item.key - 1); // set key -1
		deleteMin();
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		if(heap2.min.item.key < this.min.item.key) {
			this.min = heap2.min;
		}
		HeapNode iter1 = this.last;
		HeapNode iter2 = heap2.last.next;
		heap2.last.next = null;
		while(iter2.next != null) {
			HeapNode current2 = iter2;
			if(iter1.next.rank == iter2.rank) {
				iter2 = iter2.next;
				current2.next = iter1.next; // current2 is about to be linked with iter1. If it's going to be the root it has to point to the next tree
				iter1.next = link(iter1.next, current2);
			}
			else if(iter1.next.rank < iter2.rank) {
				if(iter1.next != this.last)
					iter1 = iter1.next;
				else {
					iter2 = iter2.next;
					current2.next = this.last.next;
					iter1.next.next = iter2;
					this.last = iter2;
				}
			}
			else {
				// insert current2 between iter1 and iter1.next
				iter2 = iter2.next;
				current2.next = iter1.next;
				iter1.next = current2;
			}
		}
	}

	/**
	 * Links two binomial trees of the same degree
	 * Returns the new root of the tree so that the previous tree can be updated to point to it
	 */
	private HeapNode link(HeapNode x, HeapNode y) {
		// We want the lesser number to be the root
		if(x.item.key > y.item.key) {
			HeapNode temp = x;
			x = y;
			y = temp;
		}
		y.next = x.child.next;
		x.child.next = y;
		x.child = y;
		return x;
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return 42; // should be replaced by student code
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return false; // should be replaced by student code
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		return 0; // should be replaced by student code
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
	}

}
