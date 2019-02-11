package legoAgent;

public interface IDataPoint {
	
	public int getPerimeter();
	public int getArea();
	public Figure getFigure();
	public void setFigure(Figure f);
	public Vector toVector();
}
