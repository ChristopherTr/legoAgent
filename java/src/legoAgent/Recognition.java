package legoAgent;

public class Recognition implements IRecognition {

	@Override
	public Figure recognize() {
		//get new DataPoint from Scanner
		Scanner scanner = new Scanner();
		int[][] image = scanner.readImage();
		IDataPoint newDataPoint = scanner.computeTrait(image);
		
		//add new DataPoint to SVM
		Dataset dataset = new Dataset();
		SVM svm = new SVM(dataset);
		Figure figure = svm.classify(newDataPoint);
		return figure;
	}
}
