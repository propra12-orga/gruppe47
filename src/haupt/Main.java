package haupt;

import javax.swing.*;

//import einstellung.Error;


public class Main
{
    public static HauptMain hauptMain = null;

    /** pfad */
    public static final String RP = "./";

      /**
       * Startet Bomberman
       */
       public static void startBomberman() {
    	   hauptMain = new HauptMain();
       }

    /**
    * Startet das Programm und erstellt eine Instanz vom Hauptspiel.
    */
    public static void main(String[] args)
    {
        startBomberman();
    }
}