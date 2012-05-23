package items;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;

import java.awt.*;
import javax.swing.*;

import Spielfeld.spielfeld;

/**
 * Title:        Bomberman
 * Description:
 * Copyright:    Copyright (c) 2009
 * @author Avinash Kumar Sharma
 * @version 1.0
 */

public class feuer extends Thread {
    /** feld object */
    private spielfeld feld = null;
    /** feld grid handle */
    private int[][] grid = null;
    /** position */
    private int x = 0;
    private int y = 0;
    /** fire type */
    private int type = 0;
    /** frame count */
    private int frame = 0;
    /** owner */
    private int owner = 0;
    /** bomb sprite image handles */
    private static Image[][] images = null;
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

    public feuer(spielfeld feld, int x, int y, int type) {
        this.feld = feld;
        grid = feld.grid;
        this.x = x;
        this.y = y;
        this.type = type;
        this.owner = owner - 1;
        this.images = spielfeld.fireImages;

        if (type == spielfeld.FIRE_BRICK)
           grid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
           spielfeld.FIRE_BRICK;
        feld.fireGrid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        true;

        /** see if there is a bonus in the same spot */
        if (feld.bonusGrid[x >> HauptMain.shiftCount]
        [y >> HauptMain.shiftCount] != null) {
           /** if yes then remove it */
           feld.removeBonus(x, y);
        }

        setPriority(Thread.MAX_PRIORITY);
        start();
    }

    /**
     * Main loop.
     */
    public void run() {
        while (true) {
            /** draw the fire */
            paint();
            /** see if any players are in the way */
            for (int i = 0; i < Hauptspiel.totalPlayers; i++) {
                /** if there is */
                if ((Hauptspiel.players[i].x >> HauptMain.shiftCount) ==
                (x >> HauptMain.shiftCount) && (Hauptspiel.players[i].y >>
                HauptMain.shiftCount) == (y >> HauptMain.shiftCount)) {
                    /** then kill it */
                    Hauptspiel.players[i].kill();
                }
            }
            /** increase frame */
            frame = frame + 1;
            /** sleep for 65 ms */
            try { sleep(65); } catch (Exception e) {}
            /** if frame is greater than 7 then it's finish burning */
            if (frame > 7) break;
        }
        feld.grid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        spielfeld.NOTHING;
        feld.fireGrid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        false;
        /** if this is a tail or brick, then it's the last fire in the chain */
        /** then refresh the screen */
        feld.paintImmediately(x, y, HauptMain.size, HauptMain.size);
        /** if this was a brick then create a bonus there */
        if (type == spielfeld.FIRE_BRICK) { feld.createBonus(x, y); }
    }

    /**
     * Drawing method.
     */
    public void paint() {
        Graphics g = feld.getGraphics();
        /** if java runtime is Java 2 */
        if (Main.J2) { paint2D(feld.getGraphics()); }
        /** if java runtime isn't Java 2 */
        else {
             g.drawImage(images[type][frame], x, y,
             HauptMain.size, HauptMain.size, null);
        }
        g.dispose();
    }

    /**
     * Drawing method for Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        /** set the rendering hints */
        g2.setRenderingHints((RenderingHints)hints);
        g2.drawImage(images[type][frame], x, y,
        HauptMain.size, HauptMain.size, null);
    }
}