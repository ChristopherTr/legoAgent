package leImRo;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;

public class Scanner implements IScanner {

	public final static int resolution = 8;	//specify the resolution 1 = maximum resolution (pixel = yMax/(resolution*minYAngle) + 1)
	// constants to define the mechanical constraining
	public final static int yMax = 946; 			// maximum Degree for the Y-Axis, X is unlimited
	public final static int speed = 500; 			// Speed for the motors [degrees/s]
	public final static int acceleration = 300; 	// maximum acceleration for the Motors [degrees/s²]
	public final static int minYAngle = 22;			//TODO: validate this
	public final static int minXAngle = 25;			//TODO: validate this
	public final static int startDir = -1;			//due mechanical construction we should start in this direction
	public final static int sensorSamples = 5;		//specify samples for median filter
	public final static double rgbThreshold = 0.12;	//Threshold for the average of the rgb sensor result to seperate between black and white
	
	// member variables
	private int pixel;	// variable holding the current pixel size
	private int dir; 	// variable to determine the direction of the y axis
	private RegulatedMotor XMotor;
	private RegulatedMotor YMotor;
	private EV3ColorSensor Sensor;
	
	private SampleProvider RgbSample;
	private SampleProvider RgbFilter;

	public Scanner() {
		pixel = 0;
		dir = startDir; 
		// instantiate Sensors and Motors
		XMotor = Motor.A;
		YMotor = Motor.B;
		Sensor = new EV3ColorSensor(SensorPort.S4);
		XMotor.resetTachoCount(); // reset the internal degree reference of the motors
		YMotor.resetTachoCount();
		XMotor.setSpeed(speed);
		YMotor.setSpeed(speed);
		XMotor.setAcceleration(acceleration);
		YMotor.setAcceleration(acceleration);
		YMotor.setStallThreshold(3, 5); // TODO: Find good values
		XMotor.setStallThreshold(3, 5); // TODO: Find good values

		// get an instance of this sensor in measurement mode
		RgbSample = Sensor.getRGBMode();
		RgbFilter = new MedianFilter(RgbSample, sensorSamples);
	}

	@Override
	/**
	 * scan whole image and calculate the characteristics of the objects
	 */
	public IDataPoint scanNewDataPoint() {
		return this.calculateCharacteristics(this.readImage());
	}

	/**
	 * scan the whole picture. This is done in a meander shape.
	 * See the following illustration (s is a scan, -> symbolize movement):
	 * s->s->s->s
	 * 		   \/
	 * s<-s<-s<-s
	 * \/
	 * s->s->s->s
	 * 		   \/
	 * s<-s<-s<-s
	 * 
	 * This shows that for the reading of a line are one scan more is necessary than movements.
	 */
	private int[][] readImage() {
		// calculate pixel size with the given constraints
		int yAnglePerPixel = minYAngle * resolution;
		int xAnglePerPixel = minXAngle * resolution;
		int yMovements = yMax / yAnglePerPixel;
		int xMovements = yMovements - 1;
		pixel = yMovements + 1;
		Logger.log("Scanner: Read " + pixel + " Pixels");

		// create array to hold the scanned image
		int[][] Image = new int[pixel][pixel];
		
		//do dummy read out to initalize sensor
		getPixelColor();
		
		// For loop for the x-axis
		for (int xIndex = 0; xIndex < pixel; xIndex++) {
			String debugImLine = "";	//debug variable for showing image
			
			// For loop for the y-axis
			for (int yIndex = 0; yIndex < pixel; yIndex++) {				
				int pixelColor = 0;
				// fetch a sample
				pixelColor = getPixelColor();
				Image[xIndex][getYIndex(yIndex)] = pixelColor;
				
				//----------Y-Movement--------------
				if(yIndex < yMovements)
				{
					turnYMotor(yAnglePerPixel);
				}
				
				//------debug----------
				if(pixelColor == 1){
					debugImLine += '#';
				}
				else {
					debugImLine += '_';
				}
				//--------------------
			}
			//------X-Movement--------
			if(xIndex < xMovements)
			{
				turnXMotor(xAnglePerPixel);
			}
			
			// invert direction for the Y-Axis
			invertDirection();
			
			//debug messages
			Logger.log("Scanner: Progress line " + (xIndex+1) + " from " + pixel);
			Logger.log(debugImLine);
		}
		
		//return to HomePosition
		returnToHome(xAnglePerPixel, yAnglePerPixel);
		
		return Image;
	}

	/**
	 * read in sensor value and filter it
	 * @return 0 for white 
	 * 		   1 for black
	 */
	private int getPixelColor() {
		// Initialize an array of floats for the filter
		float[] Rgb = new float[RgbFilter.sampleSize()];
		
		// measure filterSize Times to calculate median value
		for (int sample = 0; sample < sensorSamples; sample++) {
			RgbFilter.fetchSample(Rgb, 0);
		}
		Logger.log("Scanner: Read Pixel r " + Rgb[0] + ", g " + Rgb[1] + ", b " + Rgb[2]);
		//calculate averageColor
		float avg = (Rgb[0] + Rgb[1] + Rgb[2]) / 3;
		if (avg < rgbThreshold) {
			return 1;
		}
		return 0;
	}

	/**
	 * calculates the y Index for the image buffer
	 * Depending on the direction the index must be calculated different because of the meander shape of scanning
	 * @param i: current position y Motor
	 * @return index for the image buffer
	 */
	private int getYIndex(int i)
	{
		if (dir < 0) {
			return (pixel - i - 1);
		} else {
			return i;
		}
	}
	
	/**
	 * turn the XMotor about given angle
	 * @param xAnglePerPixel: angle to rotate
	 */
	private void turnXMotor(int xAnglePerPixel) {
		XMotor.rotate(xAnglePerPixel);
		Delay.msDelay(1);	//Delay is important for mechanical constraining and to ensure correct process of the sw
		if (XMotor.isStalled()) {
			Logger.log("Scanner: XMotor stalled");
		}
	}

	/**
	 * turn the YMotor about given angle
	 * @param yAnglePerPixel: angle to rotate
	 */
	private void turnYMotor(int yAnglePerPixel) {
		YMotor.rotate(dir * yAnglePerPixel);
		Delay.msDelay(1);	//Delay is important for mechanical constraining and to ensure correct process of the sw
		if (YMotor.isStalled()) {
			Logger.log("Scanner: YMotor stalled");
		}
	}
	
	/**
	 * return to the origin position after scanning image
	 */
	private void returnToHome(int xAnglePerPixel, int yAnglePerPixel) {
		//move back to origin position
		if (dir > 0) {	//check if Y Movement is necessary or not
			YMotor.rotate(pixel*yAnglePerPixel);
		}
		XMotor.rotate(-1*pixel*xAnglePerPixel);
		dir = startDir;
	}

	/**
	 * invert the direction flag
	 *  1 means rotate clockwise
	 * -1 means rotate counter-clockwise
	 */
	private void invertDirection() {
		if(dir > 0)
		{
			dir = -1;
		}
		else
		{
			dir = 1;
		}
	}
	
	/**
	 * calculate the characteristics perimeter and area of a given image
	 * @param image: int buffer of the image
	 * @return DataPoint with the characteristics perimeter and area
	 */
	private IDataPoint calculateCharacteristics(int[][] image) {
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
		Logger.log("Scanner: Perimeter = " + perimeter + ", Area = " + area);
		DataPoint point = new DataPoint(perimeter, area);
		return point;
	}
}
