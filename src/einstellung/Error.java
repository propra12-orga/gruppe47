package einstellung;
import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 * This class displays an error dialog then exits the program when specified.
 *
 * @author Musab Kaya
 * @version 1.6
 */
public class Error
{
    /**
     * Construct from an Exception object.
     * @param e exception object thrown
     */
    public Error(Exception e)
    {
        /** store the stack trace (error message) into a string variable */
        CharArrayWriter writer = new CharArrayWriter();
        e.printStackTrace(new PrintWriter(writer, true));
        String result = new String(" " + writer.toString() + "\n" +
        " Klicke um das Programm zu schlieﬂen.");
        
        /** create a text field and put the error message into it */
        JTextArea ta = new JTextArea(result);
		/** make it read only */
        ta.setEditable(false);
        
		/** create a scrollpane to contain the text field */
        JScrollPane scroller = new JScrollPane(ta);
		/** create a container to contain the scrollpane */
        JOptionPane pane = new JOptionPane(scroller);
		/** setup the scrollpane */
        pane.setOptionType(-JOptionPane.NO_OPTION);
        pane.setMessageType(JOptionPane.ERROR_MESSAGE);
        
        /** create and show the dialog */
        JDialog dialog = pane.createDialog(null, "Exception Abgefangen");
        dialog.setResizable(false);
        dialog.show();
        Object selection = pane.getValue();
        
        /** exit the program */
        System.exit(-1);
    }
    
	/** 
     * Construct from an Exception object and a boolean flag of whether to 
     * terminate the program or not.
     * @param e exception object thrown
     */  
    public Error(Exception e, boolean terminate)
    {
		/** store the stack trace (error message) into a string variable */
        CharArrayWriter writer = new CharArrayWriter();
        e.printStackTrace(new PrintWriter(writer, true));
        String result = new String(" " + writer.toString());
        
		/** create a text field and put the error message into it */
        JTextArea ta = new JTextArea(result);
        ta.setEditable(false);
        
		/** create a scrollpane to contain the text field */
        JScrollPane scroller = new JScrollPane(ta);
		/** create a container to contain the scrollpane */
        JOptionPane pane = new JOptionPane(scroller);
		/** setup the scrollpane */
        pane.setOptionType(-JOptionPane.NO_OPTION);
        pane.setMessageType(JOptionPane.ERROR_MESSAGE);
        
		/** create and show the dialog */
        JDialog dialog = pane.createDialog(null, "Exception Abgefangen");
        dialog.setResizable(false);
        dialog.show();
        Object selection = pane.getValue();
        
		/** exit the program if terminate is tree */
        if (terminate)
            System.exit(-1);
    }        
}