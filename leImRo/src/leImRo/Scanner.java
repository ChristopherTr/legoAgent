package leImRo;

public class Scanner implements IScanner {

	public final static int resolution = 512;
	
	@Override
	public int[][] readImage() {
		return null;
	}

	@Override
	public IDataPoint computeTrait(int[][] image) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataPoint scanNewDataPoint() {
		return this.computeTrait(this.readImage());
	}
}
