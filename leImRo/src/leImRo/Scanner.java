package leImRo;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;
import lejos.hardware.lcd.LCD;

/**
 * 
 * Klasse feur die Scan-Funktion des Roboters
 * Alle Interaktion mit der Hardware (außer Display) findet in dieser Klasse statt
 * Motoren werden angesprochen und Sensoren ausgelesen
 * Von Außen wird die Methode scanNewDataPoint angesprochen, welche einen Datenpunkt mit den benoetigten Merkmalen, aber unbestimmten Figur-Typ zurueckliefert
 *
 */
public class Scanner implements IScanner {

	public final static int resolution = 4;	//specify the resolution 1 = maximum resolution (pixel = yMax/(resolution*minYAngle) + 1)
	// constants to define the mechanical constraining
	public final static int yMax = 946; 			// maximum Degree for the Y-Axis, X is unlimited
	public final static int xSpeed = 200; 			// Speed for the motors [degrees/s]
	public final static int xAcceleration = 200; 	// maximum acceleration for the Motors [degrees/s�]
	public final static int ySpeed = 500; 			// Speed for the motors [degrees/s]
	public final static int yAcceleration = 500; 	// maximum acceleration for the Motors [degrees/s�]
	public final static int minYAngle = 22;			//TODO: validate this
	public final static int minXAngle = 5;			//TODO: validate this
	public final static int startYDir = -1;			//due mechanical construction we should start in this direction
	public final static int startXDir = -1;			//due mechanical construction we should start in this direction
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
		dir = startYDir; 
		// instantiate Sensors and Motors
		XMotor = Motor.A;
		YMotor = Motor.B;
		Sensor = new EV3ColorSensor(SensorPort.S4);
		XMotor.resetTachoCount(); // reset the internal degree reference of the motors
		YMotor.resetTachoCount();
		XMotor.setSpeed(xSpeed);
		YMotor.setSpeed(ySpeed);
		XMotor.setAcceleration(xAcceleration);
		YMotor.setAcceleration(xAcceleration);
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
		int xMovements = yMovements;
		pixel = yMovements + 1;
		Logger.log("Scanner: Read " + pixel + " Pixels");

		// create array to hold the scanned image
		int[][] Image = new int[pixel][pixel];
		//initialize with -1
		for(int x = 0 ; x < pixel; x++) {
			for(int y = 0; y < pixel; y++) {
				Image[x][y] = -1;
			}
		}
		
		
		//do dummy read out to initalize sensor
		getPixelColor();
		
		// For loop for the x-axis
		for (int xIndex = 0; xIndex < pixel; xIndex++) {
			
			// For loop for the y-axis
			for (int yIndex = 0; yIndex < pixel; yIndex++) {				
				int pixelColor = 0;
				// fetch a sample
				pixelColor = getPixelColor();
				Image[xIndex][getYIndex(yIndex)] = pixelColor;
				printImageOnLCD(Image);
				
				//----------Y-Movement--------------
				if(yIndex < yMovements)
				{
					turnYMotor(yAnglePerPixel);
					Logger.log("Scanner: Move Y" + yIndex + " von " + yMovements);
				}
				
			}
			//------X-Movement--------
			if(xIndex < xMovements)
			{
				turnXMotor(xAnglePerPixel);
				Logger.log("Scanner: Move X" + xIndex + " von " + xMovements);
			}
			
			// invert direction for the Y-Axis
			invertDirection();
			
			//debug messages
			Logger.log("Scanner: Progress line " + (xIndex+1) + " from " + pixel);
			Logger.log(this.debugImage(Image));
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
		XMotor.rotate(startXDir * xAnglePerPixel);
		Delay.msDelay(100);	//Delay is important for mechanical constraining and to ensure correct process of the sw
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
		Delay.msDelay(100);	//Delay is important for mechanical constraining and to ensure correct process of the sw
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
			YMotor.rotate((pixel-1)*yAnglePerPixel);
		}
		XMotor.rotate(-1*startXDir*(pixel-1)*xAnglePerPixel);
		dir = startYDir;
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
	 * print Image as String as Debug String
	 */
	private String debugImage(int[][] Image)
	{
		String debugImLine = "\n";
		for(int y= 0; y < pixel; y++)
		{
			for(int x=0; x < pixel; x++ )
			{
				if(Image[y][x] == 1){
					debugImLine += '#';
				}
				else {
					debugImLine += '_';
				}
			}
			debugImLine += "\n";
		}
		return debugImLine;
	}
	
	/*
	 * prints Image on LCD display
	 * white: " "; black: "#"; unknown: "O"
	 */
	private void printImageOnLCD(int[][] image) {
		LCD.clear();
		for(int i = 0; i < pixel; i++) {
			String newLine = "";
			for(int j = 0; j < pixel; j++) {
				if(image[i][j] == 0) {
					newLine += " ";
				}
				else if(image[i][j] == 1) {
					newLine += "#";
				}
				else {
					newLine += "0";
				}
				
			}
			LCD.drawString(newLine, 0, i);
		}
	}
	
	/**
	 * calculate the characteristics perimeter (sqared) and area of a given image
	 * @param image: int buffer of the image
	 * @return DataPoint with the characteristics perimeter and area
	 */
	private IDataPoint calculateCharacteristics(int[][] image) {
		int area = 0;
		int perimeter = 0;

		// iterate over all points
		for (int i = 0; i < pixel; i++) {
			for (int j = 0; j < pixel; j++) {
				// sum for area
				if (image[i][j] == 1) {
					area = area + 1;
				}

				// sum for perimeter
				if ((i > 0) && (j > 0) && (i < pixel - 1) && (j < pixel - 1)) {
					// case 1 in the center of the image, not the borders
					if (image[i][j] == 1) {
						if ((image[i][j - 1] == 0) || (image[i][j + 1]) == 0 || (image[i + 1][j] == 0) || (image[i - 1][j] == 0)) {
							perimeter = perimeter + 1;
						}
					}
				} else {
					// case 2: border of the image
					if (image[i][j] == 1) {
						perimeter = perimeter + 1;
					}
				}
			}
		}
		// for cleaner distribution: square of perimeter
		perimeter = (int) Math.pow(perimeter, 2);
		Logger.log("Scanner: Perimeter = " + perimeter + ", Area = " + area);
		DataPoint point = new DataPoint(perimeter, area);
		return point;
	}
}
