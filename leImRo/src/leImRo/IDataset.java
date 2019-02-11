package leImRo;

import java.util.ArrayList;

public interface IDataset {

	void clearAll();
	void addNewData(IDataPoint data);
	ArrayList<IDataPoint> getAllData();
	IDataPoint[] getSVMPoints();
	void removeStoredSVMPoints();
	
}
