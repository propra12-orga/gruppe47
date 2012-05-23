package items;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;

import java.awt.*;
import javax.swing.*;

import Spielfeld.spielfeld;

import java.io.*;

/**
 * File:         bonus
 * Copyright:    Copyright (c) 2009
 * @author Avinash Kumar Sharma
 * @version 1.0
 */

/**
 * This class creates the bonuses in the game.
 */
public class bonus extends Thread {
    /** feld object */
    private spielfeld feld = null;
    /** position */
    private int x = 0;
    private int y = 0;
    /** frame count */
    private int frame = 0;
    /** alive flag */
    private boolean alive = true;
    /** bonus type */
    private int type = 0;
    /** bomb sprite image handles */
    private Image[] images = null;
    /** rendering hints */
    private static Object hints = null;

    private static int FIRE = 0;
    private static int BOMB = 1;

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
     * Constructs a bonus.
     * @param feld feld object
     * @param x x-coordinate
     * @param y y-coordinage
     * @param type bonus type;
     */
    public bonus(spielfeld feld, int x, int y, int type) {
        this.feld = feld;
        this.x = x;
        this.y = y;
        this.type = type;
        this.images = spielfeld.bonusImages[type];

        setPriority(Thread.MAX_PRIORITY);
        start();
    }

    /**
     * Main loop.
     */
    public synchronized void run() {
        while (alive) {
            /** draw the bonus */
            feld.paintImmediately(x, y, HauptMain.size, HauptMain.size);
            /** rotate frame */
            frame = (frame + 1) % 2;
            /** sleep for 130 ms */
            try { sleep(130); } catch (Exception e) {}
            if (frame == 10) break;
        }
        /** remove it from the grid */
        feld.removeBonus(x, y);
    }

    /**
     * Gives this bonus to a user then removes it.
     */
    public void giveToPlayer(int player) {
        HauptMain.sndEffectPlayer.playSound("Bonus");
        /** if it's a fire bonus */
        if (type == FIRE) /** then increase the fire length by 1 */
           Hauptspiel.players[player - 1].fireLength += 1;
        /** if it's a bomb bonus */
        else if (type == BOMB) /** then increase the bomb count by 1 */
             Hauptspiel.players[player - 1].totalBombs += 1;
        kill();
    }

    /**
     * Kills the object along with the thread
     */
    public void kill() {
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