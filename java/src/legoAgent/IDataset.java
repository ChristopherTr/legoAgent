package legoAgent;

public interface IDataset {

	void store();
	void load();
	void addNewData(IDatum data);
	IDatum[] getAllData();
	IDatum[] getSVMPoints();
	void removeStoredSVMPoints();
	
}
