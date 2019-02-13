package leImRo;

public class Recognition implements IRecognition {

	private IScanner scanner;
	private SVM svm;
	private NearestNeighbour knn;
	private Dataset dataset;
	
	public Recognition(){
		this.scanner = new Scanner();
		this.dataset = Dataset.load();
		this.svm = new SVM(this.dataset);
		this.knn = new NearestNeighbour(this.dataset, 10);
	}

	/**
	 * Erkennung einer Figur - Hauptfunktion
	 * Beinhaltet
	 *  - Scannen des Bildes
	 *  - Vorbereiten der SVM, wenn erforderlich
	 *  - Bestimmen des Bildes mit der SVM
	 * @return Figure Gibt die berechnete Figur zurueck
	 */
	@Override
	public Figure recognizeSVM() {
		//get new DataPoint from Scanner
		IDataPoint newDataPoint = scanner.scanNewDataPoint();
		
		//let the SVM compute the type of image
		Figure figure = svm.classify(newDataPoint);
		Logger.log("Detected figure (corious if this works...): " + figure);
		return figure;
	}

	/**
	 * Erkennung einer Figur - Hauptfunktion
	 * Beinhaltet
	 *  - Scannen des Bildes
	 *  - Vorbereiten der SVM, wenn erforderlich
	 *  - Bestimmen des Bildes mit dem K Nearest Neighbour- Algorithmus
	 * @return: Figure Gibt die berechnete Figur zurueck
	 */
	@Override
	public Figure recognizeKNN() {
		//get new DataPoint from Scanner
		IDataPoint newDataPoint = scanner.scanNewDataPoint();
		
		//let the SVM compute the 
		Figure figure = knn.classify(newDataPoint);
		Logger.log("Detected figure (corious if this works...): " + figure);
		return figure;
	}
	
	
	/**
	 * Loescht alle bisherigen Daten inklusive Trainingsdaten, gescannte Bilder, etc. 
	 */
	@Override
	public void removeAll() {
		Logger.log("Full Reset started");
		this.dataset.clearAll();
		Dataset.store(this.dataset);
		Logger.log("Full Reset completed");
	}

	/**
	 * Funktion lernt einen neuen Datenpunkt als reine Wahrheit
	 */
	@Override
	public void train(Figure figure) {
		IDataPoint newDataPoint = this.scanner.scanNewDataPoint();
		newDataPoint.setFigure(figure);
		Logger.log("TRAIN:" + newDataPoint);
		Logger.log("Dataset:" + this.dataset);
		this.dataset.addNewData(newDataPoint);
		
		this.svm = new SVM(dataset);
		//calculate new SupportVectors
		try {
			svm.findSupportVectors();
		} catch (IllegalArgumentException e) {
			Logger.log("Falsche Verwendung von findSupportVectors: Falsche Anzahl an verfügbaren Datenpunkten");
		}
		Dataset.store(dataset);
	}
}
