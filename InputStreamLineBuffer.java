import java.io.*;
import java.util.concurrent.*;
//Note that this class actually catches output from the executable. It is named InputStreamLineBuffer as the output from the executable is input to the java program
public class InputStreamLineBuffer
{
     InputStream istream;
     ConcurrentLinkedQueue<String> lines;
     long lastTimeModified;
     Thread inputCatcher;
     boolean isAlive;
     public InputStreamLineBuffer(InputStream is) 
     {
           istream = is;
           lines = new ConcurrentLinkedQueue<String>();// To store the lines read from the executable input stream
           lastTimeModified = System.currentTimeMillis();
           isAlive = false;
           inputCatcher = new Thread(new Runnable() 
           {
                public void run() 
                {
                     StringBuilder builder = new StringBuilder(100);// To store a line till it has been read completely
                     int b;
                     try 
                     {
                          while ((b = istream.read()) != -1) 
                          {
                           // Read one char
                           if ((char) b == '\n') 
                           {
                                lines.offer(builder.toString());// Add the new line to the queue
                                builder.setLength(0); // Reset the StringBuilder
                                lastTimeModified = System.currentTimeMillis();
                           } 
                           else 
                                builder.append((char) b); // Append char to the StringBuilder if the entire line has not yet been read
                      }
                     } 
                     catch (IOException e) 
                     {
                         e.printStackTrace();
                     }
                     finally 
                     {
                         isAlive = false;// To mark the end of the obtainable output and the thread execution
                     }
                }
           }
           );
      }
     public boolean isAlive() 
     {
         return isAlive;
     }
     public void start() 
     {
         isAlive = true;
         inputCatcher.start();
      }
     public boolean hasNext() 
     {
         return lines.size() > 0;
     }
     public String getNext() 
     {
         return lines.poll();
     }
     public long timeElapsed() 
     {
         return (System.currentTimeMillis() - lastTimeModified);
     }
}