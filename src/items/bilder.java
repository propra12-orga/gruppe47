package items;
import haupt.HauptMain;
import haupt.Main;

import java.awt.*;
import javax.swing.*;
import java.io.*;

public class bilder {
    /** this object's container */
    private JPanel panel;
    /** x co-ordinate where the image is drawn */
    private int x;
    /** y co-ordinate where the image is drawn */
    private int y;
    /** ID of object (for command) */
    private int ID;
    /** image width */
    private int w;
    /** image height */
    private int h;
    /** area the object controls */
    private Rectangle rect;
    /** the images of the button: normal / outerglowed */
    private Image[] images;
    /** stae of button: normal / outglowed */
    private int state = 0;

    /** rendering hints */
    private static Object hints = null;

    static {
       
    }

    /**
     * Construct with a MainMenu object and an array of images for use with
     * the button.
     * @param mainMenu object's container
     * @param images images for the button
     */
    public bilder(JPanel panel, Image[] images) {
        this.panel = panel;
        this.images = images;
        /** calculate image dimension */
        w = images[0].getWidth(panel);
        h = images[0].getHeight(panel);
    }

    /**
     * Set object's parameters.
     * @param x x co-ordinate on window
     * @param y y co-ordinate on window
     * @param ID object's ID or command return code
     */
    public void setInfo(int x, int y, int ID) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        /** calculate area in which the object owns to handle mouse events */
        rect = new Rectangle(this.x, this.y - 5, w, h + 10);
    }

    /**
     * @return the ojbect's ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Set bevel on or off
     * @param bevelOn true or false
     */
     public void setBevel(boolean bevelOn) {
        if (bevelOn) state = 1; else state = 0;
        panel.repaint();
        panel.paintImmediately(x, y, w / (32 / HauptMain.size * 2),
           h / (32 / HauptMain.size * 2));
     }

    /**
     * Draws the button onto the window.
     * @param graphics graphics handler
     */
    public void paint(Graphics graphics) {
            Graphics g = graphics;
            /** draw the button */
            g.drawImage(images[state], x, y, w / (32 / HauptMain.size * 2),
            h / (32 / HauptMain.size * 2), null);
        }
   

    /**
     * Drawing method for Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        /** set the rendering hints */
        g2.setRenderingHints((RenderingHints)hints);
        /** draw the button */
        g2.drawImage(images[state], x, y, w / (32 / HauptMain.size * 2),
        h / (32 / HauptMain.size * 2), null);
    }
}