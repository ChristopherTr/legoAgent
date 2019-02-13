package leImRo;

public enum Figure {

	UNKNOWN("???"),
	circle("O"),
	rectangle("[]");
	
	private final String description;
	
	private Figure(String value) {
		this.description = value;
	}
	
	public String toString() {
		return this.description;
	}
}
