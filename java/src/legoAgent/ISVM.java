package legoAgent;

public interface ISVM {

	public IDatum[] findSupportVectors();
	void computeSeparator();
	public Figure classify(int perimeter, int area);
}
