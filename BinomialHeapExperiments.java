import java.util.*;

/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeapExperiments
{
	public int size;
	public HeapNode last;
	public HeapNode min;

	public static void main(String[] args) {
		for(int i = 1; i <= 5; i++) {
			System.out.printf("i = %d\n", i);
			int n = (int) Math.pow(3, i + 5) - 1;
			System.out.printf("n = %d\n\n", n);
			int totalLinks = 0;
			int deletedNodeDegreeSum = 0;
			int numberOfTreesAtEnd;

			List<Integer> keys = new ArrayList<>(n);
			for(int j = 1; j <= n; j++) {
				keys.add(j);
			}
			// Experiment 2
//			Collections.shuffle(keysList);
			// Experiment 3
//			Collections.reverse(keys);


			long t0 = System.nanoTime();

			BinomialHeapExperiments heap = new BinomialHeapExperiments();
			for(int key : keys) {
				totalLinks += heap.insert(key, "");
			}
			// Experiment 2
//			for(int j = 0; j < n/2; j++) {
//				Pair res = heap.deleteMin();
//				totalLinks += res.getFirst();
//				deletedNodeDegreeSum += res.getSecond();
//			}
			// Experiment 3
//			while(heap.size() > Math.pow(2, 5) - 1) {
//				Pair res = heap.deleteMin();
//				totalLinks += res.getFirst();
//				deletedNodeDegreeSum += res.getSecond();
//			}

			numberOfTreesAtEnd = heap.numTrees();
			long t1 = System.nanoTime();
			System.out.printf("Elapsed time: %.1fms\n", (t1 - t0) / 1000000.0);
			System.out.printf("Total links: %d\n", totalLinks);
			System.out.printf("Number of trees at the end: %d\n", numberOfTreesAtEnd);
			System.out.printf("Sum of deleted nodes' degrees: %d\n", deletedNodeDegreeSum);
			System.out.print("\n------------------------------------------------------\n");
		}
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the link count.
	 *
	 */
	public int insert(int key, String info)
	{
        HeapItem item = new HeapItem(key, info);
        HeapNode node = new HeapNode(item, null, null, null, 0);
		node.next = node;
        BinomialHeapExperiments tempHeap = new BinomialHeapExperiments();
        tempHeap.size = 1;
        tempHeap.last = node;
        tempHeap.min = node;
        return this.meld(tempHeap);
	}

	/**
	 * 
	 * Delete the minimal item
	 * Returns a pair containing the number of links and the deleted node's degree
	 *
	 */
	public Pair deleteMin()
	{
		if(this.min == null) return new Pair(0, 0);
		HeapNode min = this.min;
		int degree = min.rank;
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
		BinomialHeapExperiments tempHeap = new BinomialHeapExperiments();
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
		int linkCount = this.meld(tempHeap);
		return new Pair(linkCount, degree);
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
	 * Returns the number of links
	 *
	 */
	public int meld(BinomialHeapExperiments heap2)
	{
		if(heap2.empty()) return 0;
		this.size += heap2.size;
		if(this.last == null) {
			this.last = heap2.last;
			this.min = heap2.min;
			return 0;
		}
		HeapNode iter1 = this.last;
		HeapNode iter2 = heap2.last.next;
		heap2.last.next = null;
		HeapNode carry = null;
		int linkCount = 0;
		int maxRank = Math.max(this.last.rank, heap2.last.rank) + 1;
		for(int rank = 0; rank <= maxRank; rank++) {
			if(iter1.next != null && iter1.next.rank < rank && (iter1.next.next.rank >= rank || iter1.rank < iter1.next.rank)) iter1 = iter1.next;
			if(iter2 != null && iter2.rank < rank) iter2 = iter2.next;
			HeapNode current2 = iter2;
			HeapNode resNoCarry = null;
			HeapNode nextCarry = null;
			if(iter1.next != null && iter1.next.rank == rank && iter2 != null && iter2.rank == rank) {
				iter2 = iter2.next;
				current2.next = null;
				nextCarry = link(removeNext(iter1), current2);
				linkCount++;
			}
			else if(iter1.next != null && iter1.next.rank == rank) {
				resNoCarry = removeNext(iter1);
			}
			else if(iter2 != null && iter2.rank == rank) {
				iter2 = iter2.next;
				current2.next = null;
				resNoCarry = current2;
			}
			if(carry != null) {
				if(resNoCarry == null) {
					resNoCarry = carry;
				}
				else {
					nextCarry = link(carry, resNoCarry);
					linkCount++;
					resNoCarry = null;
				}
			}
			if(resNoCarry != null) {
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
				if(resNoCarry.item.key <= min.item.key) {
					min = resNoCarry;
				}
			}
			carry = nextCarry;
		}
		return linkCount;
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
	public static class Pair {
		private final int first;
		private final int second;

		public Pair(int first, int second) {
			this.first = first;
			this.second = second;
		}

		public int getFirst() {
			return first;
		}

		public int getSecond() {
			return second;
		}
	}
}
