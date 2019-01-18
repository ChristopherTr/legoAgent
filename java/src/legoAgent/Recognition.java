package legoAgent;

public class Recognition implements IRecognition {

	@Override
	public Figure recognize() {
		Scanner scanner = new Scanner();
		int[][] image = scanner.readImage();
		int[] traits = scanner.computeTrait(image);
		if (traits.length > 2) {
			throw new IllegalArgumentException("Return from traits-detection returned more than two values!");
		}
		SVM svm = new SVM();
		Figure figure = svm.classify(traits[0], traits[1]);
		return figure;
	}
}
