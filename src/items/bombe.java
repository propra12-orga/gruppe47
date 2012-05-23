package items;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;

import java.awt.*;
import javax.swing.*;

import Spielfeld.spielfeld;

import java.io.*;



/**
 * This class creates the bombs in the game.
 */
public class bombe extends Thread {
    /** feld object */
    private spielfeld feld = null;
    /** position */
    private int x = 0;
    private int y = 0;
    /** frame count */
    private int frame = 0;
    /** alive flag */
    private boolean alive = true;
    /** owner */
    private int owner = 0;
    /** count down : 3000 ms */
    private int countDown = 3900;
    /** bomb sprite image handles */
    private static Image[] images = null;
    /** rendering hints */
    private static Object hints = null;

    static {
        /** if java runtime is Java 2 */
        if (Main.J2) {
            /** create the rendering hints for better graphics output */
            RenderingHints h = null;
            h = new RenderingHints(null);
            h.put(RenderingHints.KEY_TEXT_ANTIALIASING,
             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            h.put(RenderingHints.KEY_FRACTIONALMETRICS,
             RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            h.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
             RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            h.put(RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
            h.put(RenderingHints.KEY_COLOR_RENDERING,
             RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            hints = (RenderingHints)h;
        }
    }

    /**
     * Constructs a BOMB!
     * @param feld game feld
     * @param x x-coordinate
     * @param y y-coordinate
     * @param owner owner
     * @param images bomb images
     */
    public bombe(spielfeld feld, int x, int y, int owner) {
        this.feld = feld;
        this.x = x;
        this.y = y;
        this.owner = owner - 1;
        this.images = spielfeld.bombImages;

        feld.grid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        spielfeld.BOMB;
        setPriority(Thread.MAX_PRIORITY);
        start();
    }

    /**
     * Main loop.
     */
    public synchronized void run() {
        while (alive) {
            /** draw the bomb */
            //paint();
            feld.paintImmediately(x, y, HauptMain.size, HauptMain.size);
            /** rotate frame */
            frame = (frame + 1) % 2;
            /** sleep for 130 ms */
            try { sleep(130); } catch (Exception e) {}
            if (!alive) break;
            /** decrease count down */
            countDown -= 130;
            /** if count down reached 0 then exit */
            /** the loop and short the bomb */
            if (countDown <= 0) break;
        }
        /** remove it from the grid */
        feld.grid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        spielfeld.NOTHING;
        /** give the bomb back to the player */
        Hauptspiel.players[owner].usedBombs -= 1;
        feld.bombGrid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        null;
        Hauptspiel.players[owner].bombGrid
        [x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] = false;
        feld.removeBomb(x, y);
        HauptMain.sndEffectPlayer.playSound("Explosion");
        /** create the fire */
        feld.createFire(x, y, owner, spielfeld.FIRE_CENTER);
    }

    /**
     * Explodes the bomb
     */
    public void shortBomb() {
        alive = false;
        interrupt();
    }

    /**
     * Drawing method.
     */
    public void paint(Graphics g) {
        /** if java runtime is Java 2 */
        if (Main.J2) { paint2D(g); }
        /** if java runtime isn't Java 2 */
        else {
             g.drawImage(images[frame], x, y,
             HauptMain.size, HauptMain.size, null);
        }
    }

    /**
     * Drawing method for Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        /** set the rendering hints */
        g2.setRenderingHints((RenderingHints)hints);
        g2.drawImage(images[frame], x, y,
        HauptMain.size, HauptMain.size, null);
    }
}