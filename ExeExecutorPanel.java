import java.io.*; 
import java.awt.*;
import javax.swing.*; 
import java.awt.event.*; 
import javax.swing.text.*;
public class ExeExecutorPanel extends JPanel
{ 
    Runtime runtime;
    Process process; 
    InputStream istream;
    OutputStream ostream;
    InputStreamLineBuffer stdout;
    ProcessBuilder builder;
    JTextArea displayOutput, displayInput;  
    public ExeExecutorPanel()
    {    
        setLayout(new BorderLayout());
        displayOutput = new JTextArea(10, 60); 
        displayInput = new JTextArea(10,60);
        Filter filter=new Filter();
        ((AbstractDocument)displayInput.getDocument()).setDocumentFilter(filter);// To prevent modification of previously passed input
        displayOutput.setText("Output:\n\n");
        displayInput.setText("Input:\n\n");
        filter.changePosition(displayInput.getText().length());
        add(new JScrollPane(displayOutput), BorderLayout.CENTER); 
        add(new JScrollPane(displayInput),BorderLayout.SOUTH);
        displayInput.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode()==e.VK_ENTER)
                {
                    try
                    {
                        e.consume();
                        int last = displayInput.getLineCount() - 1;
                        int start = displayInput.getLineStartOffset(last);
                        int end = displayInput.getLineEndOffset(last);
                        passInput(displayInput.getText().substring(start, end));// To pass only the most recent input typed
                        displayInput.append("\n");
                        filter.changePosition(end+1);// To make the immediate input passed undeditable
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            public void keyReleased(KeyEvent e)
            {
            }
            public void keyTyped(KeyEvent e)
            {
            }
        }
        );
        displayOutput.setEditable(false);
        execute();
        displayOutput();
    } 
    public void execute()
    {
        runtime = Runtime.getRuntime(); // Accessing the runtime environment to run the exe 
        builder=new ProcessBuilder("cpr1");// Enter full path of exe (or just name if exe is present in program folder) here
        builder.redirectErrorStream(true);// Combining the input and error streams of the exe
        try
        {
            process = builder.start();// Creating the executable process
        }   
        catch(Exception e)
        {
            e.printStackTrace();
        }
        istream=process.getInputStream();
        ostream=process.getOutputStream();
        stdout=new InputStreamLineBuffer(istream);// Obtaining access to the input and output streams of the process
    }
    public void passInput(String input)
    {
        // Writing to the process
        if(ostream != null) 
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream));
            try 
            {
                writer.write(input + "\n");
                writer.flush();  // Flushing input to the process
            } 
            catch(Exception e) 
            {
                e.printStackTrace();
            }
        }
    }
    public void displayOutput()
    {
        Thread streamReader = new Thread(new Runnable() 
        {       
           public void run() {
               stdout.start();// To start the input stream line buffer threads
                while(stdout.isAlive() || stdout.hasNext())
                {
                   // Get a line of output if at least 50 millis have passed
                   if(stdout.timeElapsed() > 50)
                       while(stdout.hasNext())
                           print(stdout.getNext());
                   // Sleep a bit bofore the next iteration
                   try 
                   {
                        Thread.sleep(100);
                   } 
                   catch (Exception e) 
                   {
                        e.printStackTrace();
                   }                 
               }
           }          
        }
        );
        streamReader.start();
    }
    public void print(String line) 
    {
        // To place the collected text into the TextArea 
        displayOutput.append(line+"\n"); 
    }
}