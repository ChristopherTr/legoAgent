/**
 * Demo Project to test the hardware of the roboter
 */
package leImRo;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;

/**
 * @author trdrc
 *
 */
public class Main {

	static RegulatedMotor XMotor = Motor.A;
	static RegulatedMotor YMotor = Motor.B;
	static EV3ColorSensor Sensor;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//Settings for the Mechanic
		int YMAX = 946;			//maximum Degree for the Y-Axis, X is unlimited
		int SPEED = 500;		//Speed for the motors [degrees/s]
		int ACCELERATION = 300; //maximum acceleration for the Motors [degrees/s²]
		int Y_DEGREES_PER_PIXEL = 44;	//Specify how big the movement between two pixel shall be
		int X_DEGREES_PER_PIXEL = 50;
		int PIXEL = YMAX / Y_DEGREES_PER_PIXEL;	//Pixel count with the given constraints
		//create array to hold the scanned image
		int[][] Image  = new int[43][43];
		boolean dir = false;	//variable to detrmine the direction of the y axis

		//instantiate Sensors and Motors
		Sensor = new EV3ColorSensor(SensorPort.S4);
		XMotor.resetTachoCount();	//reset the internal degree reference of the motors
		YMotor.resetTachoCount();
		XMotor.setSpeed(SPEED);
		YMotor.setSpeed(SPEED);
		XMotor.setAcceleration(ACCELERATION);
		YMotor.setAcceleration(ACCELERATION);
		YMotor.setStallThreshold(3, 5);	//TODO: Find good values

		// get an instance of this sensor in measurement mode
		SampleProvider RgbSample = Sensor.getRGBMode();
		SampleProvider RgbFilter = new MedianFilter(RgbSample, 5);
		// Initialize an array of floats for fetching samples
		float[] Rgb = new float[RgbFilter.sampleSize()];

		// TODO Auto-generated method stub
		LCD.drawString("Sensor Test", 0, 0);
		Delay.msDelay(1000);

		// For loop for the x-axis
		for (int j = 0; j < PIXEL; j++) 
		{
			
			// For loop for the y-axis
			for (int i = 0; i < PIXEL; i++) 
			{
				int y_index;	//helping variable for image array filling
				if(dir)
				{
					YMotor.rotate(Y_DEGREES_PER_PIXEL);
					if (YMotor.isStalled()) 
					{
						LCD.drawString("Stalled", 0, 7);
					}
					y_index = PIXEL - i -1;
				}
				else
				{
					YMotor.rotate((-1) * Y_DEGREES_PER_PIXEL);
					y_index = i;
				}
				Delay.msDelay(1);
				// measure 5 Times to calculate median value
				RgbFilter.fetchSample(Rgb, 0);
				RgbFilter.fetchSample(Rgb, 0);
				RgbFilter.fetchSample(Rgb, 0);
				RgbFilter.fetchSample(Rgb, 0);
				RgbFilter.fetchSample(Rgb, 0);
				// fetch a sample

				// update screen
				LCD.drawString("X: ", 0, 1);
				LCD.drawInt(XMotor.getTachoCount(), 4, 1);

				LCD.drawString("Y: ", 0, 2);
				LCD.drawInt(YMotor.getTachoCount(), 4, 2);

				LCD.drawString("C: ", 0, 3);
				LCD.drawString(String.valueOf(Rgb[0]), 4, 3);
				LCD.drawString(String.valueOf(Rgb[1]), 4, 4);
				LCD.drawString(String.valueOf(Rgb[2]), 4, 5);
				
				if (getPixel(Rgb) == 1) {
					LCD.drawString("Weiss    ", 1, 6);
					//System.out.print("#");
				} else {
					LCD.drawString("Schwarz  ", 1, 6);
					//System.out.print(" ");
				}
				Image[j][y_index] = getPixel(Rgb);
			}
			
			XMotor.rotate(X_DEGREES_PER_PIXEL);
			//invert direction for the Y-Axis
			dir = !dir;
			//System.out.println();
		}

		//show image
		LCD.clearDisplay();
		for (int k = 0; k < PIXEL; k++) 
		{
			for (int l = 0; l < PIXEL; l++) 
			{
				LCD.setPixel(k, l, (int) Image[k][l]); 
			}
		}
		Delay.msDelay(30000);
	}

	private static int getPixel(float[] rgb) {
		float sum = (rgb[0] + rgb[1] + rgb[2]) / 3;
		if (sum < 0.12) {
			return 0;
		}
		return 1;
	}

}
