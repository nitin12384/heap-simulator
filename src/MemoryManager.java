import java.util.ArrayList;

// Core class to manage the memory, Doubly Linked List of FreeSpace and ArrayList of Variables
public class MemoryManager extends DLL<FreeSpace> {
	ArrayList<Variable> varList;

	// constructor - Runs once only
	MemoryManager(int nCells) {
		// ---------- Create varList
		varList = new ArrayList<Variable>();
		head = new DLL_Node<FreeSpace>() ;
		// ---------- Initialize DLL	
		reset(nCells);
		
	}

	// -----------------------------------------------
	// ------------------------- PUBLIC METHODS-------
	// -----------------------------------------------

	// Remove all variables
	// Removes all FreeSpace and keep one big FreeSpace containing full memory
	public void reset(int nCells) 
	{
		// ----- create a full cells free space
		FreeSpace fullSpace = new FreeSpace();
		fullSpace.set(0, nCells);

		DLL_Node<FreeSpace> node = new DLL_Node<FreeSpace>();
		node.data = fullSpace;

		// ----- set head manually
		head = new DLL_Node<FreeSpace>() ;
		
		head.next = null;
		head = node;

		// ----- Empty the varList

		varList.clear();

	}

	// Used to print on GUI or console
	public String freeSpace_toString() 
	{
		String out = " Free Blocks List\n";
		DLL_Node<FreeSpace> cur;
		cur = head;
		
		while (cur != null) {
			out += cur.data.toString();
			cur = cur.next;
		}
		return out;
	}

	// Used to print on GUI or console
	public String varList_toString() 
	{
		String out = " Variables List\n";
		
		for (Variable cur : varList) {
			out += cur.toString();
		}
		return out;
	}

	// Exit Codes
	// 0 - Success
	// 1 - Not Enough Space
	// allocated_ref is given the values startAdr, size, endAdr
	// so that we can use that to paint on cell Matrix
	public int allocate(String name, int size, int allocStrat, MemoryBlock allocated_ref) 
	{
		int exitCode = 0;
		boolean success = true;

		// ------- Step 0 : Check is No FreeSpace is present
		if(head == null) {
			success = false ;
			exitCode = 1;
		}
		
		// ------- Step 1 : Find a Target FreeSpace

		DLL_Node<FreeSpace> target = null, leftSpace = null;
		
		
		
		if (success) {
			switch (allocStrat) {
			case 1:
				target = findSpaceFirstFit(size);
				break;
			case 2:
				target = findSpaceBestFit(size);
				break;
			case 3:
				target = findSpaceWorstFit(size);
				break;
			}
		}
		// --------- Step 2 : Check if space is not found
		if (target == null) {
			success = false;
			exitCode = 1;
		}

		if (success) {
			// ----------- Fill allocated_ref so that method caller gets allocation details
			allocated_ref.set(target.data.startAdr, size);
			
			// ------------ Step2b : create a new variable and add to varList
			varList.add(new Variable(name, target.data.startAdr, size)) ;
			
			// ------------Step 3 : partitioning the target space
			
			if (size != target.data.size) {
				// partitioning req.
				leftSpace = new DLL_Node<FreeSpace>();
				leftSpace.data = new FreeSpace();
				
 				leftSpace.data.set(target.data.startAdr + size, target.data.size - size);

				leftSpace = insertAfter(target, leftSpace);
			}

			// ------------- Step 4: Remove tagret space from free list
			remove(target);

			// ------------ Step 5 : Merge check
			if (leftSpace != null)
				mergeAdjacent(leftSpace);
			
		}
		return exitCode;
	}

	// Exit Codes
	// 0 - Success
	// 1 - Variable not found
	// 2 - Can't Insert FreeSpace (Coding Issue)
	// allocated_ref is given the values startAdr, size, endAdr
	// so that we can use that to paint on cell Matrix
	public int free(String name, MemoryBlock freed_ref) 
	{
		int exitCode = 0;
		boolean success = true;

		// ---------- Step 1 : Search in varList
		int varIndex = 0;
		while (varIndex < varList.size() &&  !name.equals(varList.get(varIndex).name) ){ // Order of two expressions around && is important
			varIndex++;
		}

		// ---------- Step 2 : Check if Not found
		if (varIndex == varList.size()) {
			exitCode = 1;
			success = false;
		}

		DLL_Node<FreeSpace> newSpace = null;
		if (success) {
			// ---------- Step 3 : Insert new freespace in the list
			newSpace = insertInOrder(varList.get(varIndex).startAdr, varList.get(varIndex).size);
		}

		// ---------- Step 4 : Check if insertion went well
		if (success && newSpace == null) {
			success = false;
			exitCode = 2;
		}

		if (success) {

			// ----------- Fill freed_ref so that method caller gets allocation details
			freed_ref.set(newSpace.data.startAdr, newSpace.data.size);
			
			
			// ---------- Step 5 : remove Variable
			varList.remove(varIndex);

			// ---------- Step 6 : Check if merge is required
			mergeAdjacent(newSpace);
		}

		return exitCode;
	}

	// ---------------------------------------------------------
	// ------------------------- INTERNAL PRIVATE METHODS-------
	// ---------------------------------------------------------

	// ---- What it does
	// Create a new Node
	// Find a node with data bigger than nodeData
	// Insert node in List
	// ---- What it returns
	// Reference to the inserted Node
	private DLL_Node<FreeSpace> insertInOrder(int startAdr, int size) {

		// ------- Step 1 : Create a New Node
		DLL_Node<FreeSpace> node = new DLL_Node<FreeSpace>();
		node.data = new FreeSpace() ;
		
		node.data.set(startAdr, size);

		// ------- Step 2 : Find out the target Node
		DLL_Node<FreeSpace> target = null;
		DLL_Node<FreeSpace> cur = head;

		// while( cur != null && ((MemoryBlock) cur.data).compare((MemoryBlock)
		// nodeData) != 1){ // short circuit AND necessary target = cur ; cur = cur.next
		// ; }
		// rewrite ---------------------------------------------

		while (cur != null && cur.data.compare(node.data) != 1) {
			target = cur;
			cur = cur.next;
		}

		if (target == null) 
		{
			// insert at start
			// case 1 head is not present already
			if(head == null) {
				head = node ;
			}
			else {
				head.prev = node;
				node.next = head;
				head = node;
			}
			
		} 
		else 
		{
			node.prev = target;
			node.next = target.next;
			if (target.next != null) {
				target.next.prev = node;
			}
			target.next = node;
		}

		return node;
	}

	private void mergeAdjacent(DLL_Node<FreeSpace> target) {
		// function is O(1)

		// check prev
		if (target.prev != null) {
			if (target.prev.data.endAdr + 1 == target.data.startAdr) {
				// prev is adjacent
				// change target... remove prev
				target.data.startAdr = target.prev.data.startAdr;
				target.data.size += target.prev.data.size;
				remove(target.prev);
			}
		}

		// check next
		if (target.next != null) {
			if (target.data.endAdr + 1 == target.next.data.startAdr) {
				// next is adjacent
				// change target... remove next
				target.data.endAdr = target.next.data.endAdr;
				target.data.size += target.next.data.size;
				remove(target.next);
			}
		}

		// function ended
	}

	// returns null if space not found
	private DLL_Node<FreeSpace> findSpaceFirstFit(int size) {
		// traverse till target.size is >= size
		DLL_Node<FreeSpace> target = head;
		
		while( target != null && target.data.size < size ) {
			target = target.next ;
		}
		
		return target;
	}

	private DLL_Node<FreeSpace> findSpaceBestFit(int size) {
		// traverse full list
		// diff = node.size - size
		// find min positive diff.
		
		// diff is set to be max possible
		boolean firstDiffFound = false;
		int diff = 0, cur_diff = 0;
		
		DLL_Node<FreeSpace> target = null;		
		DLL_Node<FreeSpace> cur = head;
		
		while(cur != null) 
		{
			cur_diff = cur.data.size - size ;
			
			if( firstDiffFound) {
				if(cur_diff >= 0 && cur_diff < diff) {
					diff = cur_diff ;
					target = cur ;
				}
			}
			else if(cur_diff >= 0) {
				// first positive(or 0) diff found
				firstDiffFound = true ;
				diff = cur_diff ;
				target = cur ;
				
			}
			
			cur = cur.next ;
		}
		
		
		return target;
	}

	private DLL_Node<FreeSpace> findSpaceWorstFit(int size) {
		// find max sized node
		// check if it satisfies
		DLL_Node<FreeSpace> target = head;
		DLL_Node<FreeSpace> cur = head;
		int max_size = cur.data.size ; // ----- here it use the fact that list is never empty, head is never null
		
		while(cur != null) 
		{
			
			if(cur.data.size > max_size) {
				max_size = cur.data.size ;
				target = cur ;
			}
			
			cur = cur.next;
		}
		
		if(target.data.size < size) {
			// not found
			target = null ;
		}
		
		return target;
	}

}
