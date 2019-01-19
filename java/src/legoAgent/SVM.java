package legoAgent;
import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;

public class SVM implements ISVM {

	//@Override
	private static int classifierRectangle = 1;
	private static int classifierCircle = -1;
	public IDataPoint[] findSupportVectors() {
	ArrayList <IDataPoint> listCircle = new ArrayList<IDataPoint>();
	ArrayList <IDataPoint> listRectangle = new ArrayList<IDataPoint>();
	Dataset dataSet = new Dataset();
	
	ArrayList <IDataPoint>listDataSet= dataSet.getAllData();
	
	/*
	 * Fill each figure list
	 */
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
	
	/*
	 * Ermitteln der besten Hyperebene �ber alle Kombinationen von Knoten
	 */
	examineSupportVectors(listRectangle, listCircle, classifierRectangle, classifierCircle);
	examineSupportVectors(listCircle, listRectangle, classifierCircle, classifierRectangle);
		return null;
	}

	@Override
	public void computeSeparator() {
		// TODO Auto-generated method stub

	}

	@Override
	public Figure classify(IDataPoint dataPoint) {
		// TODO Auto-generated method stub
		return null;
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
					
					//Berechnung des w- Vektors zur Beurteilung der Hyperebene
					//tbd...
				}
			}
		}
	}

}
