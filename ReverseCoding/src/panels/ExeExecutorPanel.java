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

public class ExeExecutorPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private InputStream istream;
	private OutputStream ostream;
	private ProcessBuilder builder;
	private JLabel in , out, messageLabel;
	private InputStreamLineBuffer stdout;
	private JTextArea displayOutput, displayInput;
	private JButton choose, upload, refresh, back;
	
	public Process process;
	public Runtime runtime;
	
	private File uploadFile;
	private String file,username;
	
	public boolean disabled = false;
	
	public ExeExecutorPanel(String file, String user) {
		this.file = file;
		username=user;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
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
		((AbstractDocument) displayInput.getDocument()).setDocumentFilter(filter); // To prevent modification of previously passed input

		/*Lines 35-80: Playing around with GridBagLayout to make the GUI more visually appealing*/
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
		
		upload.setEnabled(false); //Upload button should not be enabled until file to be uploaded has been chosen
		
		displayInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {   
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {   	
					try { 	 
						e.consume();
						
						int last = displayInput.getLineCount() - 1;
						int start = displayInput.getLineStartOffset(last);
						int end = displayInput.getLineEndOffset(last);
						passInput(displayInput.getText().substring(start, end)); // To pass only the most recent input typed
						displayInput.append("\n");
						
						filter.changePosition(end + 1); // To make the immediate input passed not editable
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}

			public void keyReleased(KeyEvent e) {}
			
			public void keyTyped(KeyEvent e) {}	
				
		});
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

		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayInput.setEditable(false);
				process.destroy();
				disabled = true;
			}
		});

		displayOutput.setEditable(false); //To render the output panel not editable
		execute(); //Call to start the executable process
		displayOutput();	
	}

	public void execute() {
		// Accessing the runtime environment to run the exe 
		builder = new ProcessBuilder(file); // Enter full path of exe (or just name if exe is present in program folder) here
		builder.redirectErrorStream(true); // Combining the input and error streams of the exe
		
		try {
			process = builder.start(); // Creating the executable process
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down! (Error Code: 0x2)", "Error Message", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		istream = process.getInputStream();
		ostream = process.getOutputStream();
		
		stdout = new InputStreamLineBuffer(istream); // Obtaining access to the input and output streams of the process
	}

	public void passInput(String input) {
		// Writing to the process
		if (ostream != null) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream));
			
			try {
				writer.write(input + "\n");
				writer.flush(); // Flushing input to the process
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down! (Error Code: 0x1D)", "Error Message", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}	
		}
	}
	
	public void displayOutput() {
		Thread streamReader = new Thread(new Runnable() {		
			public void run() {
				stdout.start();// To start the input stream line buffer threads
				while (stdout.isAlive() || stdout.hasNext()) {
					// Get a line of output if at least 50 milliseconds have passed
					if (stdout.timeElapsed() > 50)
						while (stdout.hasNext())
							print(stdout.getNext());
					// Sleep a bit before the next iteration
					try {
						Thread.sleep(100);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,"Reverse Coding has encountered a fatal error and needs to shut down!", "Error Message", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
			}
		});
		streamReader.start();
	}

	public void print(String line) {
		// To place the collected text into the TextArea 
		displayOutput.append(line + "\n");
	}
}