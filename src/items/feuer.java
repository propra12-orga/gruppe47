package items;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;

import java.awt.*;
import javax.swing.*;

import Spielfeld.spielfeld;



public class feuer extends Thread {
    /** spielfeld objekt */
    private spielfeld feld = null;
    /** spielfeld grid */
    private int[][] grid = null;
    /** position */
    private int x = 0;
    private int y = 0;
    /** feuer*/
    private int type = 0;
    /** frame count */
    private int frame = 0;
    /** owner */
    private int owner = 0;
    /** bombe image */
    private static Image[][] images = null;
    /** hints */
    private static Object hints = null;



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

        /** bonus*/
        if (feld.bonusGrid[x >> HauptMain.shiftCount]
        [y >> HauptMain.shiftCount] != null) {
           /** bonus benutzen */
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
            /** feuer zeichen */
            paint();
            /** ob einer spieler da ist */
            for (int i = 0; i < Hauptspiel.totalPlayers; i++) {
                /** wenn er da ist */
                if ((Hauptspiel.players[i].x >> HauptMain.shiftCount) ==
                (x >> HauptMain.shiftCount) && (Hauptspiel.players[i].y >>
                HauptMain.shiftCount) == (y >> HauptMain.shiftCount)) {
                    /** dann töten */
                    Hauptspiel.players[i].kill();
                }
            }
            /**  frame inkresieren*/
            frame = frame + 1;
            /** aus für 65 ms */
            try { sleep(65); } catch (Exception e) {}
            /** frame */
            if (frame > 7) break;
        }
        feld.grid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        spielfeld.NOTHING;
        feld.fireGrid[x >> HauptMain.shiftCount][y >> HauptMain.shiftCount] =
        false;
        
        feld.paintImmediately(x, y, HauptMain.size, HauptMain.size);
       
        if (type == spielfeld.FIRE_BRICK) { feld.createBonus(x, y); }
    }

    /**
     * Zeichen method.
     */
    public void paint() {
        Graphics g = feld.getGraphics();
        /** wenn java runtzeit Java 2 ist*/
    
        /** wenn nicht */
        else {
             g.drawImage(images[type][frame], x, y,
             HauptMain.size, HauptMain.size, null);
        }
        g.dispose();
    }

    /**
     *zeichen methode
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        /** hints */
        g2.setRenderingHints((RenderingHints)hints);
        g2.drawImage(images[type][frame], x, y,
        HauptMain.size, HauptMain.size, null);
    }
}