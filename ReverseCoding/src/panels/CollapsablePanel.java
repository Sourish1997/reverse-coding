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

class CollapsablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean selected;
	private HeaderPanel headerPanel;
	private ContentPanel contentPanel;
	
	public boolean access = false;
	
	private class HeaderPanel extends JPanel implements MouseListener {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Font font;
		private String text;
		//private BufferedImage open, closed;
		private final int OFFSET = 30; // PAD = 5;
		
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

		public void mouseClicked(MouseEvent e) {
			toggleSelection();
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}
	}
 
	public class ContentPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private JLabel status, marked, marksObtained, markedBy;
		private JButton attempt;
		
		public ContentPanel(Question obj) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e1) {
				JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
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

			attempt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					access = true;
				}
			});
		}
	}

	public CollapsablePanel(Question obj) {
		super(new GridBagLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
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
	
	public void refreshPanel(Question obj) {
		headerPanel.text=obj.name;
		headerPanel.repaint();
		contentPanel.status.setText("Status: " + obj.status);
		contentPanel.marked.setText("Marked: " + obj.marked);
		contentPanel.marksObtained.setText("Marks Obtained: " + obj.marksObtained);
		contentPanel.markedBy.setText("Marked By: " + obj.markedBy);
	}

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