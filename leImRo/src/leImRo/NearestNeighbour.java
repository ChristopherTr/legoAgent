package leImRo;

import java.util.ArrayList;

public class NearestNeighbour implements INearestNeighbour{
	
	private Dataset dataSet;
	private int kNeighbours = 0;
	
	/**
	 * Constructor
	 * @param dataset: Set dataset of classified Figures
	 */
	public NearestNeighbour(Dataset dataset) {
		this.dataSet = dataset;
	}
	
	
	/**
	 * Constructor
	 * @param dataset: Set dataset of classified Figures
	 * @param kNeighbours: Amount of selected nearest neighbours
	 */
	public NearestNeighbour(Dataset dataset, int kNeighbours) {
		this.dataSet = dataset;
		this.kNeighbours = kNeighbours;
	}
	
	/**
	 * Set amount of selected nearest neighbour
	 * @param:kNeighbour: Amount of selected nearest neighbours
	 */
	public void SetKNeighbours( int kNeighbour) {
		this.kNeighbours = kNeighbour;
	}
	
	/**
	 * Classify new Figure with k- Nearest Neighbour Algorithmn
	 * @param dataPoint: Figure to classify
	 */
	public Figure classify(IDataPoint dataPoint) {
		double[] distance = new double[this.kNeighbours];
		Figure searchedFigure = Figure.UNKNOWN;
		int neighbourCircleCount = 0;
		int neighbourRectCount   = 0;
		double oldValue;
		double newValue;
		double calcDistance;
		boolean insertPoint;
		IDataPoint pointToInsert;
		ArrayList<IDataPoint> listDataSet = this.dataSet.getAllData();
		//System.out.print(this.dataSet.toString());
		ArrayList<IDataPoint> listNearestKNeighbours = new ArrayList<IDataPoint>();
	
		if (listDataSet.size()<this.kNeighbours) {
			throw new IllegalArgumentException("Too few trainingsdata to compute K- Nearest Neighbour. Size of Dataset:"+listDataSet.size()+ "k- Parameter:"+ this.kNeighbours); 
		}
		else {
			//Step 1: setFirstElement in Neighbourhood
			listNearestKNeighbours.add(listDataSet.get(0));
			
			//Step 2: set first k Neighbourhoods and sort by distance
			for ( int outerLoop=1; outerLoop<this.kNeighbours ;outerLoop++) {
				insertPoint = false;
				pointToInsert = listDataSet.get(outerLoop);
				for(int innerLoop=0; innerLoop<listNearestKNeighbours.size()&& !insertPoint;innerLoop++) {
					if (calcEuklidDistance(dataPoint, pointToInsert)<calcEuklidDistance(dataPoint, listNearestKNeighbours.get(innerLoop))) {
						insertPoint = true;
						listNearestKNeighbours.add(innerLoop, pointToInsert);
					}
				}
				if (!insertPoint) {
					listNearestKNeighbours.add(listNearestKNeighbours.size(), pointToInsert);
				}
			}
			//Step 3: Set distances at first k neighbours
			for(int varCount=0; varCount<this.kNeighbours;varCount++) {
				distance[varCount] = calcEuklidDistance(dataPoint, listNearestKNeighbours.get(varCount));
			}
			//Step 4:Search k Nodes, that has the nearest distance to new datapoint
			for(int count=this.kNeighbours; count<listDataSet.size();count++) {
				insertPoint = false;
				pointToInsert=listDataSet.get(count);
				calcDistance = calcEuklidDistance(dataPoint, pointToInsert);
				//System.out.println("Distanz Punkt"+count+":"+calcDistance);
				if ( calcDistance<distance[this.kNeighbours-1] ) {
					for( int neighbourCount=0; neighbourCount<this.kNeighbours &&!insertPoint; neighbourCount++) {
						if ( calcDistance<distance[neighbourCount]) {
							//distance- Array anpassen
							insertPoint = true;
							listNearestKNeighbours.add(neighbourCount, pointToInsert);
							listNearestKNeighbours.remove(this.kNeighbours);
							newValue = calcDistance;
							do {
								oldValue = distance[neighbourCount];
								distance[neighbourCount] = newValue;
								newValue = oldValue;
								neighbourCount++;
							}while(neighbourCount<this.kNeighbours);
						}
					}
				}
			}
			for(IDataPoint element : listNearestKNeighbours) {
				if ( element.getFigure()==Figure.circle ) {
					neighbourCircleCount++;
				}
				else if (element.getFigure()==Figure.rectangle) {
					neighbourRectCount++;
				}
			}
			//Step 5: Evaluate type of new Datapoint
			if ( neighbourCircleCount>neighbourRectCount) {
				searchedFigure = Figure.circle;
			}
			else {
				searchedFigure = Figure.rectangle;
			}
		}
		dataPoint.setFigure(searchedFigure);
		//System.out.println("Testpunkt:");
		//System.out.print(dataPoint.toString());
		return searchedFigure;
	}
	
	
	/**
	 * Calc euclidean distance between two Figures 
	 * @param actDataPoint
	 * @param actPointToCompare
	 * @return euclidean Distance between this Figures
	 */
	private double calcEuklidDistance(IDataPoint actDataPoint, IDataPoint actPointToCompare) {
		double distance = 0.0;
		distance = Math.sqrt(Math.pow((actDataPoint.getArea()-actPointToCompare.getArea()),2)+Math.pow((actDataPoint.getPerimeter()-actPointToCompare.getPerimeter()),2));
		return distance;
	}

}
