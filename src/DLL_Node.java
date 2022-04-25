//Used as Node in Doubly Linked List
public class DLL_Node<type> 
{
	public type data;
	public DLL_Node<type> prev;
	public DLL_Node<type> next;
	
	public void reset() {
		data = null;
		prev = null;
		next = null;
	}

}