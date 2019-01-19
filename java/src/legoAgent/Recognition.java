package legoAgent;

public class Recognition implements IRecognition {

	@Override
	public Figure recognize() {
		//get new DataPoint from Scanner
		Scanner scanner = new Scanner();
		int[][] image = scanner.readImage();
		IDataPoint newDataPoint = scanner.computeTrait(image);
		
		//add new DataPoint to SVM
		SVM svm = new SVM();
		Figure figure = svm.classify(newDataPoint);
		return figure;
	}
}
