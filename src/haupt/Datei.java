package haupt;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
/**
*
* @author nForcer
*
*/
public class Datei {
/**
* Reads the xml file; converts the lines in the file to strings, then converts them to a readable
* char array
* @param pfad needs the given path to read from the xml
* @return the created char array to convert 
*/
	public char[][] read(String pfad){
		/** char array for the tokens */
		char feld[][] = new char [28][28];
		try {
			/**sax builder for reading the file */
			SAXBuilder builder = new SAXBuilder();
			/**to work with thx xml document the given path must be set */
			Document doc = builder.build(pfad);
			/** gets the root element of the xml file, so the 'brackets' of the document */
			Element spielfeld = doc.getRootElement();
			/**reads line 1 of the xml file */
			Element reihe_1 = spielfeld.getChild("zeile_1");
			String r1 = reihe_1.getText();
			/**reads line 2 of the xml file*/
			Element reihe_2= spielfeld.getChild("zeile_2");
			String r2 = reihe_2.getText();
			/**reads line 3 of the xml file*/
			Element reihe_3 = spielfeld.getChild("zeile_3");
			String r3 = reihe_3.getText();
			/**reads line 4 of the xml file*/
			Element reihe_4 = spielfeld.getChild("zeile_4");
			String r4 = reihe_4.getText();
			/**reads line 5 of the xml file*/
			/** and so on*/
			Element reihe_5 = spielfeld.getChild("zeile_5");
			String r5 = reihe_5.getText();

			Element reihe_6 = spielfeld.getChild("zeile_6");
			String r6 = reihe_6.getText();

			Element reihe_7 = spielfeld.getChild("zeile_7");
			String r7 = reihe_7.getText();

			Element reihe_8 = spielfeld.getChild("zeile_8");
			String r8 = reihe_8.getText();

			Element reihe_9 = spielfeld.getChild("zeile_9");
			String r9 = reihe_9.getText();

			Element reihe_10 = spielfeld.getChild("zeile_10");
			String r10 = reihe_10.getText();

			Element reihe_11 = spielfeld.getChild("zeile_11");
			String r11 = reihe_11.getText();

			Element reihe_12 = spielfeld.getChild("zeile_12");
			String r12 = reihe_12.getText();

			Element reihe_13 = spielfeld.getChild("zeile_13");
			String r13 = reihe_13.getText();

			Element reihe_14 = spielfeld.getChild("zeile_14");
			String r14 = reihe_14.getText();

			Element reihe_15 = spielfeld.getChild("zeile_15");
			String r15 = reihe_15.getText();

			Element reihe_16 = spielfeld.getChild("zeile_16");
			String r16 = reihe_16.getText();

			Element reihe_17 = spielfeld.getChild("zeile_17");
			String r17 = reihe_17.getText();

			Element reihe_18 = spielfeld.getChild("zeile_18");
			String r18 = reihe_18.getText();

			Element reihe_19 = spielfeld.getChild("zeile_19");
			String r19 = reihe_19.getText();

			Element reihe_20 = spielfeld.getChild("zeile_20");
			String r20 = reihe_20.getText();

			Element reihe_21 = spielfeld.getChild("zeile_21");
			String r21 = reihe_21.getText();

			Element reihe_22 = spielfeld.getChild("zeile_22");
			String r22 = reihe_22.getText();

			Element reihe_23 = spielfeld.getChild("zeile_23");
			String r23 = reihe_23.getText();

			Element reihe_24 = spielfeld.getChild("zeile_24");
			String r24 = reihe_24.getText();

			Element reihe_25 = spielfeld.getChild("zeile_25");
			String r25 = reihe_25.getText();

			Element reihe_26 = spielfeld.getChild("zeile_26");
			String r26 = reihe_26.getText();

			Element reihe_27 = spielfeld.getChild("zeile_27");
			String r27 = reihe_27.getText();

			Element reihe_28 = spielfeld.getChild("zeile_28");
			String r28 = reihe_28.getText();

			/**Fills the 2d array with the strings*/
			for (int j=0;j<28;j++)
			{
				for (int i=0;i<28;i++)
				{
					switch	(j)
					{
					case 0:
						feld[i][j]=r1.charAt(i);
						break;
					case 1:
						feld[i][j]=r2.charAt(i);
						break;
					case 2:
						feld[i][j]=r3.charAt(i);
						break;
					case 3:
						feld[i][j]=r4.charAt(i);
						break;	
					case 4:
						feld[i][j]=r5.charAt(i);
						break;
					case 5:
						feld[i][j]=r6.charAt(i);
						break;
					case 6:
						feld[i][j]=r7.charAt(i);
						break;
					case 7:
						feld[i][j]=r8.charAt(i);
						break;
					case 8:	
						feld[i][j]=r9.charAt(i);
						break;
					case 9:	
						feld[i][j]=r10.charAt(i);
						break;	
					case 10:
						feld[i][j]=r11.charAt(i);
						break;
					case 11:	
						feld[i][j]=r12.charAt(i);
						break;	
					case 12:
						feld[i][j]=r13.charAt(i);
						break;
					case 13:
						feld[i][j]=r14.charAt(i);
						break;
					case 14:
						feld[i][j]=r15.charAt(i);
						break;
					case 15:
						feld[i][j]=r16.charAt(i);
						break;
					case 16:
						feld[i][j]=r17.charAt(i);
						break;
					case 17:
						feld[i][j]=r18.charAt(i);
						break;
					case 18:	
						feld[i][j]=r19.charAt(i);
						break;
					case 19:
						feld[i][j]=r20.charAt(i);
						break;
					case 20:
						feld[i][j]=r21.charAt(i);
						break;
					case 21:
						feld[i][j]=r22.charAt(i);
						break;
					case 22:
						feld[i][j]=r23.charAt(i);
						break;
					case 23:
						feld[i][j]=r24.charAt(i);
						break;
					case 24:
						feld[i][j]=r25.charAt(i);
						break;
					case 25:
						feld[i][j]=r26.charAt(i);
						break;
					case 26:
						feld[i][j]=r27.charAt(i);
						break;
					case 27:	
						feld[i][j]=r28.charAt(i);
						break;
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		// debug printing
		for (int j=0;j<30;j++)
		{
			for (int i=0;i<30;i++)
			{
				System.out.print(feld[i][j]);
			}
			System.out.println();
		}
		return feld;
	}
}