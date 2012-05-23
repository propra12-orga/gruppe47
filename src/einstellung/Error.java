package einstellung;
import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 * Diese Klasse erstellt ein Error Feld.
 */
public class Error
{
    /**
     * Konstruiert Exception Objekt.
     * @param e exception 
     */
    public Error(Exception e)
    {
        /** Speichert Fehlermeldung in eine Variable */
        CharArrayWriter writer = new CharArrayWriter();
        e.printStackTrace(new PrintWriter(writer, true));
        String result = new String(" " + writer.toString() + "\n" +
        " Klicke um das Programm zu schlieﬂen.");
        
        /** erstelle eine Textfeld und tuh die nachricht dort rein */
        JTextArea ta = new JTextArea(result);
        ta.setEditable(false);
        
        JScrollPane scroller = new JScrollPane(ta);
        JOptionPane pane = new JOptionPane(scroller);
        pane.setOptionType(-JOptionPane.NO_OPTION);
        pane.setMessageType(JOptionPane.ERROR_MESSAGE);
        
        /** erstelle und zeige die Nachricht */
        JDialog dialog = pane.createDialog(null, "Exception Abgefangen");
        dialog.setResizable(false);
        dialog.show();
        Object selection = pane.getValue();
        
        /** Programm schlieﬂen */
        System.exit(-1);
    }
    
    public Error(Exception e, boolean terminate)
    {
        CharArrayWriter writer = new CharArrayWriter();
        e.printStackTrace(new PrintWriter(writer, true));
        String result = new String(" " + writer.toString());
        
        JTextArea ta = new JTextArea(result);
        ta.setEditable(false);
        
        JScrollPane scroller = new JScrollPane(ta);
        JOptionPane pane = new JOptionPane(scroller);
        pane.setOptionType(-JOptionPane.NO_OPTION);
        pane.setMessageType(JOptionPane.ERROR_MESSAGE);
        
        JDialog dialog = pane.createDialog(null, "Exception Abgefangen");
        dialog.setResizable(false);
        dialog.show();
        Object selection = pane.getValue();
        
        if (terminate)
            System.exit(-1);
    }        
}