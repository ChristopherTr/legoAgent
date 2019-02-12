package leImRo;

public interface INearestNeighbour {
	public Figure classify(IDataPoint dataPoint);
	public void SetKNeighbours(int k);
}
