package leImRo;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.TextMenu;

public class GUI implements IGUI {
	
	public static void processMenu(Recognition recognition) {
		String[] menuEntries = { "Recognise", "Train", "Clear", "Exit" };
		String[] subEntries = { "Rectangle", "Circle" };
		
		TextMenu mainMenu = new TextMenu(menuEntries, 1, "Main LeImRo");
		TextMenu subMenu = new TextMenu(subEntries, 1, "Train Figure");
		
		switch (mainMenu.select()) {
		case 0:
			// start recognize
			Figure figure = recognition.recognize();
			LCD.clear();
			if (figure == Figure.rectangle) {
				LCD.drawString("Rectangle", 0, 0);
			} else {
				LCD.drawString("Circle", 0, 0);
			}
			break;
		case 1:
			LCD.clear();
			if(subMenu.select() == 0) {
				recognition.train(Figure.rectangle);
			} else {
				recognition.train(Figure.circle);
			}
			break;
		case 2:
			recognition.removeAll();
			break;
		case 3:
			System.exit(0);
			break;
		default:
			break;
		}
	}
}
