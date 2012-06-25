package Spielfeld;
import haupt.HauptMain;
import haupt.Hauptspiel;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import einstellung.Error;
import einstellung.tasten;
import einstellung.tastensave;

import java.io.*;
import java.lang.Integer;

/**
 * File:         figur.java
 * Copyright:    Copyright (c) 2012
 * @author Musab Kaya
 * @version 1.6
 */

/**
 * This class creates player objects.
 */
public class figur extends Thread {
    /** game object handle */
    public Hauptspiel spiel = null;
    /** map object handle */
    private spielfeld feld = null;
    /** player's own bomb grid (must have for synchronization) */
    public boolean[][] bombGrid = null;
    /** input key queue */
    private tastensave keyQueue = null;
    /** bomb key is down or not */
    private boolean bombKeyDown = false;
    /** direction keys down */
    private byte dirKeysDown = 0x00;
    /** current direction key down */
    private byte currentDirKeyDown = 0x00;
    /** sprite width */
    private final int width = HauptMain.size;
    /** sprite height */
    private final int height = 44 / (32 / HauptMain.size);
    /** is exploding flag */
    private boolean isExploding = false;
    /** is dead flag */
    private boolean isDead = false;
    /** whether a key is pressed or not */
    private boolean keyPressed = false;
    /** the player's input keys */
    private int[] keys = null;
    /** total bombs the player has */
    public int totalBombs = 1;
    /** total bombs the player used */
    public int usedBombs = 0;
    /** the player's fire strength */
    public int fireLength = 2;
    /** if player is alive */
    public boolean isActive = true;
    /** player position */
    public int x = 0;
    public int y = 0;
    /** player's number */
    private int playerNo = 0;
    /** user's state : default to face down */
    private int state = DOWN;
    /** flag : whether the player is moving or not */
    private boolean moving = false;
	/** sprite frame number */
    private int frame = 0;
	/** clear mode flag */
    private boolean clear = false;

    /** byte enumerations */
    private static final byte BUP = 0x01;
    private static final byte BDOWN = 0x02;
    private static final byte BLEFT = 0x04;
    private static final byte BRIGHT = 0x08;
    private static final byte BBOMB = 0x10;
    /** number enumerations */
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int BOMB = 4;
    private static final int EXPLODING = 4;
    /** all player sprite images */
    private static Image[][][] sprites = null;
    /** rendering hints */
    private static Object hints = null;

    static {
       
        /** create the images */
        sprites = new Image[4][5][5];
        int[] states = { UP, DOWN, LEFT, RIGHT, EXPLODING };
        Toolkit tk = Toolkit.getDefaultToolkit();
        String path = new String();
        /** open the files */
        try {
            for (int p = 0; p < 4; p++) {
                for (int d = 0; d < 5; d++) {
                    for (int f = 0; f < 5; f++) {
                        /** generate file name */
                        path = HauptMain.RP + "Images/";
                        path += "Bombermans/Player " + (p + 1) + "/";
                        path += states[d] + "" + (f + 1) + ".gif";
                        /** open the file */
                        sprites[p][d][f] = tk.getImage(
                        new File(path).getCanonicalPath());
                    }
                }
            }
        }
        catch (Exception e) { new Error(e); }
    }

    /**
     * Constructs a player.
     * @param feld feld object
     * @param spielfeld spielfeld object
     * @param playerNo player's number
     */
    public figur(Hauptspiel spiel, spielfeld feld, int playerNo) {
        this.spiel = spiel;
        this.feld = feld;
        this.playerNo = playerNo;

        /** create the bomb grid */
        bombGrid = new boolean[30][30];
        for (int i = 0; i < 30; i++) for (int j = 0; j < 30; j++)
            bombGrid[i][j] = false;

        int r = 0, c = 0;
        /** find player's starting position */
        switch (this.playerNo)
        {
            case 1: r = c = 1; break;
            case 2: r = c = 28; break;
            case 3: r = 28; c = 1; break;
            case 4: r = 28; c = 15;
        }
        /** calculate position */
        x = r << HauptMain.shiftCount;
        y = c << HauptMain.shiftCount;

        MediaTracker tracker = new MediaTracker(spiel);
        try {
            int counter = 0;
            /** load the images */
            for (int p = 0; p < 4; p++) {
                for (int d = 0; d < 5; d++) {
                    for (int f = 0; f < 5; f++) {
                        tracker.addImage(sprites[p][d][f], counter++);
                    }
                }
            }
            /** wait for images to finish loading */
            tracker.waitForAll();
        }
        catch (Exception e) { new Error(e); }

        /** create the key queue */
        keyQueue = new tastensave();
        /** create the key configurations array */
        keys = new int[5];
        /** load the configurations */
        for (int k = tasten.UP; k <= tasten.BOMB; k++)
            keys[k] = tasten.keys[playerNo - 1][k];
        setPriority(Thread.MAX_PRIORITY);
        /** start looping */
        start();
    }

    /**
     * Key pressed event handler.
     * @param evt key event
     */
    public void keyPressed(KeyEvent evt)
    {
        /** assume no new key is pressed */
        byte newKey = 0x00;
        /** if player isn't exploding or dead and key pressed is in player's */
        /** key list */
        if (!isExploding && !isDead &&
        evt.getKeyCode() == keys[UP] ||
        evt.getKeyCode() == keys[DOWN] ||
        evt.getKeyCode() == keys[LEFT] ||
        evt.getKeyCode() == keys[RIGHT])
        {
			/** if down key pressed */
            if (evt.getKeyCode() == keys[DOWN]) {
                newKey = BDOWN;
                /** if only the up key is pressed */
                if ((currentDirKeyDown & BUP) > 0 ||
                ((currentDirKeyDown & BLEFT) == 0 &&
                (currentDirKeyDown & BRIGHT) == 0))
                currentDirKeyDown = BDOWN;
            }
			/** if up key is pressed */
            else if (evt.getKeyCode() == keys[UP]) {
                newKey = BUP;
                /** if only the down key is pressed */
                if ((currentDirKeyDown & BDOWN) > 0 ||
                ((currentDirKeyDown & BLEFT) == 0 &&
                (currentDirKeyDown & BRIGHT) == 0))
                currentDirKeyDown = BUP;
            }
			/** if left key is pressed */
            else if (evt.getKeyCode() == keys[LEFT]) {
                newKey = BLEFT;
                /** if only the right key is pressed */
                if ((currentDirKeyDown & BRIGHT) > 0 ||
                ((currentDirKeyDown & BUP) == 0 &&
                (currentDirKeyDown & BDOWN) == 0))
                currentDirKeyDown = BLEFT;
            }
			/** if right key is pressed */
            else if (evt.getKeyCode() == keys[RIGHT]) {
                newKey = BRIGHT;
                /** if only the left is pressed */
                if ((currentDirKeyDown & BLEFT) > 0 ||
                ((currentDirKeyDown & BUP) == 0 &&
                (currentDirKeyDown & BDOWN) == 0))
                currentDirKeyDown = BRIGHT;
            }
            /** if new key isn't in the key queue */
            if (!keyQueue.contains(newKey))
            {
				/** then push it on top */
                keyQueue.push(newKey);
				/** reset keys pressed buffer */
                dirKeysDown |= newKey;
                keyPressed = true;
				/** if thread is sleeping, then wake it up */
                interrupt();
            }
        }
        /** if no direction key is pressed */
        /** and bomb key is pressed */
        if (!isExploding && !isDead &&
        evt.getKeyCode() == keys[BOMB] && !bombKeyDown && isActive)
        {
            bombKeyDown = true;
            interrupt();
        }
    }

    /**
     * Key released handler.
     * @param evt key event
     */
    public void keyReleased(KeyEvent evt)
    {
        /** if a direction key is released */
        if (!isExploding && !isDead && (
        evt.getKeyCode() == keys[UP] ||
        evt.getKeyCode() == keys[DOWN] ||
        evt.getKeyCode() == keys[LEFT] ||
        evt.getKeyCode() == keys[RIGHT]))
        {
			/** if down key is released */
            if (evt.getKeyCode() == keys[DOWN]) {
				/** remove key from the all keys down buffer */
                dirKeysDown ^= BDOWN;
				/** reset current key down */
                currentDirKeyDown ^= BDOWN;
				/** remove it from the key queue */
                keyQueue.removeItems(BDOWN);
            }
			/** if up key is released */
            else if (evt.getKeyCode() == keys[UP]) {
				/** remove key from the all keys down buffer */
                dirKeysDown ^= BUP;
				/** reset current key down */
                currentDirKeyDown ^= BUP;
				/** remove it from the key queue */
                keyQueue.removeItems(BUP);
            }
			/** if left key is released */
            else if (evt.getKeyCode() == keys[LEFT]) {
                /** remove key from the all keys down buffer */
                dirKeysDown ^= BLEFT;
                /** reset current key down */
                currentDirKeyDown ^= BLEFT;
                /** remove it from the key queue */
                keyQueue.removeItems(BLEFT);
            }
			/** if right key is released */
            else if (evt.getKeyCode() == keys[RIGHT]) {
                /** remove key from the all keys down buffer */
                dirKeysDown ^= BRIGHT;
                /** reset current key down */
                currentDirKeyDown ^= BRIGHT;
                /** remove it from the key queue */
                keyQueue.removeItems(BRIGHT);
            }
			/** if no key is currently down */
            if (currentDirKeyDown == 0)
            {
                /** see if last key pressed is still pressed or not */
                boolean keyFound = false;
                /** search for last key pressed */
                while (!keyFound && keyQueue.size() > 0) {
                    /** if key is found then exit the loop */
                    if ((keyQueue.getLastItem() & dirKeysDown) > 0) {
                        currentDirKeyDown = keyQueue.getLastItem();
                        keyFound = true;
                    }
                    /** if key is not found then pop the current key */
                    /** and on to the next one */
                    else keyQueue.pop();
                }
                /** if no key found */
                if (!keyFound)
                {
                    /** remove all keys from queue if not already removed */
                    keyQueue.removeAll();
                    /** reset key buffers */
                    currentDirKeyDown = 0x00;
                    dirKeysDown = 0x00;
                    keyPressed = false;
                    interrupt();
                }
            }
        }
        /** if the bomb key is released */
        if (!isExploding && !isDead && evt.getKeyCode() == keys[BOMB])
        {
            bombKeyDown = false;
            interrupt();
        }
    }

    /**
     * Deactivates the player so it can't be controlled.
     */
    public void deactivate()
    {
        isActive = false;
    }

    /**
     * Kills the player
     */
    public void kill()
    {
        /** is player isn't dead or isn't dieing already */
        if (!isDead && !isExploding)
        {
			/** lower players left */
            Hauptspiel.playersLeft -= 1;
			/** reset frame counter */
            frame = 0;
			/** set exploding mode */
            state = EXPLODING;
			/** make it animate */
            moving = true;
			/** prepare to explode! */
            isExploding = true;
			/** release keys */
            keyPressed = false;
            HauptMain.sndEffectPlayer.playSound("Die");
			/** wake up and die */
            interrupt();
        }
    }

	/**
     * @return x co-ordinate
     */
    public int getX() { return x; }

	/**
     * @return y co-ordinate
     */
    public int getY() { return y; }

	/**
     * @return whether player is (dead or dieing) or not
     */
    public boolean isDead() { return (isDead | isExploding); }

	/**
     * Main loop
     */
    public void run()
    {
		/** can move flat */
        boolean canMove;
        /** keeps track of last key state */
        boolean lastState = false;
        /** shift count */
        int shiftCount = HauptMain.shiftCount;
        /** offset size */
        int offset = 1 << (HauptMain.shiftCount / 2);
        /** block size */
        int size = HauptMain.size;
        /** half the block size */
        int halfSize = HauptMain.size / 2;
        /** temporary variables */
        int bx = 0, by = 0;
        /** unconditional loop */
        while (true) {
            /** if bomb key is down */
            if (!isExploding && !isDead && bombKeyDown && isActive) {
                /** if bombs are available */
                if ((totalBombs - usedBombs) > 0 &&
                /** and a bomb isn't placed there already */
                feld.grid[x >> shiftCount][y >> shiftCount]
                != spielfeld.BOMB && !bombGrid[(x + halfSize) >>
                HauptMain.shiftCount][(y + halfSize) >>
                HauptMain.shiftCount]) {
                    usedBombs += 1;
                    bombGrid[(x + halfSize) >> HauptMain.shiftCount]
                    [(y + halfSize) >> HauptMain.shiftCount] = true;
                    /** create bomb */
                    feld.createBomb(x + halfSize, y + halfSize, playerNo);
                }
            }
            /** if other keys are down */
            if (!isExploding && !isDead && keyPressed) {
                /** store last state */
                lastState = keyPressed;
				/** increase frame */
                frame = (frame + 1) % 5;
				/** set moving to true */
                moving = true;
				/** assume can't move */
                canMove = false;
				/** make sure a key is down */
                if (dirKeysDown > 0) {
					/** if left key is down */
                    if ((currentDirKeyDown & BLEFT) > 0) {
                        state = LEFT;
						/** if west slot is empty then it can move */
                        canMove = (x % size != 0 || (y % size == 0 &&
                        (feld.grid[(x >> shiftCount) - 1][y >> shiftCount]
                        <= spielfeld.NOTHING)));

						/** if it can't move */
                        if (!canMove) {
                            int oy = 0;
							/** if it's a little bit north */
                            for (oy = -offset; oy < 0; oy += (size / 4)) {
								/** and west slot is empty */
                                if ((y + oy) % size == 0 &&
                                feld.grid[(x >> shiftCount) - 1]
                                [(y + oy) >> shiftCount] <= spielfeld.NOTHING) {
									/** then move anyway */
                                    canMove = true; break;
                                }
                            }
							/** if it still can't move */
                            if (!canMove) {
                                /** if it's a little bit south */
                                for (oy = (size / 4); oy <= offset;
                                oy += (size / 4)) {
                                    /** and west slot is empty */
                                    if ((y + oy) % size == 0 &&
                                    feld.grid[(x >> shiftCount) - 1]
                                    [(y + oy) >> shiftCount]
                                    <= spielfeld.NOTHING) {
                                        /** move anyway */
                                        canMove = true; break;
                                    }
                                }
                            }
							/** if it can move now */
                            if (canMove) {
                                /** clear original spot */
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                /** move up or down */
                                y += oy;
                                /** redraw the sprite */
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
						/** if it can move */
                        if (canMove) {
							/** clear original spot */
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
							/** move left */
                            x -= (size / 4);
							/** redraw the sprite */
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
						/** if it can't move */
                        else {
							/** refresh the sprite */
                            moving = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
					/** if the right key is down */
                    else if ((currentDirKeyDown & BRIGHT) > 0) {
                        state = RIGHT;
                        canMove = false;
						/** if east slot is empty */
                        canMove = (x % size != 0 || (y % size == 0 &&
                        (feld.grid[(x >> shiftCount) + 1][y >> shiftCount]
                        <= spielfeld.NOTHING)));
						
						/** if it can't move */
                        if (!canMove) {
                            int oy = 0;
							/** see if it's a bit south */
                            for (oy = -offset; oy < 0; oy += (size / 4)) {
                                /** and the east slot is empty */
								if ((y + oy) % size == 0 &&
                                feld.grid[(x >> shiftCount) + 1]
                                [(y + oy) >> shiftCount] <= spielfeld.NOTHING) {
									/** move it */
                                    canMove = true; break;
                                }
                            }
							/** if it still can't move */
                            if (!canMove) {
								/** see if it's a bit north */
                                for (oy = (size / 4); oy <= offset;
                                oy += (size / 4)) {
									/** and the east slot if empty */
                                    if ((y + oy) % size == 0 &&
                                    feld.grid[(x >> shiftCount) + 1]
                                    [(y + oy) >> shiftCount]
                                    <= spielfeld.NOTHING) {
										/** move it */
                                        canMove = true; break;
                                    }
                                }
                            }
							/** if it can move now */
                            if (canMove) {
                                /** clear original spot */
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                /** move up or down */
                                y += oy;
                                /** refresh the sprite */
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
                        /** if it can move */
                        if (canMove) {
                            /** clear original spot */
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                            /** move right */
                            x += (size / 4);
                            /** refresh the sprite */
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                        /** if it can't move */
                        else {
                            moving = false;
                            /** refresh the sprite */
                            spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
					/** if up key is down */
                    else if ((currentDirKeyDown & BUP) > 0) {
                        state = UP;
                        canMove = false;
						/** if north slot is empty */
                        canMove = (y % size != 0 || (x % size == 0 &&
                        (feld.grid[x >> shiftCount][(y >> shiftCount) - 1]
                        <= spielfeld.NOTHING)));

						/** if it can't move */
                        if (!canMove) {
                            int ox = 0;
							/** see if it's a bit to the left */
                            for (ox = -offset; ox < 0; ox += (size / 4)) {
                                /** and the north slot is empty */
								if ((x + ox) % size == 0 &&
                                feld.grid[(x + ox) >> shiftCount]
                                [(y >> shiftCount) - 1] <= spielfeld.NOTHING) {
                                    canMove = true; break;
                                }
                            }
							/** if it still can't move */
                            if (!canMove) {
								/** see if it's a bit to the right */
                                for (ox = (size / 4); ox <= offset; ox += (size / 4)) {
                                    /** and the north block is empty */
									if ((x + ox) % size == 0 &&
                                    feld.grid[(x + ox) >> shiftCount]
                                    [(y >> shiftCount) - 1]
                                    <= spielfeld.NOTHING) {
                                        canMove = true; break;
                                    }
                                }
                            }
							/** if it can move */
                            if (canMove) {
                                /** clear original block */
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                /** move right */
                                x += ox;
                                /** refresh the sprite */
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
						/** if it can move */
                        if (canMove) {
                            /** clear original block */
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                            /** move up */
                            y -= (size / 4);
                            /** refresh the sprite */
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                        /** if it can't move */
                        else {
                            /** refresh the block */
                            moving = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
					/** if the down is is down */
                    else if ((currentDirKeyDown & BDOWN) > 0)
                    {
                        state = DOWN;
                        canMove = false;
						/** if the south block is empty */
                        canMove = (y % size != 0 || (x % size == 0 &&
                        (feld.grid[x >> shiftCount][(y >> shiftCount) + 1]
                        <= spielfeld.NOTHING)));

						/** if it can't move */
                        if (!canMove) {
                            int ox = 0;
							/** see if it's a bit to the west */
                            for (ox = -offset; ox < 0; ox += (size / 4)) {
                                /** and the south block is empty */
								if ((x + ox) % size == 0 &&
                                feld.grid[(x + ox) >> shiftCount]
                                [(y >> shiftCount) + 1] <= spielfeld.NOTHING) {
                                    canMove = true; break;
                                }
                            }
							/** if it still can't move */
                            if (!canMove) {
								/** see if it's a bit to the east */
                                for (ox = (size / 4); ox <= offset;
                                ox += (size / 4)) {
									/** and the south block is empty */
                                    if ((x + ox) % size == 0 &&
                                    feld.grid[(x + ox) >> shiftCount]
                                    [(y >> shiftCount) + 1]
                                    <= spielfeld.NOTHING) {
                                        canMove = true; break;
                                    }
                                }
                            }
							/** if it can move now */
                            if (canMove) {
								/** clear orignal block */
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
								/** move left or right */
                                x += ox;
								/** refresh the sprite */
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
						/** if it can move now */
                        if (canMove) {
							/** clear original spot */
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
							/** move down */
                            y += (size / 4);
							/** refresh the sprite */
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
						/** if it can't move */
                        else {
							/** refresh the sprite */
                            moving = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
                }
            }
			/** if all keys are up */
            else if (!isExploding && !isDead && lastState != keyPressed)
            {
				/** reset frame to 0 */
                frame = 0;
                moving = false;
				/** refresh sprite */
                spiel.paintImmediately(x, y - halfSize, width, height);
                lastState = keyPressed;
            }
            /** if it's exploding */
            else if (!isDead && isExploding)
            {
                /** if frame reached 4 then it's dead */
                if (frame >= 4) isDead = true;
				/** refresh sprite */
                spiel.paintImmediately(x, y - halfSize, width, height);
				/** rotate frame count */
                frame = (frame + 1) % 5;
            }
            /** if it's dead */
            else if (isDead)
            {
                /** clear the block */
                clear = true;
                spiel.paintImmediately(x, y - halfSize, width, height);
                /** exit the loop */
                break;
            }
            /** see if the player stepped on any bonuses */
            /** try normal position */
            if (feld.bonusGrid[x >> shiftCount][y >> shiftCount] != null)
               { bx = x; by = y; }
			/** try a bit to the north */
            else if (feld.bonusGrid[x >> shiftCount][(y + halfSize)
                 >> shiftCount] != null) { bx = x; by = y + halfSize; }
			/** try a bit to the left */
            else if (feld.bonusGrid[(x + halfSize) >> shiftCount][y
                 >> shiftCount] != null) { bx = x + halfSize; by = y; }
			/** if the player did step on a bonus */
            if (bx != 0 && by != 0) {
                feld.bonusGrid[bx >> shiftCount][by >>
                shiftCount].giveToPlayer(playerNo);
                bx = by = 0;
            }
            /** see if the player stepped on any exit */
            /** try normal position */
            if (feld.exitGrid[x >> shiftCount][y >> shiftCount] != null)
               { bx = x; by = y; }
            /** try a bit to the north */
			else if (feld.exitGrid[x >> shiftCount][(y + halfSize)
                 >> shiftCount] != null) { bx = x; by = y + halfSize; }
            /** try a bit to the left */
			else if (feld.exitGrid[(x + halfSize) >> shiftCount][y
                 >> shiftCount] != null) { bx = x + halfSize; by = y; }
			/** if the player did step on a exit */
            if (bx != 0 && by != 0) {
                feld.exitGrid[bx >> shiftCount][by >>
                shiftCount].giveToPlayer(playerNo);
                bx = by = 0;
            }
            /** if it's dead, then exit the loop */
            if (isDead) break;
			/** delay 65 milliseconds */
            try { sleep(65); } catch (Exception e) { }
        }
        interrupt();
    }

    /**
     * Drawing method.
     */
    public void paint(Graphics graphics)
    {
        Graphics g = graphics;
			/** if player isn't dead and clear mode isn't on */
            if (!isDead && !clear)
            {
                /** if moving */
				if (moving)
					/** draw the animating image */
                   g.drawImage(sprites[playerNo - 1][state][frame],
                   x, y - (HauptMain.size / 2), width, height, null);
                /** if not moving */
				else
					/** draw the still image */
                    g.drawImage(sprites[playerNo - 1][state][0],
                    x, y - (HauptMain.size / 2), width, height, null);
            }
        }

    /**
     * Drawing method for Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        g2.setRenderingHints((RenderingHints)hints);
		/** if player isn't dead and clear mode isn't on */
        if (!isDead && !clear)
        {
			/** if moving */
            if (moving)
		       /** draw the animating image */
               g2.drawImage(sprites[playerNo - 1][state][frame],
               x, y - (HauptMain.size / 2), width, height, null);
			/** if not moving */
            else
				/** draw the still image */
                g2.drawImage(sprites[playerNo - 1][state][0],
                x, y - (HauptMain.size / 2), width, height, null);
        }
    }
}