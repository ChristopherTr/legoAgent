package leImRo;
import java.util.ArrayList;

/**
 * 
 * Klasse zur Erkennung des Figurtyps mithilfe einer Support-Vector-Machine
 *
 */
public class SVM implements ISVM {

	private static int classifierRectangle = 1;
	private static int classifierCircle = -1;
	private Dataset dataSet;
	/**
	 * Es sei im 2D-Raum die Gerade des Separators (Hyperebene) als 
	 * g(x, y) = this.PointOnSeparator + n * this.vectorParallelToSeparator (die Straße entlang)
	 * mit dem orthogonalen Vektor this.vectorToSeparatorSide (von der Straßenmitte zum Rand der Straße in einem Schritt)
	 */
	private Vector PointOnSeparator, vectorParallelToSeparator, vectorToSeparatorSide;
	
	
	/**
	 * Constructor for Support Vector Machine
	 * @param dataset: the dataset all operations are referring to
	 */
	public SVM(Dataset dataset) {
		this.dataSet = dataset;
	}
	
	/*
	 * compute best hyperplane with the given points
	 * currently only works with 3 datapoints to compute, else error
	 * TODO: Support more Points
	 */
	@Override
	public void findSupportVectors() {
		// New point --> delete current state
		this.PointOnSeparator = null;
		this.vectorParallelToSeparator = null;
		this.vectorToSeparatorSide = null;
		// Fill each figure list
		ArrayList<IDataPoint> listDataSet = this.dataSet.getAllData();
		ArrayList<IDataPoint> listCircle = new ArrayList<IDataPoint>();
		ArrayList<IDataPoint> listRectangle = new ArrayList<IDataPoint>();
		for(IDataPoint laufDatum: listDataSet) {
			switch(laufDatum.getFigure()) {
			case rectangle:
				listRectangle.add(laufDatum);
				break;
			case circle:
				listCircle.add(laufDatum);
				break;
			default:
				break;
			}
		}
		// Size of trainigsdata
		int size = listDataSet.size();
		if (size < 3) {
			throw new IllegalArgumentException("Too few trainingsdata to compute support-vectors: " + size + ", requered: 3+");
		} else if(size == 3) { // Easy case: onyl three points are avail. 
			Logger.log("Compute Support Vectors with 3 Datapoints:");
			Logger.log(listDataSet.get(0).toString());
			Logger.log(listDataSet.get(1).toString());
			Logger.log(listDataSet.get(2).toString());
			if(listCircle.size() == 0 || listRectangle.size() == 0) {
				throw new IllegalArgumentException("Too broken trainingsdata to compute support-vectors, requered two different Point-types");
			}
			if(listCircle.size() == 2) { // 2 circles and one rectangle are avail. 
				this.dataSet.setSvmOrientation(0);
				IDataPoint[] svmPoints = new IDataPoint[3];
				svmPoints[0] = listCircle.get(0);
				svmPoints[1] = listCircle.get(1);
				svmPoints[2] = listRectangle.get(0);
				this.dataSet.setsVMPoints(svmPoints);
			} else { // one circle and two rectangles are avail. 
				this.dataSet.setSvmOrientation(1);
				IDataPoint[] svmPoints = new IDataPoint[3];
				svmPoints[0] = listRectangle.get(0);
				svmPoints[1] = listRectangle.get(1);
				svmPoints[2] = listCircle.get(0);
				this.dataSet.setsVMPoints(svmPoints);
			}
		} else { 
			throw new IllegalArgumentException("Too much trainingsdata to compute support-vectors: " + size + ", requered: 3");
		}
		// New computation of separator
		this.computeSeparator();
	}

	/**
	 * Berchnet die Trennlinie zwischen den beiden Punktewolken
	 */
	@Override
	public void computeSeparator() {
		// TODO Auto-generated method stub
		IDataPoint[] points = this.dataSet.getSVMPoints();
		Vector a = points[0].toVector();
		Vector b = points[1].toVector();
		Vector c = points[2].toVector();
		
		Vector vectorAB = Vector.subtract(a, b);
		Vector vectorABinvert = Vector.getOrthogonal(vectorAB);
		
		/**
		 * Berechne Linearkombination von vektorAB und vectorABinvert zu c - a
		 * "Rechtwinkelig über die Straße gehen"
		 */
		double[] vectorCombination = Vector.linearcombination(vectorAB, vectorABinvert, Vector.subtract(c, a));
		
		this.vectorParallelToSeparator = vectorAB;
		this.vectorToSeparatorSide = vectorABinvert.multiply(vectorCombination[1] * 0.5);
		this.PointOnSeparator = Vector.add(a, this.vectorToSeparatorSide);
		/**
		 * Damit ergibt sich im 2D-Raum die Gerade des Separators (Hyperebene) als 
		 * g(x, y) = this.PointOnSeparator + n * this.vectorParallelToSeparator (die Straße entlang)
		 * mit dem orthogonalen Vektor this.vectorToSeparatorSide (von der Straßenmitte zum Rand der Straße in genau einem Schritt)
		 */
		Logger.log("Vector Parallel to Separator:");
		Logger.log("" + this.vectorParallelToSeparator);
		Logger.log("Vector Separator Side:");
		Logger.log("" + this.vectorToSeparatorSide);
		Logger.log("Point on Separator:");
		Logger.log("" + this.PointOnSeparator);
	}

	/**
	 * Bestimmt den Typ des gegebenen Datenpunkts anhand der bereitgestellten Daten. 
	 * Berechnet sich im Zweifelsfall 
	 */
	@Override
	public Figure classify(IDataPoint dataPoint) {
		// if current SVM is resetted or corrupted
		if(this.vectorParallelToSeparator == null || this.PointOnSeparator == null || this.vectorToSeparatorSide == null ) {
			Logger.log("SVM not fully initialized, aborting");
			return Figure.UNKNOWN;
		}
		Vector PointToClassify = dataPoint.toVector();
		
		double[] vectorCombination = Vector.linearcombination(this.vectorParallelToSeparator, this.vectorToSeparatorSide, Vector.subtract(PointToClassify, this.PointOnSeparator));
		
		/**
		 * Etwas viel Hirnschmalz führte zu dem Ergebnis: 
		 * die Orientation der SVM ist 0, wenn Kreise negative Werte liefern, 
		 * die Orientation der SVM ist 1, wenn Rechtecke negative Werte liefern. 
		 */
		Logger.log("SVM-Orientation (0: Kreise negativ): " + this.dataSet.getSvmOrientation());
		Logger.log("Distance: " + vectorCombination[1]);
		if(this.dataSet.getSvmOrientation() == 0) {
			if(vectorCombination[1] > 0) {
				return Figure.rectangle;
			} else if(vectorCombination[1] < 0) {
				return Figure.circle;
			} else {
				return Figure.UNKNOWN;
			}
		} else {
			if(vectorCombination[1] > 0)
			{
				return Figure.circle;
			} else if(vectorCombination[1] < 0) {
				return Figure.rectangle;
			} else {
				return Figure.UNKNOWN;
			}
		}
	}
}