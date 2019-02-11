package leImRo;

public class Recognition implements IRecognition {

	private IScanner scanner;
	private SVM svm;
	private Dataset dataset;
	
	public Recognition(){
		this.scanner = new Scanner();
		this.dataset = new Dataset();
		this.svm = new SVM(this.dataset);
	}

	/**
	 * Erkennung einer Figur - Hauptfunktion
	 */
	@Override
	public Figure recognize() {
		//get new DataPoint from Scanner
		IDataPoint newDataPoint = scanner.scanNewDataPoint();
		
		//let the SVM compute the 
		Figure figure = svm.classify(newDataPoint);
		return figure;
	}
	
	@Override
	public void removeAll() {
		Dataset dataset = new Dataset();
		dataset.clearAll();
		Dataset.store(dataset);
	}

	/**
	 * Funktion lernt einen neuen Datenpunkt als reine Wahrheit
	 */
	@Override
	public void train(Figure figure) {
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
