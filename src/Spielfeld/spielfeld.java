package Spielfeld;
import haupt.HauptMain;
import haupt.Hauptspiel;

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
 * File:         spielfeld.java
 * Copyright:    Copyright (c) 2012
 * @author Musab Kaya
 * @version 1.6
 */

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
    /** bomb images */
    public static Image[] bombImages = null;
    /** fire images */
    public static Image[][] fireImages = null;
    /** fire brick images */
    public static Image[][] fireBrickImages = null;
    /** bonus images */
    public static Image[][] bonusImages = null;
    /** exit images */
    public static Image[][] exitImages = null;
    /** fire type enumerations */
    public static final int FIRE_CENTER = 0;
    public static final int FIRE_VERTICAL = 1;
    public static final int FIRE_HORIZONTAL = 2;
    public static final int FIRE_NORTH = 3;
    public static final int FIRE_SOUTH = 4;
    public static final int FIRE_EAST = 5;
    public static final int FIRE_WEST = 6;
    public static final int FIRE_BRICK = 7;
    /** grid slot type enumerations */
    public static final int EXIT_FIRE = -6;
    public static final int EXIT_BOMB = -5;
    public static final int BONUS_FIRE = -4;
    public static final int BONUS_BOMB = -3;
    public static final int NOTHING = -1;
    public static final int WALL = 0;
    public static final int BRICK = 1;
    public static final int BOMB = 3;
    public static final int EXIT = 4;
    /** random level generator */
    private static Rand levelRand = null;
    /** random bonus generator */
    private static Rand bonusRand = null;
    /** random exit generator */
    private static Rand exitRand = null;
    /** current level */
    public static int level = 0;
	/** rendering hints */
    private static Object hints = null;

    static {
 

        /** create the level random generator */
        levelRand = new Rand(0, 100);
        /** create the bonus random generator */
        bonusRand = new Rand(0, 7);
        /** create the exit random generator */
        exitRand = new Rand(0, 7);
        /** creat the image objects array */
        feldImages = new Image[3][3];
        /** create the bomb objects array */
        bombImages = new Image[2];
		/** create the exit objects array */
        exitImages = new Image[2][2];
        /** create the fire objects array */
		fireImages = new Image[8][8];
		/** create the fire brick objects array */
        fireBrickImages = new Image[3][8];
		/** create the bonus image objects array */
        bonusImages = new Image[3][3];

        try {
            String[] strs = new String[3];
            /** load the map images */
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
            /** load the bomb images */
            for (int i = 0; i < 2; i++) {
                str = HauptMain.RP + "Images/BomberBombs/" + (i + 1) + ".gif";
                bombImages[i] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }

            /** load the fire images */
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
            /** load the fire brick images */
            for (int i = 0; i < 2; i++) for (f = 0; f < 8; f++)
            {
                str = HauptMain.RP + "Images/BomberFireBricks/" +
                (i + 1) + (f + 1) + ".gif";
                fireBrickImages[i][f] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }

            /** load the bonus image sprites */
            for (int i = 0; i < 3; i++) for (f = 0; f < 2; f++)
            {
                str = HauptMain.RP + "Images/BomberBonuses/" +
                (i == 0 ? "F" : "B") + (f + 1) + ".gif";
                bonusImages[i][f] = Toolkit.getDefaultToolkit().getImage(
                new File(str).getCanonicalPath());
            }
            /** load the exit image sprites */
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
        /** generator random level */
        level = levelRand.draw() % 2;
        MediaTracker tracker = new MediaTracker(this);
		/** prepare the images */
        try
        {
            int counter = 0;
            /** load the map images */
            for (int i = 0; i < 2; i++) for (int j = 0; j < 3; j++) {
                if (feldImages[i][j] != null)
                { tracker.addImage(feldImages[i][j], counter++); }
            }
            /** load the bomb images */
            for (int i = 0; i < 2; i++)
                tracker.addImage(bombImages[i], counter++);
			/** load the fire brick images */
            for (int i = 0; i < 8; i++)
                fireImages[FIRE_BRICK][i] = fireBrickImages[level][i];
            /** load the fire images */
			for (int i = 0; i < 8; i++) for (int j = 0; j < 8; j++)
                tracker.addImage(fireImages[i][j], counter++);
            
			/** wait for images to finish loading */
			tracker.waitForAll();
        } catch (Exception e) { new Error(e); }

        bombs = new Vector();
        bonuses = new Vector();
        exits = new Vector();
		/** create the fire grid */
        fireGrid = new boolean[30][30];
		/** create the bomb grid */
        bombGrid = new bombe[30][30];
		/** create the bonus grid */
        bonusGrid = new bonus[30][30];
		/** create the exit grid */
        exitGrid = new exit[30][30];
        
        /** create the map grid */
        grid = new int[30][30];
        /** fill the map with walls by alternating r by c */
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
        /** generate random bricks */
        for (int i = 0; i < 256 * 2; i++)
        {
            x = ri.draw();
            y = ri.draw();
            if (grid [x][y] == NOTHING)
               grid [x][y] = BRICK;
        }

        /** clear corners so players can stand there */
        grid [ 1][ 1] = grid [ 2][ 1] = grid [ 1][ 2] =
        grid [ 1][28] = grid [ 2][28] = grid [ 1][27] =
        grid [28][ 1] = grid [14][ 1] = grid [28][ 2] =
        grid [28][28] = grid [15][27] = grid [27][28] = NOTHING;

        /** create background color */
        backgroundColor = new Color(52, 108, 108);
        /** set panel size */
        setPreferredSize(new Dimension(30 << HauptMain.shiftCount,
        30 << HauptMain.shiftCount));
        /** double buffer on */
		setDoubleBuffered(true);
		
        setBounds(0, 0, 30 << main.shiftCount, 30 << main.shiftCount);
        setOpaque(false);
		/** add the map to the bottom layer */
        main.getLayeredPane().add(this, 1000);
    }

    /**
     * Sets game over flag on
     */
     public void setGameOver() {
        gameOver = true;
        paintImmediately(0, 0,
        30 << HauptMain.shiftCount, 30 << HauptMain.shiftCount);
     }

     /**
      * Creates a EXIT
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
     * Removes a exit
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
      * Creates a bonus.
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
     * Removes a bonus.
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
      * Creates a bomb.
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
     * Removes a bomb.
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
     * Creates a fire.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param owner owner
     * @param type fire type
     */
      public void createFire(int x, int y, int owner, int type)
      {
         int _x = (x >> HauptMain.shiftCount) << HauptMain.shiftCount;
         int _y = (y >> HauptMain.shiftCount) << HauptMain.shiftCount;
         boolean createFire = false;
         /** if there's a bomb here */
         if (grid[_x >> HauptMain.shiftCount][_y >> HauptMain.shiftCount] ==
         BOMB) {
             /** then short the bomb */
             if (bombGrid[_x >> HauptMain.shiftCount][_y
             >> HauptMain.shiftCount] != null)
             bombGrid[_x >> HauptMain.shiftCount][_y
             >> HauptMain.shiftCount].shortBomb();
         }
         /** if there's no fire there already */
         else if (!fireGrid[_x >>
            HauptMain.shiftCount][_y >> HauptMain.shiftCount]) {
             createFire = true;
             /** create a fire there */
             feuer f = new feuer(this, _x, _y, type);
         }
         /** if this is a center */
         if (createFire && type == FIRE_CENTER) {
             int shiftCount = HauptMain.shiftCount;
             int size = HauptMain.size;
             /** then create a chain of fire */
             int northStop = 0, southStop = 0, westStop = 0, eastStop = 0,
             northBlocks = 0, southBlocks = 0, westBlocks = 0, eastBlocks = 0;
             /** see how long the fire can be */
             for (int i = 1; i <= Hauptspiel.players[owner].fireLength; i++) {
                 /** if it can still go south */
                 if (southStop == 0) { if (((_y >> shiftCount) + i) < 30) {
                    /** if there isnt't a wall there */
                    if (grid[_x >> shiftCount][(_y >> shiftCount) + i] != WALL) {
                       /** if there's something there though */
                       if (grid[_x >> shiftCount][(_y >> shiftCount) + i]
                       != NOTHING)
                          /** then create a tail fire there */
                          { southStop = grid[_x >> shiftCount]
                             [(_y >> shiftCount) + i]; }
                        /** increase fire chain */
                       southBlocks += 1;
                     } else southStop = -1; }
                 }
                 /** if it can still go north */
                 if (northStop == 0) { if (((_y >> shiftCount) - 1) >= 0) {
                    /** if there isn't a wall there */
					if (grid[_x >> shiftCount][(_y >> shiftCount) - i] != WALL) {
                       /** if there's something there though */
					   if (grid[_x >> shiftCount][(_y >> shiftCount) - i]
                       != NOTHING)
                          /** then create a tail fire there */
						  { northStop = grid[_x >> shiftCount]
                             [(_y >> shiftCount) - i]; }
                        /** increaes fire chain */
					    northBlocks += 1;
                        } else northStop = -1; }
                 }
                 /** if it can still go east */
                 if (eastStop == 0) { if (((_x >> shiftCount) + i) < 30) {
                    /** if there isn't a wall there */
					if (grid[(_x >> shiftCount) + i][_y >> shiftCount] != WALL) {
                       /** if there's somethign there though */
					   if (grid[(_x >> shiftCount) + i][_y >> shiftCount]
                       != NOTHING)
                          /** then create a tail fire there */
						  { eastStop = grid[(_x >> shiftCount) + i]
                             [_y >> shiftCount]; }
                       /** increase fire chain */
					   eastBlocks += 1;
                     } else eastStop = -1; }
                 }
                 /** if it can still go west */
                 if (westStop == 0) { if (((_x >> shiftCount) - i) >= 0) {
                    /** if there isn't a wall there */
					if (grid[(_x >> shiftCount) - i][_y >> shiftCount] != WALL) {
                       /** if there's something there through */
					   if (grid[(_x >> shiftCount) - i]
                       [_y >> shiftCount] != NOTHING)
							/** then create a tail fire there */
							{ westStop = grid[(_x >> shiftCount) - i]
                             [_y >> shiftCount]; }
							 /** increase fire chain */
                       westBlocks += 1;
                     } else westStop = -1; }
                 }
             }
             /** create the north chain */
             for (int i = 1; i <= northBlocks; i++) {
                 /** if this is a tail */
				 if (i == northBlocks) {
                      /** if there's a brick */
                    if (northStop == BRICK)
                       /** then create a burning brick sprite */
                       createFire(_x, _y - (i * size), owner, FIRE_BRICK);
                     /** then create a burning brick sprite */
                    else createFire(_x, _y - (i * size), owner, FIRE_NORTH);
                 }
                 /** if it's not a tail then create a normal fire */
                 else createFire(_x, _y - (i * size), owner, FIRE_VERTICAL);
             }
             for (int i = 1; i <= southBlocks; i++) {
                 /** if this is a tail */
				 if (i == southBlocks) {
					/** if there's a brick */
                     if (southStop == BRICK)
					 /** then create a burning brick sprite */
                        createFire(_x, _y + (i * size), owner, FIRE_BRICK);
                     /** if it's not a brick then create a tail */
					 else createFire(_x, _y + (i * size), owner, FIRE_SOUTH);
                 }
				 /** if it's not a tail then create a normal fire */
                 else createFire(_x, _y + (i * size), owner, FIRE_VERTICAL);
             }
             for (int i = 1; i <= eastBlocks; i++) {
				/** if this is a tail */
                 if (i == eastBlocks) {
					/** if there's a brick */
                     if (eastStop == BRICK)
						/** then create a burning brick sprite */
                        createFire(_x + (i * size), _y, owner, FIRE_BRICK);
                     /** if it's not a brick then create a tail */
					 else createFire(_x + (i * size), _y, owner, FIRE_EAST);
                 }
				 /** if it's not a tail then create a normal fire */
                 else createFire(_x + (i * size), _y, owner, FIRE_HORIZONTAL);
             }
             for (int i = 1; i <= westBlocks; i++) {
				/** if this is a tail */
                 if (i == westBlocks) {
					/** if there's a brick */
                     if (westStop == BRICK)
					 /** then create a burning brick sprite */
                        createFire(_x - (i * size), _y, owner, FIRE_BRICK);
                     /** if it's not a brick then create a tail */
					 else createFire(_x - (i * size), _y, owner, FIRE_WEST);
                 }
				 /** if it's not a tail then create a normal fire */
                 else createFire(_x - (i * size), _y, owner, FIRE_HORIZONTAL);
             }
         }
      }

    /**
     * Drawing method.
     * @param graphics graphics handle
     */
     public synchronized void paint(Graphics graphics) {
        Graphics g = graphics;
            /** if game is over */
            if (gameOver) {
                /** fill the screen with black color */
                g.setColor(Color.black);
                g.fillRect(0, 0, 30 << HauptMain.shiftCount,
                30 << HauptMain.shiftCount);
            }
            /** if game isn't over yet */
            else {
                /** fill window with background color */
                g.setColor(backgroundColor);
                g.fillRect(0, 0, 30 << HauptMain.shiftCount,
                30 << HauptMain.shiftCount);
                /** draw the map */
                for (int r = 0; r < 30; r++) for (int c = 0; c < 30; c++) {
                    /** if there's something in the block */
                    if (grid[r][c] > NOTHING &&
                    grid[r][c] != BOMB && grid[r][c] != FIRE_BRICK &&
                    feldImages[level][grid[r][c]] != null) {
                        g.drawImage(feldImages[level][grid[r][c]],
                        r << HauptMain.shiftCount, c << HauptMain.shiftCount,
                        HauptMain.size, HauptMain.size, null);
                    }
                    /** if the block is empty */
                    else {
                        if (feldImages[level][2] != null) {
                           /** draw the floor */
                           g.drawImage(feldImages[level][2],
                           r << HauptMain.shiftCount, c <<
                           HauptMain.shiftCount, HauptMain.size,
                           HauptMain.size, null);
                        }
                    }
                }
            }
        
        if (!gameOver) {
            /** draw the bonuses */
            Bonus bb = null;
            int i = 0, k = bonuses.size();
            while (i < k) {
                bb = (Bonus)bonuses.elementAt(i);
                if (bonusGrid[bb.r][bb.c] != null)
                   bonusGrid[bb.r][bb.c].paint(g);
                i += 1;
                k = bonuses.size();
            }
            /** draw the exit */
            Exit bbb = null;
            i = 0; k = exits.size();
            while (i < k) {
                bbb = (Exit)exits.elementAt(i);
                if (exitGrid[bbb.r][bbb.c] != null)
                   exitGrid[bbb.r][bbb.c].paint(g);
                i += 1;
                k = exits.size();
            }
            /** draw the bombs */
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
     * Drawing method for Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        /** set the rendering hints */
        g2.setRenderingHints((RenderingHints)hints);
        /** if game is over */
        if (gameOver) {
            /** fill the screen with black color */
            g2.setColor(Color.black);
            g2.fillRect(0, 0, 30 << HauptMain.shiftCount,
            30 << HauptMain.shiftCount);
        }
        /** if game isn't over yet */
        else {
            /** fill window with background color */
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, 30 << HauptMain.shiftCount,
            30 << HauptMain.shiftCount);
            /** draw the map */
			for (int r = 0; r < 30; r++) for (int c = 0; c < 30; c++) {
                /** if there's something in the block */
                if (grid[r][c] > NOTHING &&
                grid[r][c] != BOMB && grid[r][c] != FIRE_BRICK &&
                feldImages[level][grid[r][c]] != null) {
                    g2.drawImage(feldImages[level][grid[r][c]],
                    r << HauptMain.shiftCount, c << HauptMain.shiftCount,
                    HauptMain.size, HauptMain.size, null);
                }
                /** if the block is empty */
                else {
                    if (feldImages[level][2] != null) {
						/** draw the floor */
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