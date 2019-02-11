package leImRo;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;

public class Scanner implements IScanner {

	// public variables
	public final static int resolution = 1;
	public final static int yMax = 946; // maximum Degree for the Y-Axis, X is unlimited
	public final static int speed = 500; // Speed for the motors [degrees/s]
	public final static int acceleration = 300; // maximum acceleration for the Motors [degrees/s²]
	public final static int minYAngle = 22;
	public final static int minXAngle = 25;
	public final static int filterSize = 5;
	public final static double rgbThreshold = 0.12;

	// private variables
	private int pixel = 0;
	private static RegulatedMotor XMotor = Motor.A;
	private static RegulatedMotor YMotor = Motor.B;
	private static EV3ColorSensor Sensor;

	@Override
	public IDataPoint scanNewDataPoint() {
		return this.computeTrait(this.readImage());
	}

	private int[][] readImage() {
		// calculate pixel size with the given constraints
		int yAnglePerPixel = minYAngle * resolution;
		int xAnglePerPixel = minXAngle * resolution;
		pixel = yMax / yAnglePerPixel;
		Logger.log("Scanner: Read " + pixel + " Pixels");

		// create array to hold the scanned image
		int[][] Image = new int[pixel][pixel];
		boolean dir = false; // variable to determine the direction of the y axis

		// instantiate Sensors and Motors
		Sensor = new EV3ColorSensor(SensorPort.S4);
		XMotor.resetTachoCount(); // reset the internal degree reference of the motors
		YMotor.resetTachoCount();
		XMotor.setSpeed(speed);
		YMotor.setSpeed(speed);
		XMotor.setAcceleration(acceleration);
		YMotor.setAcceleration(acceleration);
		YMotor.setStallThreshold(3, 5); // TODO: Find good values

		// get an instance of this sensor in measurement mode
		SampleProvider RgbSample = Sensor.getRGBMode();
		SampleProvider RgbFilter = new MedianFilter(RgbSample, filterSize);
		// Initialize an array of floats for fetching samples
		float[] Rgb = new float[RgbFilter.sampleSize()];

		// For loop for the x-axis
		for (int j = 0; j < pixel; j++) {

			// For loop for the y-axis
			for (int i = 0; i < pixel; i++) {
				int y_index; // helping variable for image array filling
				if (dir) {
					YMotor.rotate(yAnglePerPixel);
					if (YMotor.isStalled()) {
						Logger.log("YMotor stalled");
					}
					y_index = pixel - i - 1;
				} else {
					YMotor.rotate((-1) * yAnglePerPixel);
					y_index = i;
				}
				Delay.msDelay(1);
				// measure filterSize Times to calculate median value
				for (int sample = 0; sample < filterSize; sample++) {
					RgbFilter.fetchSample(Rgb, 0);
				}
				// fetch a sample
				Image[j][y_index] = getPixel(Rgb);
			}

			XMotor.rotate(xAnglePerPixel);
			if (XMotor.isStalled()) {
				Logger.log("XMotor stalled");
			}
			// invert direction for the Y-Axis
			dir = !dir;
		}
		return Image;
	}

	/*
	 * get pixel color return 0 for white return 1 for black
	 */
	private int getPixel(float[] rgb) {
		float sum = (rgb[0] + rgb[1] + rgb[2]) / 3;
		if (sum < rgbThreshold) {
			return 0;
		}
		return 1;
	}

	private IDataPoint computeTrait(int[][] image) {
		int area = 0;
		int perimeter = 0;

		for (int i = 0; i < pixel; i++) {
			for (int j = 0; j < pixel; j++) {
				if (image[i][j] == 1) {
					area = area + 1;
				}

				if ((i > 0) && (j > 0) && (i < pixel - 1) && (j < pixel - 1)) {

					if (image[i][j] == 1) {

						if ((image[i][j - 1] == 0) || (image[i][j + 1]) == 0 || (image[i + 1][j] == 0)
								|| (image[i - 1][j] == 0)) {
							perimeter = perimeter + 1;
						}
					}

				} else {
					if (image[i][j] == 1) {
						perimeter = perimeter + 1;
					}
				}
			}
		}
		Logger.log("computeTrait: Umfang = " + perimeter + ", Flaeche = " + area);
		return new DataPoint(perimeter, area);
	}
}
