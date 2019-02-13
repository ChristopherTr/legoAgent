package leImRo;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.TextMenu;

/**
 * 
 * Klasse zur Anzeige eines Menues zur Steuerung der Funktionen und Ausgabe von
 * Ergebnissen
 *
 */
public class GUI implements IGUI {

	public static void processMenu(Recognition recognition) {
		// Auflistung der Menupunkte
		String[] menuEntries = { "Recognise SVM", "Recognise KNN", "Train", "Clear", "Exit" };
		String[] subEntries = { "Rectangle", "Circle" };

		// Initialisierung der Menueobjekte
		TextMenu mainMenu = new TextMenu(menuEntries, 1, "Main LeImRo");
		TextMenu subMenu = new TextMenu(subEntries, 1, "Train Figure");

		Figure figure;

		LCD.clear();
		//Abfrage des Hauptmenues -> Arrayeintraege ueber Index abgefragt ("Recognise SVM" = 0)
		switch (mainMenu.select()) {
		case 0:
			// start recognize
			figure = recognition.recognizeSVM();
			LCD.clear();
			if (figure == Figure.rectangle) {
				LCD.drawString("Rectangle", 0, 0);
			} else if(figure == Figure.circle) {
				LCD.drawString("Circle", 0, 0);
			}
			else{
				LCD.drawString("Unknwown", 0, 0);				
			}
			Button.waitForAnyPress();
			break;
		case 1:
			// start recognize
			figure = recognition.recognizeKNN();
			LCD.clear();
			if (figure == Figure.rectangle) {
				LCD.drawString("Rectangle", 0, 0);
			} else if(figure == Figure.circle) {
				LCD.drawString("Circle", 0, 0);
			}
			else{
				LCD.drawString("Unknwown", 0, 0);				
			}
			Button.waitForAnyPress();
			break;
		case 2:
			LCD.clear();
			if (subMenu.select() == 0) {
				recognition.train(Figure.rectangle);
			} else {
				recognition.train(Figure.circle);
			}
			break;
		case 3:
			recognition.removeAll();
			break;
		case 4:
			System.exit(0);
			break;
		default:
			break;
		}
	}
}
