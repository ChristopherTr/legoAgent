package legoAgent;

public interface ISVM {

	public IDataPoint[] findSupportVectors();
	void computeSeparator();
	public Figure classify(IDataPoint dataPoint);
}
