// Basic Doubly Linked List
// Very General methods are defined
public class DLL<type> 
{
	protected DLL_Node<type> head;

	public DLL_Node<type> getHead() {
		return head;
	}

	public void insertAtStart(type nodeData) {
		// ------- Step 1 : Create a New Node
		DLL_Node<type> node = new DLL_Node<type>();
		node.data = nodeData;

		// ------- Step 2 :insert at start
		// null - head = head.next ...
		// null - node = head = ...
		head.prev = node;
		node.next = head;
		head = node;

	}

	// returns node which was added
	public DLL_Node<type> insertAfter(DLL_Node<type> target, DLL_Node<type> node) {
		// corner cases
		// target = null

		if (target == null) {
			head = node;
		} 
		else {
			node.next = target.next;
			if (target.next != null) {
				target.next.prev = node;
			}
			node.prev = target;
			target.next = node;
		}

		return node;
	}

	// remove target from list
	public void remove(DLL_Node<type> target) {
		if(target == head) 
		{
			if(head.next == null) {
				// should rarely happen though
				System.out.print("Free List Actually empty.");
			}
			
			head = head.next ;
			
		}
		else if(target.prev == null) {

			// should never happen though
			System.out.print("Free List . prev null for non-head node.");
		}
		else 
		{
			// target.next = null means target is list's last item
			if(target.next == null) {
				(target.prev).next = null ;
			}
			else {
				// target is well in the middle
				(target.prev).next = target.next ;
				(target.next).prev = target.prev ;
			}
		}
	}

}
