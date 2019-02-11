package legoAgent;

import java.util.ArrayList;

public interface IDataset {

	void store();
	void load();
	void clearAll();
	void addNewData(IDataPoint data);
	ArrayList<IDataPoint> getAllData();
	IDataPoint[] getSVMPoints();
	void removeStoredSVMPoints();
	
}
