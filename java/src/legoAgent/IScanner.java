package legoAgent;

public interface IScanner {
	
	public int[][] readImage();
	public IDataPoint computeTrait(int image[][]);
	public IDataPoint scanNewDataPoint();
	int resolution = 16;
	
}
