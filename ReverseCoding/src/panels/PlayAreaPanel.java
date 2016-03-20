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
import data.Question;
import data.QuestionList;

public class PlayAreaPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel innerPanel;
	public CollapsablePanel panels[];
	
	public QuestionList qlist;
	public String  questionNames[];
	public User client;
	public int selected = -1;
	
	private final String URL = "http://api-base.url";
	private Service refreshService = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build().create(Service.class);
	
	public PlayAreaPanel(Question questions[], User client) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		innerPanel = new JPanel();
		panels = new CollapsablePanel[questions.length];
		questionNames = new String[questions.length];
		this.client = client;
		qlist = new QuestionList(true, questions);
		
		innerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		for (int a = 0; a < questions.length; a++) {
			panels[a] = new CollapsablePanel(questions[a]);
			questionNames[a] = questions[a].name;
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
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
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
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		
		refreshPanel.start();
		checkSelected.start();
	}
}