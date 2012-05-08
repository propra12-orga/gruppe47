

public class spielfeld {
	public static void main(String[] args) {

	int hoehe = Integer.parseInt(args[0]);
	int breite = Integer.parseInt(args[1]);
	char[][] spielfeld = new char [hoehe][breite];
	String begrenzung = "#";
	erzeuge(hoehe,breite,spielfeld,begrenzung);
	darstelle(hoehe,breite,spielfeld);
	}
	public static void erzeuge(int hoehe, int breite, char [][] spielfeld, String begrenzung){
		for(int i = 0; i < hoehe; i++)
		{
			for (int j = 0; j < breite; j++)
			{
				if (i == 0 | i == (hoehe-1) )
				{
					spielfeld [i][j] = begrenzung.charAt(0);
				}
				if (j == 1 | j == (breite-1))
				{
					spielfeld [i][j] = begrenzung.charAt(0);
				}

			}
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

