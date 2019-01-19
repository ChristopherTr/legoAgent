package legoAgent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class Dataset implements IDataset {

	/**
	 * the path and filename in the filesystem of the serialized data.
	 */
	static private String fileLocation = "location.data";
	private ArrayList<IDataPoint> dataset;
	private IDataPoint[] sVMPoints;
	
	public Dataset() {
		this.load();
	}
	
	/**
	 * stores the current status of the dataset to the disk. 
	 * Storage-path is specified by static variable fileLocation. 
	 * TODO: storage of SVMPoints in separate file
	 */
	@Override
	public void store() {
		try {
			FileOutputStream fos = new FileOutputStream(fileLocation);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(dataset);
			oos.close();
			fos.close();
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
	@SuppressWarnings("unchecked")
	@Override
	public void load() {
		try {
			FileInputStream fis = new FileInputStream(fileLocation);
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    this.dataset = (ArrayList<IDataPoint>) ois.readObject();
		    ois.close();
		    fis.close();
		} catch (IOException | ClassNotFoundException e) {
			Logger.log(e.getMessage());
			this.dataset = new ArrayList<IDataPoint>();
		} finally {
			Logger.log("Objekte geladen");
		}
	}
	
	/**
	 * removes the currently existing dataset, does not store this changes to disk. 
	 */
	@Override
	public void clearAll() {
		this.dataset = new ArrayList<IDataPoint>();
		this.removeStoredSVMPoints();
	}

	/**
	 * Adds a new data to the dataset in RAM, does not store this changes to disk. 
	 */
	@Override
	public void addNewData(IDataPoint data) {
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
		// TODO Auto-generated method stub
		return this.sVMPoints;
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
