import javax.swing.SwingUtilities;

/*
Name - Nitin Kumar
Enrollment No. - BT19CSE071
Assignment 3

------Single File Code for Program------

*/

public class StartApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
