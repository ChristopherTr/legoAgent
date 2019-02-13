package leImRo;

/**
 * Basisklasse zur Beschreibung eines Datensatzes (ein gescanntes Bildes) mit den benoetigten Werten zur Erkennung
 */
public class DataPoint implements IDataPoint {
	/**
	 * periemeter and area of this data
	 */
	private int perimeter, area;
	/**
	 * Type of figure
	 */
	private Figure figure;
	
	public DataPoint(int perimeter, int area) {
		this.area = area;
		this.perimeter = perimeter;
		this.figure = Figure.UNKNOWN;
	}
	
	public DataPoint(int perimeter, int area, Figure figure) {
		this.area = area;
		this.perimeter = perimeter;
		this.figure = figure;
	}
	
	/**
	 * Returns the perimeter of this data. 
	 */
	@Override
	public int getPerimeter() {
		return this.perimeter;
	}

	/**
	 * returns the area of this data.
	 */
	@Override
	public int getArea() {
		return this.area;
	}

	/**
	 * returns the type of figure of this data, if set. 
	 * By constructor, if no figure is specified, Figure.UNKNOWN is set as value
	 */
	@Override
	public Figure getFigure() {
		return this.figure;
	}

	/**
	 * set Figure of DataPoint. Only possible if figure = UNKNOWN
	 */
	@Override
	public void setFigure(Figure f) {
		if(this.figure == Figure.UNKNOWN) {
			this.figure = f;
		}
	}
	
	/**
	 * Liefert die String-Repräsentation des Datenpunkts zurück
	 */
	public String toString() {
//		String form = "";
//		if (this.getFigure() == Figure.rectangle) {
//			form = "[]";
//		} else if (this.getFigure() == Figure.circle) {
//			form = "O";
//		} else {
//			form = "???";
//		}
		String ret = "Datapoint (area: " + this.getArea() + ", perimeter: " + this.getPerimeter() + ", Form: " + this.getFigure() + ")";
		return ret;
	}

	@Override
	public Vector toVector() {
		return new Vector(this.perimeter, this.area);
	}
}
