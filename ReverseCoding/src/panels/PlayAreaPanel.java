package panels;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import network.Service;

import data.User;
import data.QuestionList;

/**
 * <h1>PlayAreaPanel.class</h1>
 * 
 * <p>
 * Displays the list of active questions 
 * for the user logged in. Each question is in the 
 * form of a collapsable panel.
 * 
 * @author Sourish Banerjee
 */
public class PlayAreaPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JPanel innerPanel; // Container panel for the collapsable panels for all the questions.
	
	/**Each panel corresponds to a particular question.*/
	public CollapsablePanel panels[];
	
	/**Contains info pertaining to all the questions.*/
	public QuestionList qlist;
	/**Stores the names of all the questions.*/
	public String  questionNames[];
	/**Contains info pertaining to the user logged in.*/
	public User client;
	/**
	 * Stores the index of the question for which the 
	 * attempt button is pressed (if any).
	 */
	public int selected = -1;
	
	private final String URL = "http://api-base.url";
	private Service refreshService = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build().create(Service.class); // Retrofit client to handle server requests.
	
	/**
	 * Creates the play area panel using info of 
	 * the user and questions passed to it.
	 * 
	 * @param questions Info pertaining to all 
	 * the questions of the user logged in.
	 * @param client Contains info pertaining 
	 * to the user logged in.
	 */
	public PlayAreaPanel(QuestionList questions, User client) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
			+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		innerPanel = new JPanel();
		panels = new CollapsablePanel[questions.questions.length];
		questionNames = new String[questions.questions.length];
		this.client = client;
		qlist = new QuestionList(true, questions.name , questions.questions);
		
		innerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		for (int a = 0; a < questions.questions.length; a++) {
			panels[a] = new CollapsablePanel(questions.questions[a]);
			questionNames[a] = questions.questions[a].name;
			if (a == (panels.length - 1))
				break;
			innerPanel.add(panels[a], gbc);
			gbc.gridy++;
		}
		
		gbc.weighty = 0.1;
		innerPanel.add(panels[panels.length - 1], gbc);
		
		this.setLayout(new BorderLayout());
		this.add(innerPanel, BorderLayout.CENTER);
		
		maintain();
	}
	
	/**
	 * Performs maintenance operations on the play area panel. 
	 * Comprises of two threads:
	 * 
	 * <br> <br>
	 * 1. refreshPanel: Continuously updates info for all the 
	 * questions in the active play area with that received 
	 * from the server.
	 * <br>
	 * 2. checkSelected: Checks if the 'Attempt' button has 
	 * been pressed for any question. If so, notes the 
	 * question attempted.
	 */
	public void maintain() {
		Thread refreshPanel = new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						Call <QuestionList> call = refreshService.sendInfoRequest(client);
						qlist = call.execute().body();
						for(int a=0; a < questionNames.length; a++) {
							panels[a].refreshPanel(qlist.questions[a]);
							questionNames[a] = qlist.questions[a].name;
						}
					} catch (Exception e1) {};
					try{
						Thread.sleep(300000);
					} catch (InterruptedException e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
						+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		
		Thread checkSelected = new Thread(new Runnable() {
			public void run() {
				while(true) {
					for(int a=0; a < panels.length; a++) {
						if(panels[a].access == true) {
							selected = a;
							break;
						}
					}
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
		
		//Uncomment line 160 to make program work with the server.
		//refreshPanel.start();
		checkSelected.start();
	}
}