package panels;

import java.awt.Font;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import network.Service;

import data.User;
import data.Question;
import data.QuestionList;

public class LoginPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel user, pass;
	private JTextField userField;
	private JPasswordField passField;
	private JButton login;
	private ImageIcon logo = new ImageIcon("misc\\logo.png");
	
	private final String URL = "http://api-base.url";
	
	public String name;
	public User client;
	public QuestionList questions;
	public boolean disabled = false;
	public Service loginService = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build().create(Service.class);
	
	public LoginPanel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		user = new JLabel("Username: ");
		pass = new JLabel("Password: ");
		user.setFont(new Font("Callibri", Font.BOLD, 11));
		pass.setFont(new Font("Callibri", Font.BOLD, 11));
		userField = new JTextField(20);
		passField = new JPasswordField(20);
		login = new JButton("Login");
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(new JLabel(logo), gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(user, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(userField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(pass, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		add(passField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 5, 5, 20);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		add(login, gbc);
			
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Post request implemented here
				
				String id = userField.getText();
				String pass = new String(passField.getPassword());
				client = new User(id, pass);
				Question ques[] = new Question[10];
				for (int a = 0; a < 10; a++)
					ques[a] = new Question("Question " + (a + 1), "Unattempted", "-", "-", "-");
				questions = new QuestionList(true, ques);
				name = "Sourish Banerjee";
				disabled = true;
				
				/*String id = userField.getText();
				String pass = new String(passField.getPassword());
				client = new User(id, pass);
				Call <QuestionList> call = loginService.sendInfoRequest(client);
				try {
					questions = call.execute().body();
					name = questions.name;
					disabled = questions.status;
					if(disabled == false)
						JOptionPane.showMessageDialog(null,"The username or password is incorrect!", "Error Message", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,"Could not connect to server! Please try again in some time.", "Error Message", JOptionPane.ERROR_MESSAGE);
				}*/
			}
		});
	}
}