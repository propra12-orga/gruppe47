package einstellung;
import java.awt.event.KeyEvent;
import java.io.*;


public abstract class tasten {
    /** Tasten */
    public static int[][] keys = null;
    public static final int P1 = 0;
    public static final int P2 = 1;
    public static final int P3 = 2;
    public static final int P4 = 3;
    
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int BOMB = 4;

    static {
        if (!openFile())
        {
            createDefaultFile();
            openFile();
        }
    }


    public static boolean openFile() {
        boolean result = true;
        try {
            ObjectInputStream inputStream =
            new ObjectInputStream(new FileInputStream("tastensave.dat"));
            keys = (int[][])inputStream.readObject();
            inputStream.close();
        }
        catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static void writeFile() {
        try {
            ObjectOutputStream outputStream =
            new ObjectOutputStream(new FileOutputStream("tastensave.dat"));
            outputStream.writeObject((int[][])keys);
            outputStream.close();
        }
        catch (Exception e) { new Error(e); }
    }

    public static void createDefaultFile() {
        if (keys == null) keys = new int[4][5];

        keys[P1][UP] = KeyEvent.VK_UP;
        keys[P1][DOWN] = KeyEvent.VK_DOWN;
        keys[P1][LEFT] = KeyEvent.VK_LEFT;
        keys[P1][RIGHT] = KeyEvent.VK_RIGHT;
        keys[P1][BOMB] = KeyEvent.VK_NUMPAD0;

        keys[P2][UP] = KeyEvent.VK_W;
        keys[P2][DOWN] = KeyEvent.VK_S;
        keys[P2][LEFT] = KeyEvent.VK_A;
        keys[P2][RIGHT] = KeyEvent.VK_D;
        keys[P2][BOMB] = KeyEvent.VK_SPACE;

        keys[P3][UP] = KeyEvent.VK_I;
        keys[P3][DOWN] = KeyEvent.VK_K;
        keys[P3][LEFT] = KeyEvent.VK_J;
        keys[P3][RIGHT] = KeyEvent.VK_L;
        keys[P3][BOMB] = KeyEvent.VK_BACK_SLASH;

        keys[P4][UP] = KeyEvent.VK_NUMPAD8;
        keys[P4][DOWN] = KeyEvent.VK_NUMPAD5;
        keys[P4][LEFT] = KeyEvent.VK_NUMPAD4;
        keys[P4][RIGHT] = KeyEvent.VK_NUMPAD6;
        keys[P4][BOMB] = KeyEvent.VK_NUMPAD9;

        writeFile();
    }
}