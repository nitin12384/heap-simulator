import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

// extended class of panel to show boundaries
// and window size update on console
@SuppressWarnings("serial")
public class MyJPanel extends JPanel{
	
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
