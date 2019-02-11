package leImRo;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class GUI implements IGUI {

	public static void showMenu(){
		// menu
		String[] menuEntries = { "Recognise", "Train", "Clear", "Exit" };
		int selected = 0;
		while (true) {
			LCD.clear();
			for (int i = 0; i < menuEntries.length; i++) {
				if (selected == i) {
					LCD.drawString(">", 0, i);
				}
				LCD.drawString(menuEntries[i], 2, i);
			}
			Button.waitForAnyPress();
			if (Button.DOWN.isDown()) {
				selected++;
				if (selected > menuEntries.length - 1) {
					selected = menuEntries.length - 1;
				}
			}
			if (Button.UP.isDown()) {
				selected--;
				if (selected < 0) {
					selected = 0;
				}
			}
			if (Button.ENTER.isDown()) {
				LCD.clear();
				Recognition recognition = new Recognition();
				if (selected == 0) {
					// start recognise
				} else if (selected == 1) {
					// start train
					recognition.removeAll();
					recognition.train(Figure.rectangle);
					recognition.train(Figure.rectangle);
					recognition.train(Figure.circle);
					
				} else if (selected == 2) {
					// start clear
					recognition.removeAll();
				}
				else {
					System.exit(0);
				}
			}
		}
	}

}
