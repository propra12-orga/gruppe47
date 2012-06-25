package sound;
import haupt.HauptMain;


import java.io.*;

import einstellung.Error;

/**
 * File:         bgm
 * Copyright:    Copyright (c) 2012
 * @author martin
 * @version 2
 */

/**
 * This class plays the background music.
 */
public class bgm {
	/** sound object */
    private static Object player;
	/** last music played */
    private static int lastSelection = -1; 

    static {
           /** create the sound object and load the music files */
           try {
               player = new sound(
           new File(HauptMain.RP + "Sounds/BomberBGM/").
           getCanonicalPath());
           }
           catch (Exception e) { new Error(e); }
           ((sound)player).open();
        }

	/**
     * Change BGM music.
     * @param arg BGM music to chagne to
     */
    public static void change(String arg) {
        
            /**
             * change music only if the the current music is not equal to
             * the specified music
             */
            int i = 0;
            while (i < ((sound)player).sounds.size() &&
            ((sound)player).sounds.elementAt(i).
            toString().indexOf(arg) < 0) i += 1;
            if (i != lastSelection && i <
               ((sound)player).sounds.size()) {
                lastSelection = i;
                ((sound)player).change(lastSelection, true);
            }
        }
}