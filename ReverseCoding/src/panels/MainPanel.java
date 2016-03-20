package panels;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import data.User;
import data.Question;

public class MainPanel extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
			

	public PlayAreaPanel p1;
	private DashboardPanel p2;
	private LeaderboardPanel p3;
	
	public boolean disabled = false;
	
	public MainPanel(Question questions[], User client) {
		p1 = new PlayAreaPanel(questions, client);
		p2 = new DashboardPanel();	
		p3 = new LeaderboardPanel();
		add("Play Area", p1);
		add("Dashboard", p2);
		add("Leaderboard", p3);
		
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
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		
		checkSelected.start();
	}
}