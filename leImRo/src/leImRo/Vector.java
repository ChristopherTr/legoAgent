package leImRo;

public class Vector {
	private double x;
	private double y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public static Vector subtract(Vector a, Vector b) {
		return new Vector((int) (a.getX() - b.getX()), (int) (a.getY() - b.getY()));
	}
	
	public static Vector add(Vector a, Vector b) {
		return new Vector((int) (a.getX() + b.getX()), (int) (a.getY() + b.getY()));
	}
	
	public Vector multiply(double a) {
		return new Vector(this.x * a, this.y * a);
	}

	public static Vector getOrthogonal(Vector toInvert) {
		return new Vector(- toInvert.getY(), toInvert.getX());
	}
	
	/**
	 * 
	 * Berechne Linearkombination von vektorA und vectorB zu vectorC
	 * a * vectorA + b * vectorB = vectorC
	 * 
	 * Mit Mathe-Magie ergibt sich: 
	 *     
	 *             x3
	 *     y3 -  ------  *  y2
	 *             x2
	 * a = ---------------------
	 *             x1
	 *     y1 -  ------  *  y2
	 *             x2
	 * 
	 * und 
	 * 
	 *      x3 - a  * x1
	 * b = ------------------
	 *           x2
	 * 
	 * returns double{a, b}
	 */
	public static double[] linearcombination(Vector vector1, Vector vector2, Vector destination) {
		double x1 = vector1.getX(), x2 = vector2.getX(), x3 = destination.getX();
		double y1 = vector1.getY(), y2 = vector2.getY(), y3 = destination.getY();
		
		double a = (y3 - (x3/x2) * y2) / (y1 - (x1/x2) * y2);
		double b = (x3 - a * x1) / x2;
		
		double[] ret = {a, b};
		
		return ret;
	}
}
