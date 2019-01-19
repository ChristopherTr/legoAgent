package legoAgent;

public interface ISVM {

	public IDataPoint[] findSupportVectors();
	void computeSeparator();
	public Figure classify(int perimeter, int area);
}
