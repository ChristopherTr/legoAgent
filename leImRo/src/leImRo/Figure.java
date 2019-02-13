package leImRo;


/**
 * 
 * Enumeration der bestehenden Figur-Arten
 *
 */
public enum Figure {

	UNKNOWN("???"),
	circle("0"),
	rectangle("1");
	
	private final String description;
	
	private Figure(String value) {
		this.description = value;
	}
	
	public String toString() {
		return this.description;
	}
}
