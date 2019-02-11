package leImRo;

public interface ISVM {

	public void findSupportVectors();
	void computeSeparator();
	public Figure classify(IDataPoint dataPoint);
}
