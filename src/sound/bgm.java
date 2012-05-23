package sound;
import haupt.HauptMain;
import haupt.Main;

import java.io.*;

import einstellung.Error;




public class bgm {

    private static Object player;
    private static int lastSelection = -1;  /** letzte Musik Datei die gespielt wurde **/

    static {
        if (Main.J2) {
           /** erstelle Sound Objekt und lade die Musik Dateien **/
           try {
               player = new sound(
           new File(HauptMain.RP + "Sounds/BomberBGM/").
           getCanonicalPath());
           }
           catch (Exception e) { new Error(e); }
           ((sound)player).open();
        }
    }


	
    public static void change(String arg) {
        if (Main.J2) {
            /** ändere die Musik nur wenn die aktuelle Musik nicht die gleiche ist wie die angegebene Musik **/
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


    public static void stop()
    {
        if (Main.J2) {
           ((sound)player).controlStop();
        }
    }


    public static void mute()
    {
        if (Main.J2) {
           ((sound)player).mute();
        }
    }


    public static void unmute()
    {
        if (Main.J2) {
            ((sound)player).unmute();
        }
    }
}