/**
 * Demo Project to test the hardware of the roboter
 */
package leImRo;

/**
 * @author trdrc
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Recognition recognition = new Recognition();
		
		while(true) {
			GUI.processMenu(recognition);
		}
				
	}
}
