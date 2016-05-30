package panels;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultCaret;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import retrofit2.Call;

import accessories.Filter;
import accessories.InputStreamLineBuffer;

import network.Service;
import network.ServiceGenerator;

/**
 * <h1>ExeExecutorPanel.class</h1>
 * 
 * <p>
 * Interactive area that allows the user to test 
 * out the executable for a particular question.
 * 
 * @author Sourish Banerjee
 */
public class ExeExecutorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Catches the input and output streams of 
	 * the executable process.
	 */
	private InputStream istream;
	private OutputStream ostream;
	
	private JLabel in , out, messageLabel;
	private JTextArea displayOutput, displayInput;
	private JButton choose, upload, refresh, back;
	
	private ProcessBuilder builder; // Builds the executable process.
	private InputStreamLineBuffer stdout; // Handles the output from the executable process.
	
	/**The executable process.*/
	public Process process;
	
	private String file; // Path of executable process.
	private String username; // User name of the user logged in.	
	private File uploadFile; // Solution to the question created by the user.
	
	/**
	 * Stores the truth value of the activeness of the 
	 * executable panel (in the main frame).
	 */
	public boolean disabled = false;
	
	/**
	 * Creates the executor panel for the executable 
	 * whose path is passed to it.
	 * 
	 * @param file Path of executable file
	 * @param user Name of user logged in
	 */
	public ExeExecutorPanel(String file, String user) {
		this.file = file;
		username = user;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
			+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		displayOutput = new JTextArea(10, 60);
		displayOutput.setLayout(new BorderLayout());
		displayInput = new JTextArea(10, 60);
		displayInput.setLayout(new BorderLayout()); 
		in = new JLabel("Input:");
		out = new JLabel("Output:");
		messageLabel = new JLabel("");
		choose = new JButton("Choose");
		upload = new JButton("Upload");
		refresh = new JButton("Refresh");
		back = new JButton("Back"); 
		in .setFont(new Font("Callibri", Font.BOLD, 11));
		out.setFont(new Font("Callibri", Font.BOLD, 11));
		messageLabel.setFont(new Font("Callibri", Font.BOLD, 11));

		Filter filter = new Filter();
		((AbstractDocument) displayInput.getDocument()).setDocumentFilter(filter); // Prevents modification of previously passed input.

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 20, 5, 20);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		add(out, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(new JScrollPane(displayOutput), gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		add( in , gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(new JScrollPane(displayInput), gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 20, 5, 5);
		gbc.anchor = GridBagConstraints.LINE_START;
		add(messageLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		add(choose, gbc);
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 1;
		add(upload, gbc);
		gbc.gridx = 2;
		add(refresh, gbc);
		gbc.gridx = 3;
		add(back, gbc);
		
		upload.setEnabled(false); // Keeps the upload button disabled till file to be uploaded has been chosen.
		
		/*
		 * Passes the input typed by the user to the executable 
		 * process on pressing the enter key.
		 */
		displayInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {   
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {   	
					try { 	 
						e.consume();
						
						int last = displayInput.getLineCount() - 1;
						int start = displayInput.getLineStartOffset(last);
						int end = displayInput.getLineEndOffset(last);
						passInput(displayInput.getText().substring(start, end)); // Passes only the most recent input typed.
						displayInput.append("\n");
						
						filter.changePosition(end + 1); // Makes the immediate input passed not editable.
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
						+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}

			public void keyReleased(KeyEvent e) {}
			
			public void keyTyped(KeyEvent e) {}	
				
		});
		
		/*
		 * Opens a file chooser to select the solution on pressing 
		 * the 'Choose' button.
		 */
		choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "c", "cpp", "java", "py");
				fileChooser.setFileFilter(filter);
				
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					uploadFile = fileChooser.getSelectedFile();
					messageLabel.setForeground(Color.GREEN.darker());
					messageLabel.setText("File Selected: " + uploadFile.getAbsolutePath());
					upload.setEnabled(true);
				}
			}
		});

		/*
		 * Uploads the file to the server on pressing the 'Upload' 
		 * button.
		 */
		upload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Service service = ServiceGenerator.createService(Service.class);
				
				RequestBody user = RequestBody.create(MediaType.parse("multipart/form-data"), username);
				RequestBody question = RequestBody.create(MediaType.parse("multipart/form-data"), file);
				RequestBody uFile = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile);
				
				Call<String> call = service.uploadFile(uFile, user, question);
				try {
					String message=call.execute().body();
					if(message.equals("Success"))
					{
						messageLabel.setForeground(Color.GREEN.darker());
						messageLabel.setText("File successfully uploaded!");
					}
					else
					{
						messageLabel.setForeground(Color.RED);
						messageLabel.setText("Error Message: File upload failed! Please try again after some time.");
					}
				} catch(Exception e1) {
					messageLabel.setForeground(Color.RED);
					messageLabel.setText("Error Message: Could not connect to server!");
				}
			}
		});

		/*
		 * Terminates and restarts the process executable on pressing 
		 * the 'Refresh' button.
		 */
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayInput.setEditable(false);
				filter.changePosition(0);
				process.destroy();
				displayInput.setText("");
				displayOutput.setText("");
				execute();
				displayOutput();
				displayInput.setEditable(true);
			}
		});

		/*
		 * Sets the state of 'disabled' to true on pressing the 
		 * 'Back' button. 
		 */
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayInput.setEditable(false);
				process.destroy();
				disabled = true;
			}
		});

		displayOutput.setEditable(false); // Renders the output panel not editable.
		DefaultCaret caret = (DefaultCaret)displayOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
		
		execute(); // Call to start the executable process.
		displayOutput();	
	}

	/**
	 * Creates the executable process and redirects 
	 * it's input and output to local streams.
	 */
	public void execute() {
		builder = new ProcessBuilder(file);
		builder.redirectErrorStream(true); // Combines the output and error streams of the executable.
		
		try {
			process = builder.start(); // Creates the executable process.
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
			+ "fatal error and needs to shut down! (Error Code: 0x2)", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		istream = process.getInputStream();
		ostream = process.getOutputStream();
		
		stdout = new InputStreamLineBuffer(istream); // Handles output from the process.
	}

	/**
	 * Obtains input from the user and writes 
	 * it to the process' input stream.
	 * 
	 * @param input Input given by the user.
	 */
	public void passInput(String input) {
		if (ostream != null) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream));
			
			try {
				writer.write(input + "\n");
				writer.flush(); // Flushes input to the process.
			} catch (Exception e1) {
			}	
		}
	}
	
	/**
	 * Obtains output that needs to be displayed 
	 * from the process.
	 */
	public void displayOutput() {
		Thread streamReader = new Thread(new Runnable() {		
			public void run() {
				stdout.start();// Starts the input stream line buffer threads.
				while (stdout.isAlive() || stdout.hasNext()) {
					// Gets a line of output if at least 50 milliseconds have passed.
					if (stdout.timeElapsed() > 50)
						while (stdout.hasNext())
							print(stdout.getNext());
					try {
						Thread.sleep(100);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a "
						+ "fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		streamReader.start();
	}

	/**
	 * Adds the output from the process to the 
	 * output text area.
	 * 
	 * @param line Output from the process
	 */
	public void print(String line) {
		displayOutput.append(line + "\n");
	}
}