package panels;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import data.User;
import data.QuestionList;

/**
 * <h1>MainPanel.class</h1>
 * 
 * <p>
 * Container for the play area panel, dash board and 
 * leader board.
 * 
 * @author Sourish Banerjee
 */
public class MainPanel extends JTabbedPane {
	private static final long serialVersionUID = 1L;
			
	/**Play area panel to be added to the main panel.*/
	public PlayAreaPanel p1;
	/**Dash board panel to be added to the main panel.*/
	public DashboardPanel p2;
	/**Leader board panel to be added to the main panel.*/
	public LeaderboardPanel p3;
	
	/**
	 * Stores the truth value of the activeness of the 
	 * main panel (in the main frame).
	 */
	public boolean disabled = false;
	
	/**
	 * Creates the main panel by creating objects of 
	 * play area panel, dash board, leader board and 
	 * adding them to it.
	 * 
	 * @param questions Info pertaining to all 
	 * the questions of the user logged in.
	 * @param client Contains info pertaining 
	 * to the user logged in.
	 */
	public MainPanel(QuestionList questions, User client) {
		p1 = new PlayAreaPanel(questions, client);
		p2 = new DashboardPanel();	
		p3 = new LeaderboardPanel();
		add("Play Area", p1);
		add("Dashboard", p2);
		add("Leaderboard", p3);
		maintain();
	}
	
	/**
	 * Sets the value of disabled to true if the 
	 * attempt button has been pressed for any 
	 * question in the play area panel.
	 */
	public void maintain() {
		Thread checkSelected = new Thread(new Runnable(){
			public void run()
			{
				while(true)
				{
					if(p1.selected != -1)
						disabled = true;
					try {
						Thread.sleep(100);
					} catch(InterruptedException e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
						+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		
		checkSelected.start();
	}
}