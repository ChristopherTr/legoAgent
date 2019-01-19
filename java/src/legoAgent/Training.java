package legoAgent;

public class Training implements ITraining {
	private Scanner scanner;
	
	public Training(Scanner scanner){
		this.scanner=scanner;
	}
	
	@Override
	public void removeAll() {
		Dataset dataset = new Dataset();
		dataset.clearAll();
		dataset.store();
	}

	@Override
	public void addNewData(Figure figure) {
		IDataPoint newDataPoint = this.scanner.scanNewDataPoint();
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
