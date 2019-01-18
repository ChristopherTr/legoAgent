package legoAgent;

public interface IScanner {
	
	public int[][] readImage();
	public int[] computeTrait(int image[][]);
	int resolution = 16;
	
}
