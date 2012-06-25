package einstellung;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * File:         tasten.java
 * Copyright:    Copyright (c) 2012
 * @author Musab Kaya
 * @version 1.6
 */

/**
 * This class keeps track of the keyboard configration for each player.
 * This class can't be instanciated.
 */
public abstract class tasten {
    /** the keys */
    public static int[][] keys = null;
	/** player numbers enumerations */
    public static final int P1 = 0;
    public static final int P2 = 1;
    public static final int P3 = 2;
    public static final int P4 = 3;
    /** key number enumerations */
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int BOMB = 4;

	/** open the key configuration file */
    static {
		/** try to open the file and if can't open file then */
        if (!openFile())
        {
			/** create the default configuration file */
            createDefaultFile();
			/** then try to open it again */
            openFile();
        }
    }

	/**
     * Opens the configuration file.
     */
    public static boolean openFile() {
		/** setup result to be returned */
        boolean result = true;
		/** try to open the file */
        try {
			/** create the file stream object */
            ObjectInputStream inputStream =
            new ObjectInputStream(new FileInputStream("tastensave.dat"));
            /** read the file into memory */
			keys = (int[][])inputStream.readObject();
            /** close the file */
			inputStream.close();
        }
		/** if anything goes wrong, set result to to false */
        catch (Exception e) {
            result = false;
        }
		/** return the result */
        return result;
    }

	/**
     * Writes the file with current data in memory.
     */
    public static void writeFile() {
        /** try to create the file */
		try {
			/** create the file object, overwrite the file if needs to */
            ObjectOutputStream outputStream =
            new ObjectOutputStream(new FileOutputStream("tastensave.dat"));
            /** write the file */
			outputStream.writeObject((int[][])keys);
			/** close the file */
            outputStream.close();
        }
        catch (Exception e) { new Error(e); }
    }

	/**
     * Creates the configuration file with default configurations.
     */
    public static void createDefaultFile() {
        /** create the data, if it doens't exist already */
		if (keys == null) keys = new int[4][5];

		/** player 1 default key configurations */
        keys[P1][UP] = KeyEvent.VK_UP;
        keys[P1][DOWN] = KeyEvent.VK_DOWN;
        keys[P1][LEFT] = KeyEvent.VK_LEFT;
        keys[P1][RIGHT] = KeyEvent.VK_RIGHT;
        keys[P1][BOMB] = KeyEvent.VK_0;

		/** player 2 default key configurations */
        keys[P2][UP] = KeyEvent.VK_W;
        keys[P2][DOWN] = KeyEvent.VK_S;
        keys[P2][LEFT] = KeyEvent.VK_A;
        keys[P2][RIGHT] = KeyEvent.VK_D;
        keys[P2][BOMB] = KeyEvent.VK_SPACE;

		/** player 3 default key configurations */
        keys[P3][UP] = KeyEvent.VK_I;
        keys[P3][DOWN] = KeyEvent.VK_K;
        keys[P3][LEFT] = KeyEvent.VK_J;
        keys[P3][RIGHT] = KeyEvent.VK_L;
        keys[P3][BOMB] = KeyEvent.VK_BACK_SLASH;

		/** player 4 default key configurations */
        keys[P4][UP] = KeyEvent.VK_NUMPAD8;
        keys[P4][DOWN] = KeyEvent.VK_NUMPAD5;
        keys[P4][LEFT] = KeyEvent.VK_NUMPAD4;
        keys[P4][RIGHT] = KeyEvent.VK_NUMPAD6;
        keys[P4][BOMB] = KeyEvent.VK_NUMPAD9;

		/** write the file */
        writeFile();
    }
}