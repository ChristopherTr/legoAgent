package leImRo;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;

public class Scanner implements IScanner {

	//public variables
	public final static int resolution = 512;
	
	//private variables
	private static RegulatedMotor XMotor = Motor.A;
	private static RegulatedMotor YMotor = Motor.B;
	private static EV3ColorSensor Sensor;

	@Override
	public int[][] readImage() {
		return null;
	}

	@Override
	public IDataPoint computeTrait(int[][] image) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataPoint scanNewDataPoint() {
		return this.computeTrait(this.readImage());
	}

	/*
	 * scan the whole image
	 */
	private static void scanImage() {
		// Settings for the Mechanic
		int YMAX = 946; // maximum Degree for the Y-Axis, X is unlimited
		int SPEED = 500; // Speed for the motors [degrees/s]
		int ACCELERATION = 300; // maximum acceleration for the Motors [degrees/s²]
		int Y_DEGREES_PER_PIXEL = 44; // Specify how big the movement between two pixel shall be
		int X_DEGREES_PER_PIXEL = 50;
		int PIXEL = YMAX / Y_DEGREES_PER_PIXEL; // Pixel count with the given constraints
		// create array to hold the scanned image
		int[][] Image = new int[43][43];
		boolean dir = false; // variable to determine the direction of the y axis

		// instantiate Sensors and Motors
		Sensor = new EV3ColorSensor(SensorPort.S4);
		XMotor.resetTachoCount(); // reset the internal degree reference of the motors
		YMotor.resetTachoCount();
		XMotor.setSpeed(SPEED);
		YMotor.setSpeed(SPEED);
		XMotor.setAcceleration(ACCELERATION);
		YMotor.setAcceleration(ACCELERATION);
		YMotor.setStallThreshold(3, 5); // TODO: Find good values

		// get an instance of this sensor in measurement mode
		SampleProvider RgbSample = Sensor.getRGBMode();
		SampleProvider RgbFilter = new MedianFilter(RgbSample, 5);
		// Initialize an array of floats for fetching samples
		float[] Rgb = new float[RgbFilter.sampleSize()];

		// For loop for the x-axis
		for (int j = 0; j < PIXEL; j++) {

			// For loop for the y-axis
			for (int i = 0; i < PIXEL; i++) {
				int y_index; // helping variable for image array filling
				if (dir) {
					YMotor.rotate(Y_DEGREES_PER_PIXEL);
					if (YMotor.isStalled()) {
						//ToDo Error Handling
					}
					y_index = PIXEL - i - 1;
				} else {
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
				Image[j][y_index] = getPixel(Rgb);
			}

			XMotor.rotate(X_DEGREES_PER_PIXEL);
			// invert direction for the Y-Axis
			dir = !dir;
		}
	}

	/*
	 * get pixel color
	 * return 0 for white
	 * return 1 for black
	 */
	private static int getPixel(float[] rgb) {
		float sum = (rgb[0] + rgb[1] + rgb[2]) / 3;
		if (sum < 0.12) {
			return 0;
		}
		return 1;
	}
}
