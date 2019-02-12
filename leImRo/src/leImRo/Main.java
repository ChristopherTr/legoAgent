/**
 * Demo Project to test the hardware of the roboter
 */
package leImRo;

/**
 * @author trdrc
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean debugDataset= false;

		Recognition recognition = new Recognition();
		
		if ( debugDataset ) {
			Dataset dataset = Dataset.load();
			dataset.clearAll();
			//Circles
			int testR[] = {8,12,22,11,3,33,15,57,23,22};
			for(int count=0; count <10; count++) {
				dataset.addNewData(new DataPoint((int)(Math.pow((2*Math.PI*testR[count]),2)), (int)(Math.PI*Math.pow(testR[count], 2)),Figure.circle));
				dataset.addNewData(new DataPoint((int)(Math.pow(4*testR[count],2)),(int) (Math.pow(testR[count], 2)),Figure.rectangle));
			}	
			NearestNeighbour nb = new NearestNeighbour(dataset);
			nb.SetKNeighbours(5);
			nb.classify(new DataPoint((int)(2*Math.PI*14), (int)(Math.PI*Math.pow(14, 2))));
		}
		
		while(true) {
			GUI.processMenu(recognition);
		}
				
	}
}
