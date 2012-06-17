package items;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;

import java.awt.*;
import javax.swing.*;

import Spielfeld.spielfeld;

import java.io.*;

/**
 * File:         exit
 * Copyright:    Copyright (c) 2012
 * @author Musab Kaya
 * @version 1.0
 */

/**
 * This class creates the exit in the game.
 */
public class exit extends Thread {
    /** feld object */
    private spielfeld feld = null;
    /** position */
    private int x = 0;
    private int y = 0;
    /** frame count */
    private int frame = 0;
    /** alive flag */
    private boolean alive = true;
    /** exit type */
    private int type = 0;
    /** exit sprite image handles */
    private Image[] images = null;
    /** rendering hints */
    private static Object hints = null;

    private static int FIRE = 0;
    private static int BOMB = 1;

    static {
        
    }

    /**
     * Constructs a exit.
     * @param feld feld object
     * @param x x-coordinate
     * @param y y-coordinage
     * @param type exit type;
     */
    public exit(spielfeld feld, int x, int y, int type) {
        this.feld = feld;
        this.x = x;
        this.y = y;
        this.type = type;
        this.images = spielfeld.exitImages[type];

        setPriority(Thread.MAX_PRIORITY);
        start();
    }

    /**
     * Main loop.
     */
    public synchronized void run() {
        while (alive) {
            /** draw the exit */
            feld.paintImmediately(x, y, HauptMain.size, HauptMain.size);
            /** rotate frame */
            frame = (frame + 1) % 2;
            /** sleep for 130 ms */
            try { sleep(130); } catch (Exception e) {}
            if (frame == 10) break;
        }
        /** remove it from the grid */
        feld.removeExit(x, y);
    }

    /**
     * Gives this exit to a user then removes it.
     */
    public void giveToPlayer(int player) {
        HauptMain.sndEffectPlayer.playSound("Exit");
        /** if it's a fire exit */
        if (type == FIRE) /** then increase the fire length by 1 */
           Hauptspiel.players[player - 1].fireLength += 1;
        /** if it's a bomb exit */
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
      
             g.drawImage(images[frame], x, y,
             HauptMain.size, HauptMain.size, null);
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