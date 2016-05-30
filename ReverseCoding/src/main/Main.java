package main;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicMenuBarUI;

import data.User;
import data.QuestionList;

import panels.MainPanel;
import panels.LoginPanel;
import panels.ExeExecutorPanel;

/**
 * <h1>Main.class</h1>
 * 
 * <p>
 * Brings all elements of the application together.
 * 
 * @author Sourish Banerjee
 */
public class Main {
	/*
	 * Login panel, main panel and executor panel 
	 * to be added to the main frame.
	 */
	private static LoginPanel login;
	private static MainPanel mpanel;
	private static ExeExecutorPanel executor;
	
	/*The main frame.*/
	private static JFrame frame;
	
	/*Name of user logged in.*/
	private static String name;
	
	/*
	 * Variables whose truth values determine 
	 * which panel is displayed on the main 
	 * frame at any given point of time.
	 */
	private static boolean loginDisabled = false;
	private static boolean mainDisabled = true;
	private static boolean executorDisabled = true;
	
	/**
	 * Adds the logout bar to the main frame.
	 * 
	 * @param name Name of user logged in.
	 */
	public static void addLogoutBar(String name) {
		JMenuBar logoutBar = new JMenuBar();
		JMenu logoutMenu = new JMenu("          Logout          ");
		JMenuItem logout = new JMenuItem("Logout       ");
		JMenuItem exit = new JMenuItem("Exit");
		JLabel username = new JLabel(" " + name);
		ImageIcon userLogo = new ImageIcon("misc//user.png");
		
		logoutMenu.setFont(new Font("Callibri", Font.BOLD, 11));
		username.setFont(new Font("Callibri", Font.BOLD, 11));
		
		logoutBar.setUI(new BasicMenuBarUI() {
		    public void paint(Graphics g, JComponent c) {
		       g.setColor (new Color(219, 219, 219));
		       g.fillRect (0, 0, c.getWidth (), c.getHeight ());
		    }
		});
		
		logoutBar.add(Box.createRigidArea(new Dimension(5, 25)));
		logoutBar.add(new JLabel(userLogo));
		logoutBar.add(username);
		logoutBar.add(Box.createRigidArea(new Dimension(100, 25)));
		logoutBar.add(Box.createHorizontalGlue());
		logoutMenu.add(logout);
		logoutMenu.add(exit);
		logoutBar.add(logoutMenu);
		frame.setJMenuBar(logoutBar);
		
		/*
		 * Performs maintenance work and logs out the 
		 * user if the logout menu item is selected.
		 */
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(executorDisabled == false)
					executor.process.destroy();
				loginDisabled = false;
				mainDisabled = true;
				executorDisabled = true;
				frame.setJMenuBar(null);
				addLoginPanel();
			}
		});
		
		/*
		 * Performs maintenance work and terminates the 
		 * application if the exit menu item is selected.
		 */
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(executorDisabled == false)
					executor.process.destroy();
				System.exit(0);
			}
		});
	}
	
	/**
	 * Adds the login panel to the main frame.
	 */
	public static void addLoginPanel() {
		login = new LoginPanel();
		frame.getContentPane().removeAll();
		frame.add(new JScrollPane(login), BorderLayout.CENTER);
		frame.validate();
	}
	
	/**
	 * Adds the main panel (and logout bar) to 
	 * the main frame.
	 * 
	 * @param questions Info pertaining to all 
	 * the questions of the user logged in.
	 * @param client Info pertaining to the 
	 * user logged in.
	 * @param caller 0: Called from login panel.
	 * <br>
	 * 1: Called from executor panel.
	 */
	public static void addMainPanel(QuestionList questions, User client, int caller) {
		if(caller == 0)
		{
			/*Initializes main panel if call is from logout panel.*/
			mpanel = new MainPanel(questions, client);
			name = questions.name;
		}
		else
		{
			/*
			 * Resets selected state of question attempted if call 
			 * is from executable panel.
			 */
			mpanel.p1.panels[mpanel.p1.selected].access = false;
			mpanel.p1.selected = -1;
			mpanel.disabled = false;
		}
		frame.getContentPane().removeAll();
		addLogoutBar(name);
		frame.add(new JScrollPane(mpanel), BorderLayout.CENTER);
		frame.validate();
	}
	
	/**
	 * Adds the executor panel (and logout 
	 * bar) to the main frame.
	 * 
	 * @param file Path of executable file.
	 * @param user Name of user logged in.
	 */
	public static void addExecutorPanel(String file, String user) {
		executor = new ExeExecutorPanel(file, user);
		frame.getContentPane().removeAll();
		addLogoutBar(name);
		frame.add(new JScrollPane(executor), BorderLayout.CENTER);
		frame.validate();
	}
	
	/**
	 * Starts the main loop thread which determines 
	 * which panel to be displayed on the main 
	 * frame at any given point of time based on
	 * user actions.
	 */
	public static void start() {
		Thread mainLoop = new Thread(new Runnable() {
			public void run() {
				while(true) {
					if(loginDisabled == false) {
						loginDisabled = login.disabled;
						if(loginDisabled == true) {
							addMainPanel(login.questions, login.client, 0);
							mainDisabled = false;
						}
					}
					else if(mainDisabled == false) {
						mainDisabled = mpanel.disabled;
						if(mainDisabled == true) {
							addExecutorPanel("misc\\" + mpanel.p1.questionNames[mpanel.p1.selected] 
							+ ".exe", mpanel.p1.client.id);
							executorDisabled = false;
						}
					} 
					else {
						executorDisabled = executor.disabled;
						if(executorDisabled == true) {
							addMainPanel(mpanel.p1.qlist, mpanel.p1.client, 1);
							mainDisabled = false;
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
						+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		
		mainLoop.start();
	}
	
	/**
	 * Creates the main frame and calls start() 
	 * to start the main loop thread.
	 * 
	 * @param args This argument is not used.
	 */
	public static void main(String args[]) {
		frame = new JFrame("Reverse Coding");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
			+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLayout(new BorderLayout());
		addLoginPanel();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
				if(executorDisabled == false)
					executor.process.destroy();
		    }
		});
		
		start();
	}
}