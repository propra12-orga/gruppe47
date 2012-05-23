package Spielfeld;

/** Diese Klasse ist daf�r da um die Items, Bomben, Bl�cke zu generieren **/
public class Rand {
	/** Niedrigste Zahl im Bereich */
    private int low = 0;
    /** H�chste Zahl im Bereich */
    private int high = 0;
    /** stack gr��e */
    private static final int BUFFER_SIZE = 101;
    /** stack zum halten der zuf�lligen Doubles */
    private static double[] buffer = new double[BUFFER_SIZE];
    
    
    /** F�lle den stack mit 101 zuf�lligen double **/
    static {
        for (int i = 0; i < BUFFER_SIZE; i++)
            buffer[i] = java.lang.Math.random();
    }

    /**
     * Konstruiert ein Objekt das zuf�llige integers im bestimmten bereich erstellt.
     * @param low kleinste integer im bereich
     * @param high gr��te integer im bereich
     */
    public Rand(int low, int high) {
        this.low = low;
        this.high = high;
    }
    
    /**
     * Nehme den n�chsten zuf�lligen double aus dem stack und generiere ein integer daraus.
     * @return ein zuf�lliger integer
     */
    public int draw() {
        int result = low + (int)((high - low + 1) * nextRandom());
        return result;
    }

    /**
     * Ziehe einen zuf�lligen Element aus dem stack und speichere den Wert in eine
     * variable dann f�lle das element mit einem neuen zuf�lligen double.
     * @return eine zuf�llige double
     */
    private static double nextRandom() {
        /** ziehe ein zuf�lliges element aus dem stack */
        int position = (int)(java.lang.Math.random() * BUFFER_SIZE);
        if (position == BUFFER_SIZE)
            position = BUFFER_SIZE - 1;
        /** speichere den Wert des Elements */
        double result = buffer[position];
        /** f�lle das Element mit einem neuen zuf�lligen double */
        buffer[position] = java.lang.Math.random();
        /** return den Wert */
        return result;
    }
}