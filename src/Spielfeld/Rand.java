package Spielfeld;

/** Diese Klasse ist dafür da um die Items, Bomben, Blöcke zu generieren **/
public class Rand {
	/** Niedrigste Zahl im Bereich */
    private int low = 0;
    /** Höchste Zahl im Bereich */
    private int high = 0;
    /** stack größe */
    private static final int BUFFER_SIZE = 101;
    /** stack zum halten der zufälligen Doubles */
    private static double[] buffer = new double[BUFFER_SIZE];
    
    
    /** Fülle den stack mit 101 zufälligen double **/
    static {
        for (int i = 0; i < BUFFER_SIZE; i++)
            buffer[i] = java.lang.Math.random();
    }

    /**
     * Konstruiert ein Objekt das zufällige integers im bestimmten bereich erstellt.
     * @param low kleinste integer im bereich
     * @param high größte integer im bereich
     */
    public Rand(int low, int high) {
        this.low = low;
        this.high = high;
    }
    
    /**
     * Nehme den nächsten zufälligen double aus dem stack und generiere ein integer daraus.
     * @return ein zufälliger integer
     */
    public int draw() {
        int result = low + (int)((high - low + 1) * nextRandom());
        return result;
    }

    /**
     * Ziehe einen zufälligen Element aus dem stack und speichere den Wert in eine
     * variable dann fülle das element mit einem neuen zufälligen double.
     * @return eine zufällige double
     */
    private static double nextRandom() {
        /** ziehe ein zufälliges element aus dem stack */
        int position = (int)(java.lang.Math.random() * BUFFER_SIZE);
        if (position == BUFFER_SIZE)
            position = BUFFER_SIZE - 1;
        /** speichere den Wert des Elements */
        double result = buffer[position];
        /** fülle das Element mit einem neuen zufälligen double */
        buffer[position] = java.lang.Math.random();
        /** return den Wert */
        return result;
    }
}