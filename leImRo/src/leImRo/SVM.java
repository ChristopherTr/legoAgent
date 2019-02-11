package legoAgent;
import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;

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
	 */
	@Override
	public IDataPoint[] findSupportVectors() {
		/*
		 * Fill each figure list
		 */
		ArrayList<IDataPoint> listDataSet = this.dataSet.getAllData();
		ArrayList<IDataPoint> listCircle = new ArrayList<IDataPoint>();
		ArrayList<IDataPoint> listRectangle = new ArrayList<IDataPoint>();
		for(IDataPoint laufDatum: listDataSet) {
			switch(laufDatum.getFigure()) {
			//case triangle:
			//	break;
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
		int size = this.dataSet.getAllData().size();
		if (size < 3) {
			throw new IllegalArgumentException("Too few trainingsdata to compute support-vectors: " + size + ", requered: 3+");
		} else if(size == 3) { // Easy case: onyl three points are avail. 
			IDataPoint[] circleArray = (IDataPoint[]) listCircle.toArray();
			IDataPoint[] rectangleArray = (IDataPoint[]) listRectangle.toArray();
			if(listCircle.size() == 2) { // 2 circles and one rectangle are avail. 
				this.dataSet.setSvmOrientation(0);
				IDataPoint[] svmPoints = new IDataPoint[3];
				svmPoints[0] = circleArray[0];
				svmPoints[1] = circleArray[1];
				svmPoints[2] = rectangleArray[0];
				this.dataSet.setsVMPoints(svmPoints);
			} else { // one circle and two rectangles are avail. 
				this.dataSet.setSvmOrientation(1);
				IDataPoint[] svmPoints = new IDataPoint[3];
				svmPoints[0] = rectangleArray[0];
				svmPoints[1] = rectangleArray[1];
				svmPoints[2] = circleArray[0];
				this.dataSet.setsVMPoints(svmPoints);
			}
		} else { // Difficult case: more than three points are avail. 
			examineSupportVectors(listRectangle, listCircle, classifierRectangle, classifierCircle);
			examineSupportVectors(listCircle, listRectangle, classifierCircle, classifierRectangle);
			
			// Viel Spaß Benny :P
		}
		
		return null;
	}

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
		 * mit dem orthogonalen Vektor this.vectorToSeparatorSide (von der Straßenmitte zum Rand der Straße in einem Schritt)
		 */
		
	}

	@Override
	public Figure classify(IDataPoint dataPoint) {
		Vector PointToClassify = dataPoint.toVector();
		
		double[] vectorCombination = Vector.linearcombination(this.vectorParallelToSeparator, this.vectorToSeparatorSide, Vector.subtract(PointToClassify, this.PointOnSeparator));
		
		/**
		 * Etwas viel Hirnschmalz führte zu dem Ergebnis: 
		 * die Orientation der SVM ist 0, wenn Kreise negative Werte liefern, 
		 * die Orientation der SVM ist 1, wenn Rechtecke negative Werte liefern. 
		 */
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
	
	//ToDo: Return the best "separator"
	//Aufbau Matrix
	//  		Node 1	Node 2	Node 3	Classification
	//Perimeter	a00     a01		a02		1/-1
	//Area		a10		a11		a12		1/-1
	//Default	1		1		1		1/-1
	private void examineSupportVectors(ArrayList<IDataPoint> hostList, ArrayList<IDataPoint> slaveList, int firstClassifier, int secondClassifier) {
		SimpleMatrix nodeMatrix = new SimpleMatrix(3, 3);
		
		//Klassifikationsvektor: Aufbau statisch, da innerhalb der Methode immer beispielsweise 1* Kreis und 2*Rechteck genutzt wird
		SimpleMatrix classificationVector = new SimpleMatrix(3,1);
		classificationVector.setRow(0, 0,firstClassifier);
		classificationVector.setRow(1, 0,secondClassifier);
		classificationVector.setRow(2, 0,secondClassifier);
		
		for(IDataPoint hostDatum: hostList) {
			for(IDataPoint firstSlaveDatum: slaveList) {
				for(IDataPoint secondSlaveDatum: slaveList) {
					nodeMatrix.set(0, 0, (hostDatum.getPerimeter()*2+hostDatum.getArea()*2+1));    //a00
					nodeMatrix.set(0, 1, (hostDatum.getPerimeter()*firstSlaveDatum.getPerimeter()+hostDatum.getArea()*firstSlaveDatum.getArea()+1));    //a01
					nodeMatrix.set(0, 2, (hostDatum.getPerimeter()*secondSlaveDatum.getPerimeter()+hostDatum.getArea()*secondSlaveDatum.getArea()+1));    //a02
				    
					nodeMatrix.set(1, 0, (firstSlaveDatum.getPerimeter()*hostDatum.getPerimeter()+firstSlaveDatum.getArea()*hostDatum.getArea()+1));    		//a10
					nodeMatrix.set(1, 1, (firstSlaveDatum.getPerimeter()*2+firstSlaveDatum.getArea()*2+1));													//a11	
					nodeMatrix.set(1, 2, (firstSlaveDatum.getPerimeter()*secondSlaveDatum.getPerimeter()+firstSlaveDatum.getArea()*secondSlaveDatum.getArea()+1));    //a12
				
					nodeMatrix.set(2, 0, (secondSlaveDatum.getPerimeter()*hostDatum.getPerimeter()+secondSlaveDatum.getArea()*hostDatum.getArea()+1));    		//a20
					nodeMatrix.set(2, 1, (secondSlaveDatum.getPerimeter()*firstSlaveDatum.getPerimeter()+secondSlaveDatum.getArea()*firstSlaveDatum.getArea()+1));//a21													//a11	
					nodeMatrix.set(2, 2, (secondSlaveDatum.getPerimeter()*2+secondSlaveDatum.getArea()*2+1));    													//a22	
					
					//Berechnung der Alphawerte
					SimpleMatrix alphaValues = nodeMatrix.solve(classificationVector);
					
					
					
					
					// Aufruf, damit die Klasse ohne Fehler ist: 
					alphaValues.bits();
					//Berechnung des w- Vektors zur Beurteilung der Hyperebene
					//tbd...
				}
			}
		}
	}

}
