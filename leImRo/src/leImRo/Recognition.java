package leImRo;

public class Recognition implements IRecognition {

	@Override
	public Figure recognize() {
		//get new DataPoint from Scanner
		Scanner scanner = new Scanner();
		IDataPoint newDataPoint = scanner.scanNewDataPoint();
		
		//add new DataPoint to SVM
		Dataset dataset = new Dataset();
		SVM svm = new SVM(dataset);
		Figure figure = svm.classify(newDataPoint);
		return figure;
	}
	
	@Override
	public void removeAll() {
		Dataset dataset = new Dataset();
		dataset.clearAll();
		dataset.store();
	}

	@Override
	public void addNewData(Figure figure) {
		Scanner scanner = new Scanner();
		IDataPoint newDataPoint = scanner.scanNewDataPoint();
		newDataPoint.setFigure(figure);
		
		Dataset dataset = new Dataset();
		dataset.addNewData(newDataPoint);
		
		SVM svm = new SVM(dataset);
		//calculate new SupportVectors
		try {
			svm.findSupportVectors();
		} catch (IllegalArgumentException e) {
			//ignore in case of to little points stored in dataset
		}

		dataset.store();
	}
}
