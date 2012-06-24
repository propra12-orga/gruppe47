package haupt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Spielfeld.spielfeld;

import sound.Soundeffekt;
import sound.bgm;

import einstellung.Error;

import java.lang.Integer;
import java.io.*;
/**
 * 
 * @author Laptop
 *
 */

public class HauptMain extends JFrame {
    /** path for the files */
    public static String RP = "./";
    /** declares the menue*/
    private Hauptmenue menu = null;
    /** declares the game*/
    private Hauptspiel spiel = null;
    /** declares the soundeffects for the play*/
    public static Soundeffekt sndEffectPlayer = null;
    /** scale value for the window*/
    public static final int shiftCount = 4;
    /** defines the size of the window*/
    public static final int size = 1 << shiftCount;

    static {
        sndEffectPlayer = new Soundeffekt();
    }

    /**
     * constructs main frame.
     */
    public HauptMain() {

        addWindowListener(new WindowAdapter() {
            /**
             * converts the window close action
             * @param evt window event
             */
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });

        /** keyboard listner*/
        addKeyListener(new KeyAdapter() {
            /**
             * converts pressed keys on the keyboard
             * @param evt keyboard event
             */
            public void keyPressed(KeyEvent evt) {
            	if (menu != null) menu.keyPressed(evt);
                if (spiel != null) spiel.keyPressed(evt);
            }

            /**
             * converts released keys
             * @param evt keyboard event
             */
            public void keyReleased(KeyEvent evt) {
                if (spiel != null) spiel.keyReleased(evt);
            }
        });

        setTitle("Bomberman");

        /** set window icon */
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                new File(RP + "Images/Bomberman.gif").getCanonicalPath()));
        getContentPane().add(menu = new Hauptmenue(this));
        }
        catch (Exception e) { new Error(e);}
       
        setResizable(false);
       
        pack();

        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;

        setLocation(x, y);
        show();
        toFront();
    }

    /**
     * new game.
     * @param players total number of players
     */
    public void newGame(int players)
    {
    	/** loading screen */
        JDialog dialog = new JDialog(this, "Lade Spiel...", false);
        dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        dialog.setSize(new Dimension(200, 0));
        dialog.setResizable(false);
        /** x coordinates of the loading screen*/
        int x = getLocation().x + (getSize().width - 200) / 2;
        /** x coordinates of the loading screen*/
        int y = getLocation().y + getSize().height / 2;
        dialog.setLocation(x, y);
        dialog.show();

        getContentPane().removeAll();
        getLayeredPane().removeAll();
        menu = null;
        spielfeld feld = new spielfeld(this);
        spiel = new Hauptspiel(this, feld, players);
        dialog.dispose();
        show();
        
    }

    /**
     * main method.
     * @param args arguments
     */
    public static void main(String[] args) {
        HauptMain bomberMain1 = new HauptMain();
    }
}
