package leImRo;

import java.io.*;
import java.util.ArrayList;


public class Dataset implements IDataset {

	/**
	 * the path and filename in the filesystem of the serialized data.
	 */
	private static final String fileLocation = "datapoints.csv";
	public static final String csvSplit = ",";
	private ArrayList<IDataPoint> dataset;
	private IDataPoint[] sVMPoints;
	/**
	 * orientation, whether negative values means circles or rectangles
	 * 0: circles get negative values, 
	 * 1: rectangles get negative values
	 */
	private int svmOrientation = 0;
	
	public Dataset() {
		this.dataset = new ArrayList<IDataPoint>();
		this.sVMPoints = new DataPoint[3];
	}
	
	/**
	 * TODO: Sort the received array by figure Type ()
	 * @param sVMPoints
	 */
	public void setsVMPoints(IDataPoint[] sVMPoints) {
		this.sVMPoints = sVMPoints;
	}

	public int getSvmOrientation() {
		return svmOrientation;
	}

	public void setSvmOrientation(int svmOrientation) {
		this.svmOrientation = svmOrientation;
	}
	
	/**
	 * stores the current status of the dataset to the disk. 
	 * Storage-path is specified by static variable fileLocation. 
	 */
	public static void store(Dataset dataset) {
		String outstring = new String();
		try {
			FileWriter fw = new FileWriter(Dataset.fileLocation);
			BufferedWriter bw = new BufferedWriter(fw);
			outstring = "Perimeter_squared" + Dataset.csvSplit + "Area" + Dataset.csvSplit + "Form" + System.lineSeparator();
			Logger.log(outstring);
			bw.write(outstring);
			for (IDataPoint point : dataset.getAllData()) {
				outstring = "" + point.getPerimeter() + Dataset.csvSplit + point.getArea() + Dataset.csvSplit + point.getFigure() + System.lineSeparator();
//				switch (point.getFigure()) { // Maybe replace the string components beneath with constant representations ...
//					case circle:
//						outstring += Figure.circle;
//						break;
//					case rectangle:
//						outstring += Figure.rectangle;
//					default:
//						break;
//					}
				Logger.log(outstring);
				bw.write(outstring);
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			Logger.log(e.getMessage());
		} finally {
			Logger.log("Objektspeicherung beendet");
		}
	}

	/**
	 * loads the data that are currently stored in the filesystem. 
	 * Path is specified by static variable fileLocation.
	 * TODO: Load of SVMPoints from separate file 
	 */
	public static Dataset load() {
		Dataset dataset = new Dataset();
		Logger.log("Lade Daten von " + Dataset.fileLocation);
		try {
			FileReader fr = new FileReader(Dataset.fileLocation);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			Logger.log(line);
			while ((line = br.readLine()) != null) {
				Logger.log(line);
				String[] tmp = line.split(Dataset.csvSplit);
				if (tmp.length != 3) {
					Logger.log("broken Dataset: " + line);
					continue;
				}
				int perimeter = Integer.parseInt(tmp[0]);
				int area = Integer.parseInt(tmp[1]);
				if(0 == Figure.circle.toString().compareTo(tmp[2])) {
					dataset.addNewData(new DataPoint(perimeter, area, Figure.circle));
				} else if (0 == Figure.rectangle.toString().compareTo(tmp[2])) {
					dataset.addNewData(new DataPoint(perimeter, area, Figure.rectangle));
				} else {
					Logger.log("ERROR while loading: " + line);
					Logger.log("failed to compare '" + tmp[2] + "' with '" + Figure.circle + "' or '" + Figure.rectangle + "'");
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			Logger.log("Bisher ist die Datei noch nicht angelegt, starte mit leerem Datensatz ...");
			dataset = new Dataset();
		} catch (IOException e) {
			Logger.log(e.getMessage());
			dataset = new Dataset();
		} finally {
			Logger.log("Objekte geladen");
		}
		return dataset;
	}
	
	/**
	 * removes the currently existing dataset, does not store this changes to disk. 
	 */
	@Override
	public void clearAll() {
		this.dataset = new ArrayList<IDataPoint>();
		this.removeStoredSVMPoints();
		this.svmOrientation = 0;
	}

	/**
	 * Adds a new data to the dataset in RAM, does not store this changes to disk. 
	 */
	@Override
	public void addNewData(IDataPoint data) {
		Logger.log("Saving " + data + " to dataset.");
		this.dataset.add(data);
	}

	/**
	 * returns the current dataset from RAM. 
	 */
	@Override
	public ArrayList<IDataPoint> getAllData() {
		return this.dataset;
	}

	/**
	 * returns the current SVM-Points to create the border, 
	 * or null, if no data is stored
	 */
	@Override
	public IDataPoint[] getSVMPoints() {
		return this.sVMPoints;
	}
	
	/**
	 * returns the String-Repräsentation of this object
	 */
	public String toString() {
		String ret = "Dataset with " + this.dataset.size() + " elements: ";
		for (IDataPoint dataPoint : this.dataset) {
			ret += System.lineSeparator() + dataPoint;
		}
		return ret;
	}

	/**
	 * removes the currently stored SVM-Points. 
	 * Used e.g. in case of addition of a new datapoint to the dataset. 
	 */
	@Override
	public void removeStoredSVMPoints() {
		this.sVMPoints = null;
	}
}
