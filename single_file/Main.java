
package single_file;

/*

Name - Nitin Kumar
Enrollment No. - BT19CSE071
Assignment 3

------Single File Code for Program------

*/

//-----------------------------IMPORTS

import java.lang.String; // for String
import java.util.ArrayList; // to use ArrayList(Dynamic Array)

// These imports are for GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//------------------------------------------------
// ------------------------- BASIC CLASSES -------
// -----------------------------------------------

// Used as Node in Doubly Linked List
class DLL_Node<type> 
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

// Base Class for Variable and FreeSpace 
// Contains common data members and methods
class MemoryBlock 
{
	public int startAdr;
	public int endAdr; 
	public int size;// size = endAdr - startAdr + 1
	
	public MemoryBlock() {
	
	}
	@Override
	public String toString() {
		String out = "Address : (" + startAdr + " \tto  " + endAdr + "),\tSize : " + size + "\n";
		return out;
	}

	// this == obj2 return 0
	// this > obj2 return 1
	// this < obj2 return -1
	public int compare(MemoryBlock obj2) {
		// comparison based on startAdr
		int ans = 0;
		if (this.startAdr > obj2.startAdr) {
			ans = 1;
		} 
		else if (this.startAdr < obj2.startAdr) {
			ans = -1;
		}
		return ans;
	}

	// size should be >0, startAdr should be >=0
	public void set(int startAdr_Val, int size_Val) {
		size = size_Val;
		startAdr = startAdr_Val;
		endAdr = startAdr + size - 1;
	}
}

// FreeSpace class - Store nothing more than MemoryBlock
class FreeSpace extends MemoryBlock 
{
	@Override
	public String toString() {
		String out = " FreeSpace\t" + super.toString();
		return out;
	}

	// this == obj2 return 0
	// this > obj2 return 1
	// this < obj2 return -1
	// Somehow automatic casting is not working .. had to use this
	public int compare(FreeSpace obj2) {
		int ans = super.compare((MemoryBlock) obj2);
		return ans;
	}
}

// Store name also - apart from MemoryBlock members
class Variable extends MemoryBlock 
{
	public String name;

	public Variable(String nm, int strtAdr, int sz) {
		set(strtAdr, sz);
		name = nm;
	}
	
	public String toString() {
		String out = " Name : " + name + "\t" + super.toString();
		return out;
	}
}

// Basic Doubly Linked List
// Very General methods are defined
class DLL<type> 
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

//------------------------------------------------
//------------------------- CORE CLASSES ---------
//------------------------------------------------

// Core class to manage the memory, Doubly Linked List of FreeSpace and ArrayList of Variables
class MemoryManager extends DLL<FreeSpace> {
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
	public String varList_toString() 
	{
		String out = " Variables List\n";
		
		for (Variable cur : varList) {
			out += cur.toString();
		}
		return out;
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

// extended class of panel to show boundaries
// and window size update on console
@SuppressWarnings("serial")
class MyJPanel extends JPanel{
	
	// constructors
	public MyJPanel(LayoutManager layout) {
		super(layout);
	}
	
	public MyJPanel() {
		super();
	}

	// method is overridden to draw rectangle boundary
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
		
		int h = getHeight();
		int w = getWidth();
		System.out.println("Panel Height : " + h + " Width : " + w );
		
		// draw panel boundary.
		g.drawRect(2,2,w-4, h-4);
	}
}



// Main Class - Manages GUI and Memory 
class MainApplication {

	// --- Final Constants 
	final Color freeCellColor1 = new Color(119, 199, 117); //light final Color
	final Color freeCellColor2 = new Color(104, 176, 102);
	final Color filledCellColor1 = new Color(255, 80, 80); // light final Color
	final Color filledCellColor2 = new Color(199, 64, 64);
	final Color cellBorderColor = new Color(200, 200, 200);

	final int COL_MULT = 10 ;
	
	// For memory cell matrix
	int nCells;
	int nCellRows;
	int nCellCols;
	int cellSize;

	int allocStrat; // 1 for first fit, 2 for best fit, 3 for worst fit

	// DLL of FreeSpace and ArrayList of variables
	MemoryManager memoryManager;
	
	// UI - Variables
	JFrame mainFrame ;
	MyJPanel cellPanel, controlPanel, inpPanel, outPanel ;
	
	JPanel  inpFieldPanel, outFieldPanel;
	
	JButton cells[] ;
	
	JButton freeAll,setNCells ;
	JRadioButton firstFit, bestFit, worstFit ;
	ButtonGroup stratGroup ;
	
	JTextField nCellsField ;
	JTextField inpField ;
	JTextField outField ;
	
	JTextArea varArea ;
	JTextArea freeSpaceArea ;
	
	JScrollPane varPane ;
	JScrollPane freeSpacePane ;
	
	// Action Listeners
	ActionListener 	freeAll_Listener,
					setNCells_Listener, allocStrat_Listener,
					inpField_Listener;
	
	// To initialize Components
	// Only memory allocation happens here
	public void init(int nCells_val)
	{
		nCells = nCells_val ;
		
		memoryManager = new MemoryManager(nCells_val);
		
		mainFrame = new JFrame();
		
		cellPanel 		= new MyJPanel();
		controlPanel 	= new MyJPanel();
		outPanel 		= new MyJPanel();
		inpPanel 		= new MyJPanel();
		
		inpFieldPanel	= new JPanel();
		outFieldPanel	= new JPanel();
		
		cells 		= new JButton[nCells] ;
		
		freeAll 	= new JButton("Free All");
		setNCells 	= new JButton("Reset With this nCell.");
		
		firstFit	= new JRadioButton("First Fit");
		bestFit		= new JRadioButton("Best Fit");
		worstFit 	= new JRadioButton("Worst Fit");
		
		stratGroup 	= new ButtonGroup() ;
		
		nCellsField = new JTextField();
		inpField 	= new JTextField("(Input Here... Example \"allocate a 20\" , \"free a\")");
		outField 	= new JTextField("(Output)");
		
		varArea 		= new JTextArea(" Varaibles List\n");
		freeSpaceArea 	= new JTextArea(" Free Blocks List\n");
		
		varPane 		= new JScrollPane(varArea);
		freeSpacePane 	= new JScrollPane(freeSpaceArea) ;
	}
	
	public void start() {
		draw();
		createActionListeners();
		linkActionListeners();
	}
	
	// Initialization function
	// Draws Frame, and all components
	private void draw() 
	{
		// ---------- Step 1 : Draw mainFrame
		mainFrame.setLayout( new GridLayout(3,1,5,5)) ;
		
		mainFrame.setTitle("Heap Management Simulation");
		mainFrame.setSize(800,600);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// ---------- Step 2 : Add Basic Skeleton Panels
		mainFrame.add(cellPanel);
		mainFrame.add(inpPanel);
		mainFrame.add(outPanel);
		
		inpPanel.setLayout( new GridLayout(3,1,1,1));
		outPanel.setLayout(new GridLayout(1,2,1,1));
		
		// ---------- Step 3 : Draw memory Cell Matrix
		drawCellMatrix();
		
		// ---------- Step 4 : Draw controlPanel
		// ---------- Step 4a : Radio Button Group
		stratGroup.add(firstFit);
		stratGroup.add(bestFit);
		stratGroup.add(worstFit);
		
		firstFit.setSelected(true);
		allocStrat = 1 ; // first fit
		
		// ---------- Step 4b : Finalize ControlPanel
		nCellsField.setColumns(10);
		
		controlPanel.add(freeAll);
		controlPanel.add(firstFit);
		controlPanel.add(bestFit);
		controlPanel.add(worstFit);
		controlPanel.add(nCellsField);
		controlPanel.add(setNCells);
		
		// ---------- Step 4b : Draw ControlPanel on inpPanel
		
		inpPanel.add(controlPanel) ;
	
		// ---------- Step 5 : Draw inpField and outField on inpPanel
		inpFieldPanel.setLayout(new BorderLayout());
		outFieldPanel.setLayout(new BorderLayout());
		inpFieldPanel.add(inpField, BorderLayout.NORTH) ;
		outFieldPanel.add(outField, BorderLayout.NORTH) ;
		
		outField.setEditable(false);
		
		inpPanel.add(inpFieldPanel);
		inpPanel.add(outFieldPanel);
		
		// ---------- Step 6 : Draw varList and FreeSpace List on outPanel	
		
		freeSpaceArea.setText(memoryManager.freeSpace_toString());
		varArea.setEditable(false);
		freeSpaceArea.setEditable(false);
		outPanel.add(varPane, BorderLayout.CENTER);
		outPanel.add(freeSpacePane, BorderLayout.CENTER);
		
		
		
	}

	// Draw the cell matrix manually
	// Draws like it is reseted. Green color only.
	private void drawCellMatrix() {
		int w = cellPanel.getWidth() ;
		int h = cellPanel.getHeight() ;
		int sz_temp;
		
		sz_temp = (int) Math.sqrt((h*w)/nCells) ;
		nCellCols = COL_MULT * ( 1 + w/( COL_MULT*(sz_temp - 1) ) ) ;
		cellSize = w / nCellCols ;
		nCellRows = (int) Math.ceil( nCells/nCellCols) ;
		
		// ---------- Step 4 : Draw Cells
		cellPanel.removeAll();
		//mainFrame.remove(cellPanel);
		cellPanel.setLayout( new GridLayout(10, nCellCols) );	// ----------------- This value Works best
		
		for(int i=0; i<nCells; i++) {
			// -------- Allocate memory
			cells[i] = new JButton() ;
			
			// -------- Set Color
			//cells[i].setBorder(null);
			
			if(i % COL_MULT == 0) {
				cells[i].setBackground(freeCellColor2);
			}
			else {
				cells[i].setBackground(freeCellColor1);
			}
			
			// ------- Set Size
			//cells[i].setSize( new Dimension(cellSize, cellSize) );
			
			// ------- Add to panel
			cellPanel.add(cells[i]) ;
			
		}
		
		//mainFrame.add(cellPanel);
	}
	
	
	private void allocateAndPaint(String name, int size) {
		// ----- Step 1 : try to allocate 
		MemoryBlock allocate_ref = new MemoryBlock();
		
		int exit_code;
		if(size >= 3) {
			// size should be at least 3 ( 1 byte metadata, 1 byte other data, 1 byte at least for allocation
			exit_code = memoryManager.allocate(name, size, allocStrat, allocate_ref);
		}
		else {
			exit_code = 2 ;
		}
		
		
		if(exit_code == 0) {
			// success
			// ------- Do the paint
			// from startAdr to endAdr
			// there are at least 3 cells to paint everytime  
			
			// metadata and other data cell color
			cells[allocate_ref.startAdr].setBackground(filledCellColor2);
			cells[allocate_ref.startAdr+1].setBackground(filledCellColor2);
			
			// allocated data cell color
			for(int i = allocate_ref.startAdr + 2; i<= allocate_ref.endAdr; i++) {
				cells[i].setBackground(filledCellColor1);
			}
			
			// ------ Write the output
			outField.setText("Variable \'" + name + "\' allocated. (2 bytes extra space for metadata, etc.)");
			varArea.setText(memoryManager.varList_toString());
			freeSpaceArea.setText(memoryManager.freeSpace_toString());
			
		}
		else if(exit_code == 1) {
			outField.setText("Not Enough Space on Heap. Can't Allocate.");
		}
		else if(exit_code == 2) {
			outField.setText("Invalid Size given.");
		}
		else {
			outField.setText("Unknown error in allocation");
		}
		
	}
	
	private void freeAndPaint(String name) {
		// ----- Step 1 : try to free
		MemoryBlock freed_ref = new MemoryBlock();
		int exit_code = memoryManager.free(name, freed_ref);

		if (exit_code == 0) {
			// success
			// ------- Do the paint
			// from startAdr to endAdr
			
			for (int i = freed_ref.startAdr; i <= freed_ref.endAdr; i++) {
				if(i % COL_MULT == 0) {
					cells[i].setBackground(freeCellColor2);
				}
				else {
					cells[i].setBackground(freeCellColor1);
				}
				
			}

			// ------ Write the output
			outField.setText("Variable \'" + name + "\' freed ");
			varArea.setText(memoryManager.varList_toString());
			freeSpaceArea.setText(memoryManager.freeSpace_toString());

		} else if (exit_code == 1) {
			outField.setText("Invalid name of vairable. Can't Find.");
		} else {
			outField.setText("Unknown error in allocation.Can't Insert FreeSpace");
		}
	}
	
	private void createActionListeners()
	{
		freeAll_Listener = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				/*memoryManager.reset(nCells);
				
				drawCellMatrix();
	
				outField.setText("Memory Reseted successfully...");
				varArea.setText(memoryManager.varList_toString());
				freeSpaceArea.setText(memoryManager.freeSpace_toString());
				*/
				
				/*
				nCellsField.setText( String.valueOf(nCells) );
				setNCells.doClick();
				
				outField.setText("(Output)");
				*/
				
				int l = memoryManager.varList.size(); // Because it will change dynamically
					
				for(int i=l-1; i>=0; i--) {
					freeAndPaint(memoryManager.varList.get(i).name);
				}
				outField.setText("All Variable Freed.");
			}
		};
		
		setNCells_Listener = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				
				boolean success = true ;
				
				try {
					nCells = Integer.parseInt( nCellsField.getText() ) ;
				} 
				catch (java.lang.NumberFormatException e) {
					outField.setText("Invalid arguement as number of cells . Enter an Integer");
					success = false ;
				} 
				
				nCellsField.setText("");
				
				
				if(nCells <= 0 || nCells > 2000 ) {
					outField.setText("Invalid number of Cells. Should be in [1,2000]");
					success = false ;
				}
				// redraw the frame
				
				if (success) 
				{
					mainFrame.setVisible(false);
					mainFrame.dispose();
					init(nCells);
					start();
					outField.setText("Number of cells set to " + nCells + " successfully.");
					// Memory Manager
					memoryManager.reset(nCells);
					// redraw cells
					drawCellMatrix();
				}
				
			}
		};
		
		inpField_Listener = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				// ----- Step 1 : Take the input
				String inp = inpField.getText();
				inpField.setText("");
				
				// ----- Step 2 : parse the input string
				// allocate <name> <size> | free <name>
				boolean error = false ;
				
				String words[] = inp.split(" ") ;
				
				if(words.length == 2) {
					// should be a free <var_name>
					if(words[0].equals("free") || words[0].equals("f") ) {
						freeAndPaint(words[1]);
					}
					else {
						error = true;
					}
				}
				else if(words.length == 3) {
					// should be allocate call
					
					if(words[0].equals("allocate") || words[0].equals("a") ) {
						allocateAndPaint(words[1], Integer.parseInt(words[2]) + 2); // 2 bytes for metadata, and other fields
					}
					else {
						error = true ;
					}
				}
				else {
					error = true ;
				}
				
				if(error) {
					outField.setText("Invalid command in Input.(Valid Examples : \"allocate a 20\" , \"free a\")");
				}
				
				
			}
		};
		
		allocStrat_Listener = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				if( (JRadioButton)ae.getSource() == firstFit ) {
					allocStrat = 1;
				}
				else if((JRadioButton)ae.getSource() == bestFit) {
					allocStrat = 2;
				}
				else {
					allocStrat = 3;
				}
				
				outField.setText("Allocation Strategy Changed Successfully to " + allocStrat);
			}
		};
	}

	private void linkActionListeners()
	{
		freeAll.addActionListener(freeAll_Listener);
		setNCells.addActionListener(setNCells_Listener);
		nCellsField.addActionListener(setNCells_Listener);
		
		inpField.addActionListener(inpField_Listener);
		
		firstFit.addActionListener(allocStrat_Listener);
		bestFit.addActionListener(allocStrat_Listener);
		worstFit.addActionListener(allocStrat_Listener);
	}
}


// Entry point
public class Main{

	public static void main(String[] args) 
	{
		System.out.print("Start of main() \n\n");
		// entry point of program
		SwingUtilities.invokeLater(new Runnable() 
		{
			// 
			public void run() {
				MainApplication app = new MainApplication();
		
				app.init(500);
				app.start() ;
			}
		});
		
		
		

	}
}
