package sound;
import haupt.HauptMain;

import java.io.*;

/**
 * File:         Soundeffekt.java
 * Copyright:    Copyright (c) 2012
 * @author Martin
 * @version 1.6
 */

/**
 * This class plays the background music.
 */
public class Soundeffekt extends Thread {
    public Soundeffekt() {
        start();
    }

    public void playSound(String str) {
        
            SoundFigur sound = null;
            try {
                /** create sound player */
                sound = new SoundFigur(
                new File(HauptMain.RP +
                "Sounds/Soundeffekts/" +  str + ".mid").
                getCanonicalPath());
            }
            catch (Exception e) { }
			/** open file */
            ((SoundFigur)sound).open();
			/** then play sound */
            sound.change(0, false);
        }
    }
