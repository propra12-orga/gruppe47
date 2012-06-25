package Spielfeld;

/**
 * File:         Rand.java
 * Copyright:    Copyright (c) 20012
 * @author Musab Kaya
 * @version 1.6
 */

/**
 * This class generates random integers within a given range by creating a
 * stack of 101 random doubles then generate random integers from the stack
 * randomly.  First, a stack of size 101 is created and filled with random
 * doubles.  Everytime an integer is drawn, an element is selected from the
 * stack randomly.  The value at the selected element is used to generate
 * a random integer then that element in the stack is filled with a
 * newly generated double.
 */
public class Rand {
	/** lowest integer in the range */
    private int low = 0;
    /** highest integer in the range */
    private int high = 0;
    /** stack size */
    private static final int BUFFER_SIZE = 101;
    /** stack to hold random doubles */
    private static double[] buffer = new double[BUFFER_SIZE];
    
    
    /**
     * Fill the stack with 101 random doubles using the built-in random double
     * generator.
     */
    static {
        for (int i = 0; i < BUFFER_SIZE; i++)
            buffer[i] = java.lang.Math.random();
    }

    /**
     * Constructs an object that generates random integers in a given range.
     * @param low the lowest integer in the range
     * @param high the highest integer in the range
     */
    public Rand(int low, int high) {
        this.low = low;
        this.high = high;
    }
    
    /**
     * Get the next random double from the stack then generate an integer from
     * it.
     * @return a random integer
     */
    public int draw() {
        int result = low + (int)((high - low + 1) * nextRandom());
        return result;
    }

    /**
     * Pick a random element in the stack and store the value of it into a
     * variable to be returned then fill that element with a new random double.
     * @return a random double
     */
    private static double nextRandom() {
        /** pick a random element in the stack */
        int position = (int)(java.lang.Math.random() * BUFFER_SIZE);
        if (position == BUFFER_SIZE)
            position = BUFFER_SIZE - 1;
        /** store the value of that element */
        double result = buffer[position];
        /** fill that element with a new random double */
        buffer[position] = java.lang.Math.random();
        /** return the value */
        return result;
    }
}