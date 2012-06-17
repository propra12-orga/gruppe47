package Spielfeld;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;
import items.bombe;
import items.bonus;
import items.feuer;
import items.exit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import einstellung.Error;

import java.io.*;
import java.util.Vector;

/**
 * This class draws the map and handles things like bonuses and bombs.
 */
public class spielfeld extends JPanel {
    /** frame Objekt */
    private HauptMain main = null;
    /** game over flag */
    private boolean gameOver = false;
    /** background color */
    private Color backgroundColor = null;
    /** the map grid array */
    public int[][] grid = null;
    /** fire grid */
    public boolean[][] fireGrid = null;
    /** bomb grid */
    public bombe[][] bombGrid = null;
    /** bonus grid */
    public bonus[][] bonusGrid = null;
    /** exit grid */
    public exit[][] exitGrid = null;
    /** bombs */
    private Vector bombs = null;
    /** bonuses */
    private Vector bonuses = null;
    /** exit */
    private Vector exits = null;

    /**
     * Bomb info class
     */
    private class Bomb {
        public Bomb(int x, int y) {
            r = (x >> HauptMain.shiftCount);
            c = (y >> HauptMain.shiftCount);
        }
        public int r = 0;
        public int c = 0;
    }

    /**
     * Bonus info class
     */
    private class Bonus {
        public Bonus(int x, int y) {
            r = (x >> HauptMain.shiftCount);
            c = (y >> HauptMain.shiftCount);
        }
        public int r = 0;
        public int c = 0;
    }
    /**
     * Exit info class
     */
    private class Exit {
        public Exit(int x, int y) {
            r = (x >> HauptMain.shiftCount);
            c = (y >> HauptMain.shiftCount);
        }
        public int r = 0;
        public int c = 0;
    }

    /** image handles for the map images */
    private static Image[][] feldImages = null;
    /** BombenBild */
    public static Image[] bombImages = null;
    /** FeuerBild */
    public static Image[][] fireImages = null;
    /** Feuerstein Bilder */
    public static Image[][] fireBrickImages = null;
    /** BonusBilder */
    public static Image[][] bonusImages = null;
    /** Exit Bilder */
    public static Image[][] exitImages = null;
    /** Feuertyp aufzählung */
    public static final int FIRE_CENTER = 0;
    public static final int FIRE_VERTICAL = 1;
    public static final int FIRE_HORIZONTAL = 2;
    public static final int FIRE_NORTH = 3;
    public static final int FIRE_SOUTH = 4;
    public static final int FIRE_EAST = 5;
    public static final int FIRE_WEST = 6;
    public static final int FIRE_BRICK = 7;
    /** raster aufzählung */
    public static final int EXIT_FIRE = -6;
    public static final int EXIT_BOMB = -5;
    public static final int BONUS_FIRE = -4;
    public static final int BONUS_BOMB = -3;
    public static final int NOTHING = -1;
    public static final int WALL = 0;
    public static final int BRICK = 1;
    public static final int BOMB = 3;
    public static final int EXIT = 4;
    /** zufälliges level generator */
    private static Rand levelRand = null;
    /** zufällig bonus generieren */
    private static Rand bonusRand = null;
    /** zufällig exit generieren */
    private static Rand exitRand = null;
    /** aktuelles Level */
    public static int level = 0;
    private static Object hints = null;

    static {
 

        /** erstellt Level zufällig */
        levelRand = new Rand(0, 100);
        /** erstellt bonus zufällig */
        bonusRand = new Rand(0, 7);
        /** erstellt exit zufällig */
        exitRand = new Rand(0, 7);
        /** Erstellt Bild Objekt array */
        feldImages = new Image[3][3];
        /** Erstellt Bomben Objekt Array */
        bombImages = new Image[2];
        exitImages = new Image[2][2];
        fireImages = new Image[8][8];
        fireBrickImages = new Image[3][8];
        bonusImages = new Image[2][2];

        try {
            String[] strs = new String[3];
            /** lade spielfeld bilder */
            for (int i = 0; i < 2; i++) {
                strs[0] = HauptMain.RP + "Images/BomberWalls/" + (i + 1);
                strs[1] = HauptMain.RP + "Images/BomberBricks/" + (i + 1);
                strs[2] = HauptMain.RP + "Images/BomberFloors/" + (i + 1);
                for (int j = 0; j < 3; j++) {
                    if (i == 0) strs[j] += ".jpg";
                    else strs[j] += ".gif";
                }
                feldImages[i][0] = Toolkit.getDefaultToolkit().getImage(
                new File(strs[0]).getCanonicalPath());
                feldImages[i][1] = Toolkit.getDefaultToolkit().getImage(
                new File(strs[1]).getCanonicalPath());
                if (i == 0) feldImages[i][2] = null;
                else
                    feldImages[i][2] = Toolkit.getDefaultToolkit().getImage(
                    new File(strs[2]).getCanonicalPath());
            }

            String str = null;
            /** lade bomben bilder */
            for (int i = 0; i < 2; i++) {
                str = HauptMain.RP + "Images/BomberBombs/" + (i + 1) + ".gif";
                bombImages[i] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }

            /** lade die feuer bilder */
            for (int t = 0; t < 7; t++) for (int i = 0; i < 8; i++)
            {
                str = HauptMain.RP + "Images/BomberFires/";
                if (t == FIRE_CENTER) str += "C";
                else if (t == FIRE_VERTICAL) str += "V";
                else if (t == FIRE_NORTH) str += "N";
                else if (t == FIRE_HORIZONTAL) str += "H";
                else if (t == FIRE_EAST) str += "E";
                else if (t == FIRE_WEST) str += "W";
                else if (t == FIRE_SOUTH) str += "S";
                if (t == FIRE_BRICK) fireImages[t][i] = null;
                else {
                    str += (i + 1) + ".gif";
                    fireImages[t][i] = Toolkit.getDefaultToolkit().getImage(
                    new File(str).getCanonicalPath());
                }
            }

            int f = 0;
            /** lade feuer blöcke */
            for (int i = 0; i < 2; i++) for (f = 0; f < 8; f++)
            {
                str = HauptMain.RP + "Images/BomberFireBricks/" +
                (i + 1) + (f + 1) + ".gif";
                fireBrickImages[i][f] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }

            /** lade bonus bilder */
            for (int i = 0; i < 2; i++) for (f = 0; f < 2; f++)
            {
                str = HauptMain.RP + "Images/BomberBonuses/" +
                (i == 0 ? "F" : "B") + (f + 1) + ".gif";
                bonusImages[i][f] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }
            /** lade exit bilder */
            for (int i = 0; i < 2; i++) for (f = 0; f < 2; f++) {
                str = HauptMain.RP + "Images/BomberExit/" + (i + 1) + 
                (i == 0 ? "F" : "B") + (f + 1) + ".gif";
                exitImages[i][f] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }
        }
        catch (Exception e) { new Error(e); }
    }

    public spielfeld(HauptMain main) {
        this.main = main;
        /** generiere level zufällig */
        level = levelRand.draw() % 2;
        MediaTracker tracker = new MediaTracker(this);
        try
        {
            int counter = 0;
            /** lade feld bilder */
            for (int i = 0; i < 2; i++) for (int j = 0; j < 3; j++) {
                if (feldImages[i][j] != null)
                { tracker.addImage(feldImages[i][j], counter++); }
            }
            /** lade bomben bilder */
            for (int i = 0; i < 2; i++)
                tracker.addImage(bombImages[i], counter++);
            for (int i = 0; i < 8; i++)
                fireImages[FIRE_BRICK][i] = fireBrickImages[level][i];
            for (int i = 0; i < 8; i++) for (int j = 0; j < 8; j++)
                tracker.addImage(fireImages[i][j], counter++);
            tracker.waitForAll();
        } catch (Exception e) { new Error(e); }

        bombs = new Vector();
        bonuses = new Vector();
        exits = new Vector();
        fireGrid = new boolean[30][30];
        bombGrid = new bombe[30][30];
        bonusGrid = new bonus[30][30];
        exitGrid = new exit[30][30];
        
        /** Erstelle feld raster */
        grid = new int[30][30];
        /** fülle den rand mit ausnahme des spielers */
        for (int r = 0; r < 30; r++) for (int c = 0; c < 30; c++) {
            /** if it's the edge */
            if (r == 0 || c == 0 || r == 29 || c == 29) grid[r][c] = WALL;
            else if ( (r & 1) == 0 && (c & 1) == 0 ) grid[r][c] = WALL;
            else grid[r][c] = NOTHING;
            fireGrid[r][c] = false;
            bombGrid[r][c] = null;
            bonusGrid[r][c] = null;
            exitGrid[r][c] = null;
        }

        int x, y;
        Rand ri = new Rand(1, 28);
        /** generiert zufällig blöcke **/
        for (int i = 0; i < 256 * 2; i++)
        {
            x = ri.draw();
            y = ri.draw();
            if (grid [x][y] == NOTHING)
               grid [x][y] = BRICK;
        }

        /** spieler kann hier stehen */
        grid [ 1][ 1] = grid [ 2][ 1] = grid [ 1][ 2] =
        grid [ 1][28] = grid [ 2][28] = grid [ 1][27] =
        grid [28][ 1] = grid [14][ 1] = grid [28][ 2] =
        grid [28][28] = grid [15][27] = grid [27][28] = NOTHING;

        /** hintergrundfarbe */
        backgroundColor = new Color(52, 108, 108);
        /** setze panel grösse */
        setPreferredSize(new Dimension(30 << HauptMain.shiftCount,
        30 << HauptMain.shiftCount));
        setDoubleBuffered(true);

        setBounds(0, 0, 30 << main.shiftCount, 30 << main.shiftCount);
        setOpaque(false);
        main.getLayeredPane().add(this, 1000);
    }

    /**
     * Spielende
     */
     public void setGameOver() {
        gameOver = true;
        paintImmediately(0, 0,
        30 << HauptMain.shiftCount, 30 << HauptMain.shiftCount);
     }

     /**
      * Erstelle EXIT
      * @param x x-coordinate
      * @param y y-coordinate
      * @param owner owner
      */
    public synchronized void createExit(int x, int y) {
        int _x = (x >> HauptMain.shiftCount) << HauptMain.shiftCount;
        int _y = (y >> HauptMain.shiftCount) << HauptMain.shiftCount;
        int type = exitRand.draw();
        /** erstelle exit : 0 = feuer; 1 = bombe */
        if (type == 0 || type == 1) {
           exitGrid[_x >> HauptMain.shiftCount][_y >> HauptMain.shiftCount] =
           new exit(this, _x, _y, type);
           exits.addElement(new Exit(_x, _y));
        }
    }
    
    /**
     * Lösche exit
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public synchronized void removeExit(int x, int y) {
        int i = 0, k = exits.size();
        int r = (x >> HauptMain.shiftCount);
        int c = (y >> HauptMain.shiftCount);
        Exit b = null;
        while (i < k) {
            b = (Exit)exits.elementAt(i);
            if (b.r == r && b.c == c) {
                exits.removeElementAt(i);
                exitGrid[b.r][b.c].kill();
                exitGrid[b.r][b.c] = null;
                paintImmediately(b.r << HauptMain.shiftCount,
                b.c << HauptMain.shiftCount, HauptMain.size,
                HauptMain.size);
                break;
            }
            i += 1;
            k = exits.size();
        }
     }
    
     /**
      * Erstelle Bonus
      * @param x x-coordinate
      * @param y y-coordinate
      * @param owner owner
      */
    public synchronized void createBonus(int x, int y) {
        int _x = (x >> HauptMain.shiftCount) << HauptMain.shiftCount;
        int _y = (y >> HauptMain.shiftCount) << HauptMain.shiftCount;
        int type = bonusRand.draw();
        /** erstelle bonus : 0 = feuer; 1 = bombe */
        if (type == 0 || type == 1) {
           bonusGrid[_x >> HauptMain.shiftCount][_y >> HauptMain.shiftCount] =
           new bonus(this, _x, _y, type);
           bonuses.addElement(new Bonus(_x, _y));
        }
    }

    /**
     * Lösche bonus
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public synchronized void removeBonus(int x, int y) {
        int i = 0, k = bonuses.size();
        int r = (x >> HauptMain.shiftCount);
        int c = (y >> HauptMain.shiftCount);
        Bonus b = null;
        while (i < k) {
            b = (Bonus)bonuses.elementAt(i);
            if (b.r == r && b.c == c) {
                bonuses.removeElementAt(i);
                bonusGrid[b.r][b.c].kill();
                bonusGrid[b.r][b.c] = null;
                paintImmediately(b.r << HauptMain.shiftCount,
                b.c << HauptMain.shiftCount, HauptMain.size,
                HauptMain.size);
                break;
            }
            i += 1;
            k = bonuses.size();
        }
     }

     /**
      * Erstelle bombe
      * @param x x-coordinate
      * @param y y-coordinate
      * @param owner owner
      */
    public synchronized void createBomb(int x, int y, int owner) {
        int _x = (x >> HauptMain.shiftCount) << HauptMain.shiftCount;
        int _y = (y >> HauptMain.shiftCount) << HauptMain.shiftCount;
        bombGrid[_x >> HauptMain.shiftCount][_y >> HauptMain.shiftCount] =
        new bombe(this, _x, _y, owner);
        bombs.addElement(new Bomb(_x, _y));
    }

    /**
     * lösche bombe
     * @param x x-coordinate
     * @param y y-coordinate
     */
     public synchronized void removeBomb(int x, int y) {
        int i = 0, k = bombs.size();
        int r = (x >> HauptMain.shiftCount);
        int c = (y >> HauptMain.shiftCount);
        Bomb b = null;
        while (i < k) {
            b = (Bomb)bombs.elementAt(i);
            if (b.r == r & b.c == c) {
                bombs.removeElementAt(i);
                break;
            }
            i += 1;
            k = bombs.size();
        }
     }

     /**
      * erstelle feuer
      * @param x x-coordinate
      * @param y y-coordinate
      * @param owner owner
      * @param type feuer type
      */
      public void createFire(int x, int y, int owner, int type)
      {
         int _x = (x >> HauptMain.shiftCount) << HauptMain.shiftCount;
         int _y = (y >> HauptMain.shiftCount) << HauptMain.shiftCount;
         boolean createFire = false;
         /** wenn bombe dort ist */
         if (grid[_x >> HauptMain.shiftCount][_y >> HauptMain.shiftCount] ==
         BOMB) {
             /** dann verkürze die bombe */
             if (bombGrid[_x >> HauptMain.shiftCount][_y
             >> HauptMain.shiftCount] != null)
             bombGrid[_x >> HauptMain.shiftCount][_y
             >> HauptMain.shiftCount].shortBomb();
         }
         /** wenn kein feuer dort ist */
         else if (!fireGrid[_x >>
            HauptMain.shiftCount][_y >> HauptMain.shiftCount]) {
             createFire = true;
             /** erstelle feuer */
             feuer f = new feuer(this, _x, _y, type);
         }
         /** Wenn Zentrum des Feuers hier */
         if (createFire && type == FIRE_CENTER) {
             int shiftCount = HauptMain.shiftCount;
             int size = HauptMain.size;
             /** dann erstelle feuerflammen*/
             int northStop = 0, southStop = 0, westStop = 0, eastStop = 0,
             northBlocks = 0, southBlocks = 0, westBlocks = 0, eastBlocks = 0;
             /** fuckt wie lang die flamme gehen kann */
             for (int i = 1; i <= Hauptspiel.players[owner].fireLength; i++) {
                 /** nur nach süden */
                 if (southStop == 0) { if (((_y >> shiftCount) + i) < 30) {
                     /** wenn keine wand */
                    if (grid[_x >> shiftCount][(_y >> shiftCount) + i] != WALL) {
                       /** wenn hier nichts ist */
                       if (grid[_x >> shiftCount][(_y >> shiftCount) + i]
                       != NOTHING)
                          /** dann erstelle flamme */
                          { southStop = grid[_x >> shiftCount]
                             [(_y >> shiftCount) + i]; }
                         /** erhöhe Feuer Kette */
                       southBlocks += 1;
                     } else southStop = -1; }
                 }
                 /** nur nach norden */
                 if (northStop == 0) { if (((_y >> shiftCount) - 1) >= 0) {
                    if (grid[_x >> shiftCount][(_y >> shiftCount) - i] != WALL) {
                       if (grid[_x >> shiftCount][(_y >> shiftCount) - i]
                       != NOTHING)
                          { northStop = grid[_x >> shiftCount]
                             [(_y >> shiftCount) - i]; }
                       northBlocks += 1;
                       } else northStop = -1; }
                 }
                 /** nur nach osten */
                 if (eastStop == 0) { if (((_x >> shiftCount) + i) < 30) {
                    if (grid[(_x >> shiftCount) + i][_y >> shiftCount] != WALL) {
                       if (grid[(_x >> shiftCount) + i][_y >> shiftCount]
                       != NOTHING)
                          { eastStop = grid[(_x >> shiftCount) + i]
                             [_y >> shiftCount]; }
                       eastBlocks += 1;
                     } else eastStop = -1; }
                 }
                 /** nur nach westen */
                 if (westStop == 0) { if (((_x >> shiftCount) - i) >= 0) {
                    if (grid[(_x >> shiftCount) - i][_y >> shiftCount] != WALL) {
                       if (grid[(_x >> shiftCount) - i]
                       [_y >> shiftCount] != NOTHING)
                          { westStop = grid[(_x >> shiftCount) - i]
                             [_y >> shiftCount]; }
                       westBlocks += 1;
                     } else westStop = -1; }
                 }
             }
             /** Erstelle Kette nach norden */
             for (int i = 1; i <= northBlocks; i++) {
                 if (i == northBlocks) {
                     /**wenn dort ein block ist */
                    if (northStop == BRICK)
                       /** dann erstelle ein brennendes block */
                       createFire(_x, _y - (i * size), owner, FIRE_BRICK);
                     /** wenn es kein block ist dann erstelle flamme */
                    else createFire(_x, _y - (i * size), owner, FIRE_NORTH);
                 }
                 /** wenn keine Flamme erstellt wird,  dann erstelle Feuer */
                 else createFire(_x, _y - (i * size), owner, FIRE_VERTICAL);
             }
             for (int i = 1; i <= southBlocks; i++) {
                 if (i == southBlocks) {
                     if (southStop == BRICK)
                        createFire(_x, _y + (i * size), owner, FIRE_BRICK);
                     else createFire(_x, _y + (i * size), owner, FIRE_SOUTH);
                 }
                 else createFire(_x, _y + (i * size), owner, FIRE_VERTICAL);
             }
             for (int i = 1; i <= eastBlocks; i++) {
                 if (i == eastBlocks) {
                     if (eastStop == BRICK)
                        createFire(_x + (i * size), _y, owner, FIRE_BRICK);
                     else createFire(_x + (i * size), _y, owner, FIRE_EAST);
                 }
                 else createFire(_x + (i * size), _y, owner, FIRE_HORIZONTAL);
             }
             for (int i = 1; i <= westBlocks; i++) {
                 if (i == westBlocks) {
                     if (westStop == BRICK)
                        createFire(_x - (i * size), _y, owner, FIRE_BRICK);
                     else createFire(_x - (i * size), _y, owner, FIRE_WEST);
                 }
                 else createFire(_x - (i * size), _y, owner, FIRE_HORIZONTAL);
             }
         }
      }

     /**
      * Zeichne Methode.
      * @param graphics graphics handle
      */
     public synchronized void paint(Graphics graphics) {
        Graphics g = graphics;
            /** spiel vorbei */
            if (gameOver) {
                /** hintergrund schwarz setzen */
                g.setColor(Color.black);
                g.fillRect(0, 0, 30 << HauptMain.shiftCount,
                30 << HauptMain.shiftCount);
            }
            /** spiel nicht vorbei */
            else {
                /** hintergrund farbe setzen */
                g.setColor(backgroundColor);
                g.fillRect(0, 0, 30 << HauptMain.shiftCount,
                30 << HauptMain.shiftCount);
                /** zeichne das feld */
                for (int r = 0; r < 30; r++) for (int c = 0; c < 30; c++) {
                    /** if there's something in the block */
                    if (grid[r][c] > NOTHING &&
                    grid[r][c] != BOMB && grid[r][c] != FIRE_BRICK &&
                    feldImages[level][grid[r][c]] != null) {
                        g.drawImage(feldImages[level][grid[r][c]],
                        r << HauptMain.shiftCount, c << HauptMain.shiftCount,
                        HauptMain.size, HauptMain.size, null);
                    }
                    /** wenn block leer */
                    else {
                        if (feldImages[level][2] != null) {
                           /** untergrund zeichnen */
                           g.drawImage(feldImages[level][2],
                           r << HauptMain.shiftCount, c <<
                           HauptMain.shiftCount, HauptMain.size,
                           HauptMain.size, null);
                        }
                    }
                }
            }
        
        if (!gameOver) {
            /** zeichne bonus */
            Bonus bb = null;
            int i = 0, k = bonuses.size();
            while (i < k) {
                bb = (Bonus)bonuses.elementAt(i);
                if (bonusGrid[bb.r][bb.c] != null)
                   bonusGrid[bb.r][bb.c].paint(g);
                i += 1;
                k = bonuses.size();
            }
            /** zeichne exit */
            Exit bbb = null;
            i = 0; k = exits.size();
            while (i < k) {
                bbb = (Exit)exits.elementAt(i);
                if (exitGrid[bbb.r][bbb.c] != null)
                   exitGrid[bbb.r][bbb.c].paint(g);
                i += 1;
                k = exits.size();
            }
            /** zeichne bombe */
            Bomb b = null;
            i = 0; k = bombs.size();
            while (i < k)
            {
                b = (Bomb)bombs.elementAt(i);
                if (bombGrid[b.r][b.c] != null)
                   bombGrid[b.r][b.c].paint(g);
                i += 1;
                k = bombs.size();
            }
        }
     }

    /**
     * Zeichne Methode für Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        /** set the rendering hints */
        g2.setRenderingHints((RenderingHints)hints);
        /** spiel vorbei */
        if (gameOver) {
            /** hintergrund schwarz setzen */
            g2.setColor(Color.black);
            g2.fillRect(0, 0, 30 << HauptMain.shiftCount,
            30 << HauptMain.shiftCount);
        }
        /** wenn spiel nicht vorbei ist */
        else {
            /** hintergrund farbe setzen */
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, 30 << HauptMain.shiftCount,
            30 << HauptMain.shiftCount);
            for (int r = 0; r < 30; r++) for (int c = 0; c < 30; c++) {
                /** wenn block voll */
                if (grid[r][c] > NOTHING &&
                grid[r][c] != BOMB && grid[r][c] != FIRE_BRICK &&
                feldImages[level][grid[r][c]] != null) {
                    g2.drawImage(feldImages[level][grid[r][c]],
                    r << HauptMain.shiftCount, c << HauptMain.shiftCount,
                    HauptMain.size, HauptMain.size, null);
                }
                /** wenn block leer */
                else {
                    if (feldImages[level][2] != null) {
                       g2.drawImage(feldImages[level][2],
                       r << HauptMain.shiftCount, c <<
                       HauptMain.shiftCount, HauptMain.size,
                       HauptMain.size, null);
                    }
                }
            }
        }
    }
}