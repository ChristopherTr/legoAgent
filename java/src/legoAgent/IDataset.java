package legoAgent;

import java.util.ArrayList;

public interface IDataset {

	void store();
	void load();
	void clearAll();
	void addNewData(IDatum data);
	ArrayList<IDatum> getAllData();
	IDatum[] getSVMPoints();
	void removeStoredSVMPoints();
	
}
