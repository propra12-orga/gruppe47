

public class spielfeld {
	public static void main(String[] args) {

	int hoehe = Integer.parseInt(args[0]);
	int breite = Integer.parseInt(args[1]);
	char[][] spielfeld = new char [hoehe][breite];
	String begrenzung = "#";
	erzeuge(hoehe,breite,spielfeld,begrenzung);
	mauern(hoehe,breite,spielfeld, begrenzung);
	darstelle(hoehe,breite,spielfeld);
	}
	public static void erzeuge(int hoehe, int breite, char [][] spielfeld, String begrenzung){
		for(int i = 0; i < hoehe; i++)
		{
			for (int j = 0; j < breite; j++)
			{
				if (i == 0 | i == (hoehe-1) )
				{
					spielfeld [i][j] = begrenzung.charAt(0); // Erzeugung der oberen und unteren begrenzung
				}
				if (j == 0 | j == (breite-1))
				{
					spielfeld [i][j] = begrenzung.charAt(0); // Erzeugung der rechten und linken begrenzung
				}

			}
		}
	}
	public static void mauern (int hoehe, int breite, char [][] spielfeld, String begrenzung)
	{
		for (int i=0; i < (hoehe*breite/3);i++) //1/3 des spielfeldes sollen mauern enthalten (wert noch nciht endgültig)
		{
			int h = (int) (Math.random() * ((hoehe - 1) - 1) + 1); //Zufallszahl für die Höhe
			int b = (int) (Math.random() * ((breite - 1) -1) + 1); // Zufallszahl für die Breite
			spielfeld [h][b] = begrenzung.charAt(0);
		}
	}
	public static void darstelle(int hoehe, int breite, char [][] spielfeld)
	{
		for (int i=0;i<hoehe;i++)
		{
			for (int j=0;j<breite;j++)
			{
				System.out.print(spielfeld[i][j]);
			}
		System.out.println();
		}
	}
}

