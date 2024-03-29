package haupt;	

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Spielfeld.figur;
import Spielfeld.spielfeld;

import einstellung.Error;

import java.io.*;

public class Hauptspiel extends JPanel
implements ActionListener {
	/**declares the main mathode*/
    private HauptMain main = null;
    /** declares the boolean for gameover*/
    private boolean gameOver = false;
    /**declares the fiel*/
    private spielfeld feld = null;
    /** declares the winner*/
    private int winner = -1;
    /**declares the timer for the game*/
    private Timer timer = null;
    /** declares the elapsed seconds for the timer*/
    private int elapsedSec = 0;

    /** declares the images*/
    private static Image[] images = null;
    /** declares the total players */
    public static int totalPlayers = 1;
    /** declares the players who are left*/
    public static int playersLeft = totalPlayers;
    /** declares the players*/
    public static figur[] players = null;

    static
    {
        String path = HauptMain.RP + "Images/BomberEndGame/";
        String str = null;
        images = new Image[6];
        try
        {
            for (int i = 0; i < 4; i++) {
                str = path + "Player " + (i + 1) + " Wins.jpg";
                images[i] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }
            str = path + "Draw.jpg";
            images[4] = Toolkit.getDefaultToolkit().getImage(
            new File(str).getCanonicalPath());
            str = path + "Enter to Continue.jpg";
            images[5] = Toolkit.getDefaultToolkit().getImage(
            new File(str).getCanonicalPath());
        }
        catch (Exception e) { new Error(e);}
    }

    /**
     * Constructs a game.
     * @param main main frame object
     * @param feld feld object
     * @param totalPlayers total number of players
     */
    public Hauptspiel(HauptMain main, spielfeld feld, int totalPlayers) {
        this.main = main;
        this.feld = feld;
        this.totalPlayers = this.playersLeft = totalPlayers;
        players = new figur[totalPlayers];
        /** create the players */
        for (int i = 0; i < totalPlayers; i++)
            players[i] = new figur(this, feld, i + 1);

        setBounds(0, 0, 30 << main.shiftCount, 30 << main.shiftCount);
        setOpaque(false);
        main.getLayeredPane().add(this, 0);
    }

    /**
     * Key pressed handler
     * @param evt key event
     */
    public void keyPressed(KeyEvent evt)
    {
        if (!gameOver) {
           for (int i = 0; i < totalPlayers; i++)
               players[i].keyPressed(evt);
        }
        else if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            timer.stop();
            timer = null;
            main.dispose();
            new HauptMain();
        }
    }

    /**
     * @param evt key event
     */
    public void keyReleased(KeyEvent evt)
    {
        if (!gameOver) {
           for (int i = 0; i < totalPlayers; i++)
               players[i].keyReleased(evt);
        }
    }

    /**
     * paints the graphics when one player won and while the players are alive 
     * @param graphics graphics handle
     */
    public void paint(Graphics graphics) {
        Graphics g = graphics;
        if (!gameOver) {
           for (int i = 0; i < totalPlayers; i++)
               players[i].paint(graphics);
        }
            if (gameOver) {
                g.drawImage(images[winner], 0,
                		HauptMain.size == 16 ? -25 : -50,
                30 << HauptMain.shiftCount,
                30 << HauptMain.shiftCount, this);

                if (elapsedSec == 0)
                    g.drawImage(images[5], 0,
                    (30 << HauptMain.shiftCount) - images[5].getHeight(this) /
                    (HauptMain.size != 16 ? 1 : 2),
                    images[5].getWidth(this) / (HauptMain.size != 16 ? 1 : 2),
                    images[5].getHeight(this) /
                    (HauptMain.size != 16 ? 1 : 2), this);
                
                else
                    g.fillRect(0, (30 << HauptMain.shiftCount) -
                    images[5].getHeight(this) /
                    (HauptMain.size != 16 ? 1 : 2),
                    images[5].getWidth(this) /
                     (HauptMain.size != 16 ? 1 : 2),
                    images[5].getHeight(this) /
                    (HauptMain.size != 16 ? 1 : 2));
            }
            if (playersLeft <= 1 && timer == null)
            {
                for (int i = 0; i < totalPlayers; i++)
                    players[i].deactivate();
                timer = new Timer(1000, this);
                timer.start();
            }
        }


    /**
     * paint method
     * @param x x-coordinate
     * @param y y-coordinate
     * @param w width
     * @param h height
     */
    public void pPaintImmediately(int x, int y, int w, int h) {
        paintImmediately(x, y, w, h);
    }

    /**
     * checks if there is a winner, set the elapsed time +1 when any action is performed by the players,
     * paints the image for the winner
     * @param evt action event
     */
    public void actionPerformed(ActionEvent evt) {
        elapsedSec += 1;
        if (elapsedSec >= 4)
        {
            winner = 4;
            for (int i = 0; i < totalPlayers; i++) {
                if (!players[i].isDead()) { winner = i; break; }
            }
            gameOver = true;
            feld.setGameOver();
            timer.stop();
            timer = new Timer(500, this);
            timer.start();
        }
        if (gameOver)
        {
            elapsedSec %= 2;
            paintImmediately(0,
            (30 << HauptMain.shiftCount) - images[5].getHeight(this) /
            (HauptMain.size != 16 ? 1 : 2),
            images[5].getWidth(this)  / (HauptMain.size != 16 ? 1 : 2),
            images[5].getHeight(this)  / (HauptMain.size != 16 ? 1 : 2));
        }
    }
}