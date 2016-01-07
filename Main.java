import javax.swing.*;
import java.awt.*;
import java.io.*;
public class Main
{
    public static void main(String args[])// To create the frame and add the exe executor panel to it
    {
        JFrame frame=new JFrame("EXE Executor");
        ExeExecutorPanel panel=new ExeExecutorPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setLayout(new BorderLayout());
        frame.add(panel,BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}