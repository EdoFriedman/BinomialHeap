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
		size -= Math.pow(2,min.rank);

		// We removed the old minimum's tree from the heap, so we need to meld its children back
		BinomialHeap tempHeap = new BinomialHeap();
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
		return; // should be replaced by student code
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{    
		return; // should be replaced by student code
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		return; // should be replaced by student code   		
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
