package panels;

import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.UIManager;

import javax.swing.JOptionPane;

//import javax.imageio.*;
//import java.awt.image.BufferedImage;

import data.Question;

/**
 * <h1>CollapsablePanel.class</h1>
 * 
 * <p>
 * Creates a template panel for the questions 
 * in the Play Area. It is composed of two separate panels:
 * 
 * <br> <br>
 * 1. HeaderPanel: Displays the title of the question.
 * <br>
 * 2. ContentPanel: Displays info about the question 
 * (can be collapsed).
 * 
 * @author Sourish Banerjee
 */
public class CollapsablePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private boolean selected; // Indicates the state (collapsed or expanded) of the content panel.
	
	/*Components of the collapsable panel.*/
	private HeaderPanel headerPanel;
	private ContentPanel contentPanel;
	
	/**Stores whether the attempt button has been pressed for the particular question or not.*/
	public boolean access = false;
	
	/**
	 * <h1>HeaderPanel.class</h1>
	 * 
	 * <p>
	 * Displays the title of the question and 
	 * toggles the state (collapsed or expanded) of the content panel associated 
	 * with it on mouse click.
	 * 
	 * @author Sourish Banerjee
	 */
	public class HeaderPanel extends JPanel implements MouseListener {
		private static final long serialVersionUID = 1L;

		private Font font;
		private String text;
		//private BufferedImage open, closed;
		private final int OFFSET = 30; // PAD = 5;
		
		/**
		 * Creates a new HeaderPanel with the text 
		 * passed to it as the title.
		 * 
		 * @param text Title of the question that 
		 * needs to be displayed.
		 */
		public HeaderPanel(String text) {
			addMouseListener(this);
			this.text = text;
			font = new Font("Callibri", Font.BOLD, 12);
			//setRequestFocusEnabled(true);
			setPreferredSize(new Dimension(200, 30));
			/*int w = getWidth();
            int h = getHeight();
 			
           	try {
               	open = ImageIO.read(new File("a1.png"));
               	closed = ImageIO.read(new File("a2.png"));
           	} catch (IOException e) {
               	e.printStackTrace();
           	}*/
		}

		/**
		 * Handles stylistic details of the header 
		 * panel implementation and draws the title 
		 * of the question onto it.
		 */
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color color1 = getBackground();
			Color color2 = color1.darker();
			int w = getWidth();
			int h = getHeight();
			GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
			g2.setPaint(gp);
			g2.fillRect(0, 0, w, h);
			/*if (selected)
       			g2.drawImage(open, PAD, 0, h, h, this);
   			else
       			g2.drawImage(closed, PAD, 0, h, h, this);*/
			g2.setFont(font);
			FontRenderContext frc = g2.getFontRenderContext();
			LineMetrics lm = font.getLineMetrics(text, frc);
			float height = lm.getAscent() + lm.getDescent();
			float x = OFFSET;
			float y = (h + height) / 2 - lm.getDescent();
			g2.setPaint(Color.BLACK);
			g2.drawString(text, x, y);
		}

		/**
		 * Whenever the header panel is clicked, calls 
		 * the toggleSelection() method to reverse the 
		 * state (collapsed or expanded) of the content 
		 * panel associated with it.
		 */
		public void mouseClicked(MouseEvent e) {
			toggleSelection();
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}
	}
 
	/**
	 *<h1>ContentPanel.class</h1>
	 *
	 *<p>
	 *Displays the details pertaining to a 
	 *particular question. Details include:
	 *<br> <br>
	 *Status of question: Attempted or not attempted.
	 *<br>
	 *Marked: Whether the solution has been marked or not.
	 *<br>
	 *Marks Obtained: Marks alloted for the solution.
	 *<br>
	 *Marked By: Name of evaluator.
	 *<br> <br>
	 *It also contains an 'Attempt' button which takes 
	 *the user into the executor panel for the respective 
	 *question.
	 *
	 * @author Sourish Banerjee
	 */
	public class ContentPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel status, marked, marksObtained, markedBy;
		private JButton attempt;
		
		/**
		 * Creates a content panel with the details of the 
		 * question obtained from the question object 
		 * passed to it.
		 * 
		 * @param obj Contains data pertaining to the 
		 * question with which the content panel is 
		 * associated.
		 */
		public ContentPanel(Question obj) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e1) {
				JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
				+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			status = new JLabel("Status: " + obj.status);
			marked = new JLabel("Marked: " + obj.marked);
			marksObtained = new JLabel("Marks Obtained: " + obj.marksObtained);
			markedBy = new JLabel("Marked By: " + obj.markedBy);
			attempt = new JButton("Attempt");

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(2, 1, 2, 1);
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridx = 0;
			gbc.gridy = 0;
			setLayout(new GridBagLayout());
			add(status, gbc);
			gbc.gridx = 1;
			add(marked, gbc);
			gbc.gridx = 2;
			add(marksObtained, gbc);
			gbc.gridx = 3;
			add(markedBy, gbc);
			gbc.gridx = 4;
			add(attempt, gbc);

			/*Sets access to true if the 'Attempt' button is pressed*/
			attempt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					access = true;
				}
			});
		}
	}

	/**
	 * Creates a new collapsable panel by creating 
	 * objects of header panel and content panel 
	 * and adding them to it.
	 * 
	 *@param obj Contains data pertaining to the 
	 * question with which the collapsable panel is 
	 * associated.
	 */
	public CollapsablePanel(Question obj) {
		super(new GridBagLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
			+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		selected = false;
		headerPanel = new HeaderPanel(obj.name);

		setBackground(new Color(200, 200, 220));
		contentPanel = new ContentPanel(obj);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(headerPanel, gbc);
		add(contentPanel, gbc);
		contentPanel.setVisible(false);
		
		JLabel padding = new JLabel();
		gbc.weighty = 1.0;
		add(padding, gbc);
	}
	
	/**
	 * Updates info pertaining to the question as per the 
	 * data present in the question object passed to it.
	 * 
	 * @param obj Contains data pertaining to the 
	 * question with which the collapsable panel is 
	 * associated.
	 */
	public void refreshPanel(Question obj) {
		headerPanel.text=obj.name;
		headerPanel.repaint();
		contentPanel.status.setText("Status: " + obj.status);
		contentPanel.marked.setText("Marked: " + obj.marked);
		contentPanel.marksObtained.setText("Marks Obtained: " + obj.marksObtained);
		contentPanel.markedBy.setText("Marked By: " + obj.markedBy);
	}

	/**
	 * Toggles the state (collapsed or expanded) of the content 
	 * panel associated with the collapsable panel.
	 */
	public void toggleSelection() {
		selected = !selected;
		if (contentPanel.isShowing())
			contentPanel.setVisible(false);
		else
			contentPanel.setVisible(true);
		validate();
		headerPanel.repaint();
	}
}