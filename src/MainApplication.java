import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// Main Class - Manages GUI and Memory 
public class MainApplication {

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