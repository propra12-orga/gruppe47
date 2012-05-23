package sound;
import haupt.HauptMain;
import haupt.Main;

import java.io.*;




public class Soundeffekt extends Thread {
    public Soundeffekt() {
        start();
    }

    public void playSound(String str) {
        if (Main.J2) {
            SoundFigur sound = null;
            try {
                /** erstelle Sound Figur **/
                sound = new SoundFigur(
                new File(HauptMain.RP +
                "Sounds/Soundeffekts/" +  str + ".mid").
                getCanonicalPath());
            }
            catch (Exception e) { }
            ((SoundFigur)sound).open();  /** öffne Datei **/
            sound.change(0, false);  /** dann spiele sound ab **/
        }
    }
}