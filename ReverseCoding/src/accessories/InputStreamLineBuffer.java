package accessories;

import java.io.InputStream;
import java.io.IOException;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <h1>InputStreamLineBuffer.class</h1>
 * 
 * <p>
 * Input buffer that catches and stores the output 
 * from the executable process.
 * 
 * @author Sourish Banerjee
 */

public class InputStreamLineBuffer {
     private InputStream istream; // Input stream on which the input stream line buffer is built.
     private ConcurrentLinkedQueue<String> lines; // Stores the lines read from the executable input stream.
     private long lastTimeModified; // Stores the time of the most recent extraction from the executable output stream. 
     private Thread inputCatcher;
     private boolean isAlive; // Stores the state of activeness of the input steam line buffer.
     
     /**
      * To initialize the input stream line buffer.
      * 
      * @param is Input stream upon which the input 
      * stream line buffer is built. 
      */
     public InputStreamLineBuffer(InputStream is) {
    	   istream = is;
           lines = new ConcurrentLinkedQueue<String>();
           lastTimeModified = System.currentTimeMillis();
           isAlive = false;
        
           inputCatcher = new Thread(new Runnable() {
                public void run() {
             
                	 StringBuilder builder = new StringBuilder(100); // Stores a line till it has been read completely.
                     int b;
                     
                     try {
                          while ((b = istream.read()) != -1) {
                           // Reads one char
                           if ((char) b == '\n') {
                                lines.offer(builder.toString()); // Adds the new line to the queue.
                                builder.setLength(0); // Resets the StringBuilder.
                                lastTimeModified = System.currentTimeMillis();
                           } 
                           else 
                                builder.append((char) b); // Appends the char read to the 'builder' if the entire line has not yet been read.
                      }
                     } catch (IOException e1) {
                     } finally {
                         isAlive = false;
                     }
                }
           }
           );
     }
     
     /**
      * To check whether the buffer is active or not.
      * 
      * @return Boolean value indicating whether the 
      * buffer is active or not.
      */
     public boolean isAlive() {
         return isAlive;
     }
     
     /**
      * To start the input stream line buffer. Starts 
      * the input catcher thread which reads output 
      * from the executable process and feeds it into 
      * the input stream line buffer.
      */
     public void start() {
         isAlive = true;
         inputCatcher.start();
     }
     
     /**
      * To check whether the input stream line buffer 
      * has any more lines of output.
      * 
      * @return Boolean value indicating whether the 
      * input stream line buffer has any more lines of 
      * output or not. 
      */
     public boolean hasNext() {
         return lines.size() > 0;
     }
     
     /**
      * To obtain the next line of output from the 
      * input stream line buffer.
      * 
      * @return The next line of output from 
      * the input stream line buffer.
      */
     public String getNext() {
         return lines.poll();
     }
     
     /**
      * To return the amount of time elapsed since the 
      * last line of output was read from the executable 
      * process.
      * 
      * @return The amount of time elapsed since the last 
      * line was read form the executable process.
      */
     public long timeElapsed() {
         return (System.currentTimeMillis() - lastTimeModified);
     }
}