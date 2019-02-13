/**
 * Startpunkt fuer die Roboter-Anwendung
 */
package leImRo;

/**
 * @author Christopher Traub
 * @author Jens Eisele
 * @author Daniel Koelbel
 * @author Benjamin Baechle
 * @author Wolf-Michael Dieter
 *
 */
public class Main {
	public static void main(String[] args) {
		//Die Klasse Recognition kappselt alle benötigten Funktionen
		Recognition recognition = new Recognition();
		
		// Das Menue wird dauerhaft angezeigt und kann über die Referenz auf die Recognition Instanz alle Programmteile starten.
		// Nach Abschluss eines Programmteils wird erneut das Menue angezeigt.
		while(true) {
			GUI.processMenu(recognition);
		}
				
	}
}
