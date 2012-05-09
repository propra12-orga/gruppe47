

public class spielfeld {
	public static void main(String[] args) {

	int x = 1;
	int y = 1;
	int hoehe = Integer.parseInt(args[0])*2;
	int breite = Integer.parseInt(args[1])*5;
	char[][] spielfeld = new char [hoehe][breite];
	String begrenzung = "#";
	String figur = "@";
	erzeuge(hoehe,breite,spielfeld,begrenzung);
	mauern(hoehe,breite,spielfeld, begrenzung);
	erzeugefigur(spielfeld,figur,x,y);
	darstelle(hoehe,breite,spielfeld);
	rechts (spielfeld,figur,x,y);
	darstelle(hoehe,breite,spielfeld);
	oben (spielfeld,figur,y,x);
	darstelle(hoehe,breite,spielfeld);
	links (spielfeld, figur, x,y);
	darstelle(hoehe,breite,spielfeld);
	unten (spielfeld,figur, y,x);
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
		for (int i=0; i < (hoehe*breite/4);i++) //1/3 des spielfeldes sollen mauern enthalten (wert noch nciht endgültig)
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

public static void erzeugefigur (char [][] spielfeld, String figur, int x, int y)
{
	spielfeld [x][y]=figur.charAt(0);
}


public static void rechts(char[][]spielfeld,String figur, int x,int y)
{
	x=x+1;
	spielfeld[y][x]=figur.charAt(0);
	
}
public static void links(char[][]spielfeld,String figur, int x,int y)
{
	x=x-1;
	spielfeld[y][x]=figur.charAt(0);
	
}
public static void unten(char[][]spielfeld,String figur, int y, int x)
{
	y=y+1;
	spielfeld[y][x]=figur.charAt(0);
	
}
public static void oben(char[][]spielfeld,String figur, int y, int x)
{
	y=y-1;
	spielfeld[y][x]=figur.charAt(0);
	
}
}