package Spielfeld;
import haupt.HauptMain;
import haupt.Hauptspiel;
import haupt.Main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import einstellung.Error;
import einstellung.tasten;
import einstellung.tastensave;

import java.io.*;
import java.lang.Integer;


public class figur extends Thread {
    /** spiel objekt */
    public Hauptspiel spiel = null;
    /** feld objekt */
    private spielfeld feld = null;
    /** player's own bomb grid (must have for synchronization) */
    public boolean[][] bombGrid = null;
    /** Tasteneingabe */
    private tastensave keyQueue = null;
    /** Taste für Bombe gedrückt oder nicht */
    private boolean bombKeyDown = false;
    /** Richtungstaste gedrückt */
    private byte dirKeysDown = 0x00;
    /** aktuelle Richtungtaste gedrückt */
    private byte currentDirKeyDown = 0x00;
    /** figur breite */
    private final int width = HauptMain.size;
    /** figur höhe */
    private final int height = 44 / (32 / HauptMain.size);
    /** ist explodiert */
    private boolean isExploding = false;
    /** ist TOT */
    private boolean isDead = false;
    /** egal ob eine Taste gedrückt wird oder nicht */
    private boolean keyPressed = false;
    /** Eingabetasten des Spielers */
    private int[] keys = null;
    /** Insgesamt Bomben die der Spieler hat */
    public int totalBombs = 1;
    /** Insgesamt Bombe die der Spieler benutzt */
    public int usedBombs = 0;
    /** Feuerrate */
    public int fireLength = 2;
    /** Wenn Spieler am leben ist */
    public boolean isActive = true;
    /** Spielerposition */
    public int x = 0;
    public int y = 0;
    /** Spielernummer */
    private int playerNo = 0;
    /** Spielfigur steht zum Spieler */
    private int state = DOWN;
    /** Ablegen : egal wenn der Spieler steht oder nicht */
    private boolean moving = false;

    private int frame = 0;

    private boolean clear = false;

    /** byte aufzählung */
    private static final byte BUP = 0x01;
    private static final byte BDOWN = 0x02;
    private static final byte BLEFT = 0x04;
    private static final byte BRIGHT = 0x08;
    private static final byte BBOMB = 0x10;
    /** nummer aufzählung */
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int BOMB = 4;
    private static final int EXPLODING = 4;
    /** Alle spielerbilder */
    private static Image[][][] sprites = null;
    /** Rendern */
    private static Object hints = null;

    static {
        if (Main.J2) {
            /** erstellt besseren output */
            RenderingHints h = null;
            h = new RenderingHints(null);
            h.put(RenderingHints.KEY_TEXT_ANTIALIASING,
             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            h.put(RenderingHints.KEY_FRACTIONALMETRICS,
             RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            h.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
             RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            h.put(RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
            h.put(RenderingHints.KEY_COLOR_RENDERING,
             RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            hints = (RenderingHints)h;
        }

        /** Erstelle Bild */
        sprites = new Image[4][5][5];
        int[] states = { UP, DOWN, LEFT, RIGHT, EXPLODING };
        Toolkit tk = Toolkit.getDefaultToolkit();
        String path = new String();
        /** Öffne Verzeichnis */
        try {
            for (int p = 0; p < 4; p++) {
                for (int d = 0; d < 5; d++) {
                    for (int f = 0; f < 5; f++) {
                        /** Dateiname generieren */
                        path = HauptMain.RP + "Images/";
                        path += "Bombermans/Player " + (p + 1) + "/";
                        path += states[d] + "" + (f + 1) + ".gif";
                        /** Datei öffnen */
                        sprites[p][d][f] = tk.getImage(
                        new File(path).getCanonicalPath());
                    }
                }
            }
        }
        catch (Exception e) { new Error(e); }
    }

    /**
     * Erstellt Spieler.
     * @param spiel spiel Objekt
     * @param feld feld Objekt
     * @param playerNo SpielerNr
     */
    public figur(Hauptspiel spiel, spielfeld feld, int playerNo) {
        this.spiel = spiel;
        this.feld = feld;
        this.playerNo = playerNo;

        /** Erstellt Bombenfeld */
        bombGrid = new boolean[30][30];
        for (int i = 0; i < 30; i++) for (int j = 0; j < 30; j++)
            bombGrid[i][j] = false;

        int r = 0, c = 0;
        /** findet die Position des Spielers */
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
            /** Lade Bilder */
            for (int p = 0; p < 4; p++) {
                for (int d = 0; d < 5; d++) {
                    for (int f = 0; f < 5; f++) {
                        tracker.addImage(sprites[p][d][f], counter++);
                    }
                }
            }
            /** warte bis Bilder geladet haben */
            tracker.waitForAll();
        }
        catch (Exception e) { new Error(e); }

        /** erstelle die tasteneingabe */
        keyQueue = new tastensave();
        /** tasten konfiguration */
        keys = new int[5];
        /** lade die einstellung */
        for (int k = tasten.UP; k <= tasten.BOMB; k++)
            keys[k] = tasten.keys[playerNo - 1][k];
        setPriority(Thread.MAX_PRIORITY);
        /** starte wiederholung */
        start();
    }

    /**
     * Taste gedrückt event handler.
     * @param evt tasten event
     */
    public void keyPressed(KeyEvent evt)
    {
        /** bis gedrückt nichts übernehmen */
        byte newKey = 0x00;
        /** befehl wenn spieler nicht tot oder explodiert ist */

        if (!isExploding && !isDead &&
        evt.getKeyCode() == keys[UP] ||
        evt.getKeyCode() == keys[DOWN] ||
        evt.getKeyCode() == keys[LEFT] ||
        evt.getKeyCode() == keys[RIGHT])
        {
            if (evt.getKeyCode() == keys[DOWN]) {
                newKey = BDOWN;
                /** if only the up key is pressed */
                if ((currentDirKeyDown & BUP) > 0 ||
                ((currentDirKeyDown & BLEFT) == 0 &&
                (currentDirKeyDown & BRIGHT) == 0))
                currentDirKeyDown = BDOWN;
            }
            else if (evt.getKeyCode() == keys[UP]) {
                newKey = BUP;
                /** if only the down key is pressed */
                if ((currentDirKeyDown & BDOWN) > 0 ||
                ((currentDirKeyDown & BLEFT) == 0 &&
                (currentDirKeyDown & BRIGHT) == 0))
                currentDirKeyDown = BUP;
            }
            else if (evt.getKeyCode() == keys[LEFT]) {
                newKey = BLEFT;
                /** if only the right key is pressed */
                if ((currentDirKeyDown & BRIGHT) > 0 ||
                ((currentDirKeyDown & BUP) == 0 &&
                (currentDirKeyDown & BDOWN) == 0))
                currentDirKeyDown = BLEFT;
            }
            else if (evt.getKeyCode() == keys[RIGHT]) {
                newKey = BRIGHT;
                /** if only the left is pressed */
                if ((currentDirKeyDown & BLEFT) > 0 ||
                ((currentDirKeyDown & BUP) == 0 &&
                (currentDirKeyDown & BDOWN) == 0))
                currentDirKeyDown = BRIGHT;
            }
            /** wenn taste nicht in der liste */
            if (!keyQueue.contains(newKey))
            {
                keyQueue.push(newKey);
                dirKeysDown |= newKey;
                keyPressed = true;
                interrupt();
            }
        }
        /** wenn keine richtung gedrückt wurde */
        /** und bombentaste gedrückt */
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
        /** wenn eine Richtungstaste losgelassen wird */
        if (!isExploding && !isDead && (
        evt.getKeyCode() == keys[UP] ||
        evt.getKeyCode() == keys[DOWN] ||
        evt.getKeyCode() == keys[LEFT] ||
        evt.getKeyCode() == keys[RIGHT]))
        {

            if (evt.getKeyCode() == keys[DOWN]) {

                dirKeysDown ^= BDOWN;

                currentDirKeyDown ^= BDOWN;

                keyQueue.removeItems(BDOWN);
            }

            else if (evt.getKeyCode() == keys[UP]) {

                dirKeysDown ^= BUP;

                currentDirKeyDown ^= BUP;

                keyQueue.removeItems(BUP);
            }

            else if (evt.getKeyCode() == keys[LEFT]) {

                dirKeysDown ^= BLEFT;

                currentDirKeyDown ^= BLEFT;

                keyQueue.removeItems(BLEFT);
            }

            else if (evt.getKeyCode() == keys[RIGHT]) {

                dirKeysDown ^= BRIGHT;

                currentDirKeyDown ^= BRIGHT;

                keyQueue.removeItems(BRIGHT);
            }

            if (currentDirKeyDown == 0)
            {

                boolean keyFound = false;

                while (!keyFound && keyQueue.size() > 0) {

                    if ((keyQueue.getLastItem() & dirKeysDown) > 0) {
                        currentDirKeyDown = keyQueue.getLastItem();
                        keyFound = true;
                    }

                    else keyQueue.pop();
                }

                if (!keyFound)
                {

                    keyQueue.removeAll();

                    currentDirKeyDown = 0x00;
                    dirKeysDown = 0x00;
                    keyPressed = false;
                    interrupt();
                }
            }
        }
        /** wenn bombentaste losgelassen wird */
        if (!isExploding && !isDead && evt.getKeyCode() == keys[BOMB])
        {
            bombKeyDown = false;
            interrupt();
        }
    }

    /**
     * Spieler deaktivieren.
     */
    public void deactivate()
    {
        isActive = false;
    }

    /**
     * Spieler töten
     */
    public void kill()
    {
        /** wenn spieler nicht tot ist */
        if (!isDead && !isExploding)
        {
            Hauptspiel.playersLeft -= 1;
            frame = 0;

            state = EXPLODING;

            moving = true;

            isExploding = true;

            keyPressed = false;
            HauptMain.sndEffectPlayer.playSound("Die");

            interrupt();
        }
    }

    public int getX() { return x; }

    public int getY() { return y; }


    public boolean isDead() { return (isDead | isExploding); }

    public void run()
    {

        boolean canMove;

        boolean lastState = false;

        int shiftCount = HauptMain.shiftCount;

        int offset = 1 << (HauptMain.shiftCount / 2);

        int size = HauptMain.size;

        int halfSize = HauptMain.size / 2;

        int bx = 0, by = 0;

        while (true) {
            /** wenn bombe gedrückt wurde */
            if (!isExploding && !isDead && bombKeyDown && isActive) {
                /** wenn bombe verfügbar ist */
                if ((totalBombs - usedBombs) > 0 &&
                /** und wenn keine bombe gelegt wurde */
                feld.grid[x >> shiftCount][y >> shiftCount]
                != spielfeld.BOMB && !bombGrid[(x + halfSize) >>
                HauptMain.shiftCount][(y + halfSize) >>
                HauptMain.shiftCount]) {
                    usedBombs += 1;
                    bombGrid[(x + halfSize) >> HauptMain.shiftCount]
                    [(y + halfSize) >> HauptMain.shiftCount] = true;
                    /** erstelle die bombe */
                    feld.createBomb(x + halfSize, y + halfSize, playerNo);
                }
            }
            /** wenn andere tasten gedrückt wurde */
            if (!isExploding && !isDead && keyPressed) {
                /** speichere letzte instanz*/
                lastState = keyPressed;
                frame = (frame + 1) % 5;
                moving = true;
                canMove = false;
                if (dirKeysDown > 0) {
                    if ((currentDirKeyDown & BLEFT) > 0) {
                        state = LEFT;
                        canMove = (x % size != 0 || (y % size == 0 &&
                        (feld.grid[(x >> shiftCount) - 1][y >> shiftCount]
                        <= spielfeld.NOTHING)));


                        if (!canMove) {
                            int oy = 0;

                            for (oy = -offset; oy < 0; oy += (size / 4)) {

                                if ((y + oy) % size == 0 &&
                                feld.grid[(x >> shiftCount) - 1]
                                [(y + oy) >> shiftCount] <= spielfeld.NOTHING) {

                                    canMove = true; break;
                                }
                            }

                            if (!canMove) {
   
                                for (oy = (size / 4); oy <= offset;
                                oy += (size / 4)) {

                                    if ((y + oy) % size == 0 &&
                                    feld.grid[(x >> shiftCount) - 1]
                                    [(y + oy) >> shiftCount]
                                    <= spielfeld.NOTHING) {

                                        canMove = true; break;
                                    }
                                }
                            }

                            if (canMove) {
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                y += oy;
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
                        if (canMove) {
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                            x -= (size / 4);
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                        else {
                            moving = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
                    else if ((currentDirKeyDown & BRIGHT) > 0) {
                        state = RIGHT;
                        canMove = false;
                        canMove = (x % size != 0 || (y % size == 0 &&
                        (feld.grid[(x >> shiftCount) + 1][y >> shiftCount]
                        <= spielfeld.NOTHING)));

                        if (!canMove) {
                            int oy = 0;
                            for (oy = -offset; oy < 0; oy += (size / 4)) {
                                if ((y + oy) % size == 0 &&
                                feld.grid[(x >> shiftCount) + 1]
                                [(y + oy) >> shiftCount] <= spielfeld.NOTHING) {
                                    canMove = true; break;
                                }
                            }
                            if (!canMove) {

                                for (oy = (size / 4); oy <= offset;
                                oy += (size / 4)) {
                                    if ((y + oy) % size == 0 &&
                                    feld.grid[(x >> shiftCount) + 1]
                                    [(y + oy) >> shiftCount]
                                    <= spielfeld.NOTHING) {
                                        canMove = true; break;
                                    }
                                }
                            }
                            if (canMove) {
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                y += oy;
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
                        if (canMove) {
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                            x += (size / 4);
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                        else {
                            moving = false;
                            spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
                    else if ((currentDirKeyDown & BUP) > 0) {
                        state = UP;
                        canMove = false;
                        canMove = (y % size != 0 || (x % size == 0 &&
                        (feld.grid[x >> shiftCount][(y >> shiftCount) - 1]
                        <= spielfeld.NOTHING)));

                        if (!canMove) {
                            int ox = 0;
                            for (ox = -offset; ox < 0; ox += (size / 4)) {
                                if ((x + ox) % size == 0 &&
                                feld.grid[(x + ox) >> shiftCount]
                                [(y >> shiftCount) - 1] <= spielfeld.NOTHING) {
                                    canMove = true; break;
                                }
                            }
                            if (!canMove) {
                                for (ox = (size / 4); ox <= offset; ox += (size / 4)) {
                                    if ((x + ox) % size == 0 &&
                                    feld.grid[(x + ox) >> shiftCount]
                                    [(y >> shiftCount) - 1]
                                    <= spielfeld.NOTHING) {
                                        canMove = true; break;
                                    }
                                }
                            }
                            if (canMove) {
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                x += ox;
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
                        if (canMove) {
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                            y -= (size / 4);
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                        else {
                            moving = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
                    else if ((currentDirKeyDown & BDOWN) > 0)
                    {
                        state = DOWN;
                        canMove = false;
                        canMove = (y % size != 0 || (x % size == 0 &&
                        (feld.grid[x >> shiftCount][(y >> shiftCount) + 1]
                        <= spielfeld.NOTHING)));

                        if (!canMove) {
                            int ox = 0;
                            for (ox = -offset; ox < 0; ox += (size / 4)) {
                                if ((x + ox) % size == 0 &&
                                feld.grid[(x + ox) >> shiftCount]
                                [(y >> shiftCount) + 1] <= spielfeld.NOTHING) {
                                    canMove = true; break;
                                }
                            }
                            if (!canMove) {
                                for (ox = (size / 4); ox <= offset;
                                ox += (size / 4)) {
                                    if ((x + ox) % size == 0 &&
                                    feld.grid[(x + ox) >> shiftCount]
                                    [(y >> shiftCount) + 1]
                                    <= spielfeld.NOTHING) {
                                        canMove = true; break;
                                    }
                                }
                            }
                            if (canMove) {
                                clear = true; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                                x += ox;
                                clear = false; spiel.paintImmediately(x,
                                y - halfSize, width, height);
                            }
                        }
                        if (canMove) {
                            clear = true; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                            y += (size / 4);
                            clear = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                        else {
                            moving = false; spiel.paintImmediately(x,
                            y - halfSize, width, height);
                        }
                    }
                }
            }
            else if (!isExploding && !isDead && lastState != keyPressed)
            {
                frame = 0;
                moving = false;
                spiel.paintImmediately(x, y - halfSize, width, height);
                lastState = keyPressed;
            }
            /** wenn es explodiert */
            else if (!isDead && isExploding)
            {
                /** wenn bestimmte anzahl erreicht dann tot */
                if (frame >= 4) isDead = true;
                spiel.paintImmediately(x, y - halfSize, width, height);
                frame = (frame + 1) % 5;
            }
            /** wenn tot */
            else if (isDead)
            {
                /** lösche block */
                clear = true;
                spiel.paintImmediately(x, y - halfSize, width, height);
                /** exit */
                break;
            }
            /** wenn spieler trettet auf ein bonus */
            if (feld.bonusGrid[x >> shiftCount][y >> shiftCount] != null)
               { bx = x; by = y; }
            else if (feld.bonusGrid[x >> shiftCount][(y + halfSize)
                 >> shiftCount] != null) { bx = x; by = y + halfSize; }
            else if (feld.bonusGrid[(x + halfSize) >> shiftCount][y
                 >> shiftCount] != null) { bx = x + halfSize; by = y; }

            if (bx != 0 && by != 0) {
                feld.bonusGrid[bx >> shiftCount][by >>
                shiftCount].giveToPlayer(playerNo);
                bx = by = 0;
            }
            /** wenn tot exit */
            if (isDead) break;
            try { sleep(65); } catch (Exception e) { }
        }
        interrupt();
    }

    /**
     * Zeichne Methode.
     */
    public void paint(Graphics graphics)
    {
        Graphics g = graphics;
        if (Main.J2) { paint2D(graphics); }
        else {
            if (!isDead && !clear)
            {
                if (moving)
                   g.drawImage(sprites[playerNo - 1][state][frame],
                   x, y - (HauptMain.size / 2), width, height, null);
                else
                    g.drawImage(sprites[playerNo - 1][state][0],
                    x, y - (HauptMain.size / 2), width, height, null);
            }
        }
    }

    /**
     * Zeichne Methode für Java 2's Graphics2D
     * @param graphics graphics handle
     */
    public void paint2D(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        g2.setRenderingHints((RenderingHints)hints);
        if (!isDead && !clear)
        {
            if (moving)
               g2.drawImage(sprites[playerNo - 1][state][frame],
               x, y - (HauptMain.size / 2), width, height, null);
            else
                g2.drawImage(sprites[playerNo - 1][state][0],
                x, y - (HauptMain.size / 2), width, height, null);
        }
    }
}