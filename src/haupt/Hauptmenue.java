package haupt;

import items.bilder;

import sound.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import einstellung.Error;
import einstellung.einstellung;

import java.io.*;

public class Hauptmenue extends JPanel {
	/** declares the main method*/
    private HauptMain main = null;
    /** declares the images of the buttons*/
    private bilder[] imageButtons = null;
    /**declares what is selected on start up*/
    private int selection = P2;

    /** declares the background image*/
    private static Image backgroundImg = null;
    /** declares the button images*/
    private static Image[] buttonImagesDown = null;
    /***/
    private static Image[] buttonImagesUp = null;
    /** declares the position of p2*/
    private static final int P2 = 0;
    /** declares the position of the options*/
    private static final int CONTROL_SETUP = 1;
    /** declares the postion of the exit*/
    private static final int EXIT = 2;

    static {
        buttonImagesDown = new Image[5];
        buttonImagesUp = new Image[5];

        Toolkit tk = Toolkit.getDefaultToolkit();
        String path = HauptMain.RP + "Images/BomberMenu/";
        String file = null;
        try {
            file = "hintergrund_bomberman2.png";
            backgroundImg = tk.getImage(new File(file).getCanonicalPath());

            for (int i = 0; i < 5; i++) {
                if (i == P2) file = path + (2) + " Spieler";
                else if (i == CONTROL_SETUP) file = path + "Einstellung";
                else if (i == EXIT) file = path + "Exit";
                buttonImagesDown[i] = tk.getImage(
                new File(file + " Down.gif").getCanonicalPath());
                buttonImagesUp[i] = tk.getImage(
                new File(file + " Up.gif").getCanonicalPath());
            }
        }
        catch (Exception e) { new Error(e);}
    }

    /**
     * creates the menue with the postions of the buttons
     * @param main HauptMain object
     */
    public Hauptmenue(HauptMain main) {
        this.main = main;
        setPreferredSize(new Dimension(30 << main.shiftCount,
        30 << main.shiftCount));

        MediaTracker mt = new MediaTracker(this);
try {
            int counter = 0;
            mt.addImage(backgroundImg, counter++);
            for (int i = 0; i < 5; i++) {
                mt.addImage(buttonImagesDown[i], counter++);
                mt.addImage(buttonImagesUp[i], counter++);
            }
            mt.waitForAll();
    }
            catch (Exception e) { new Error(e);}
       
        imageButtons = new bilder[5];
        for (int i = 0; i < 5; i++) {
            Image[] images = { buttonImagesDown[i], buttonImagesUp[i] };
            imageButtons[i] = new bilder(this, images);
        }
        int dy = buttonImagesDown[0].getHeight(this) / (30 / main.size * 2);
        for (int i = P2; i <= EXIT; i++)
            imageButtons[i].setInfo(120, (400 / (40 / main.size)) + (dy * i), i);
        imageButtons[P2].setBevel(true);
        HauptMain.sndEffectPlayer.playSound("bomberman_hauptmenue");
    }

    /**
     * converts pressed keys in the menue
     * @param evt key event
     */
    public void keyPressed(KeyEvent evt) {
        int newSelection = selection;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_LEFT: newSelection -= 1; break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_RIGHT: newSelection += 1; break;
            case KeyEvent.VK_ENTER: doCommand(selection);
        }
        if (selection != newSelection) {
            if (newSelection < 0) newSelection = EXIT;
            imageButtons[selection].setBevel(false);
            selection = newSelection;
            selection %= 5;
            imageButtons[selection].setBevel(true);
        }
    }

    /**
     * converts the choosen buttons, either starts a new game, or opens the option menue or exit the game
     * @param command command
     */
    public void doCommand(int command) {
        switch (command) {
            case P2:
                 main.newGame(selection + 2);
                 break;
            case CONTROL_SETUP:
                 new einstellung(main);
                 break;
            case EXIT:
                /** questions if you really want to close the game */
                JOptionPane pane =
                new JOptionPane("Willst du wirklich Bomberman verlassen?");
                /** yes or no */
                pane.setOptionType(JOptionPane.YES_NO_OPTION);
                pane.setMessageType(JOptionPane.WARNING_MESSAGE);
                /** creates dialog */
                JDialog dialog = pane.createDialog(this, "Bomberman Schließen?");
                dialog.setResizable(false);
                /** show dialog */
                dialog.show();
                Object selection = pane.getValue();

                /** if yes */
                if (selection != null && selection.toString().equals("0"))
                    /** exit */
                    System.exit(0);
        }
    }

    /**
     * Painting method.
     * @param g graphics handler
     */
    public void paint(Graphics graphics) {
        Graphics g = graphics;
            g.drawImage(backgroundImg, 0, 0, 30 <<
            main.shiftCount, 30 << main.shiftCount, this);
        for (int i = 0; i < 5; i++) if (imageButtons[i] != null)
            imageButtons[i].paint(g);
    }
}
