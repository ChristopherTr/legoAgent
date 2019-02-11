package legoAgent;

public interface IRecognition {

	public Figure recognize();

	void removeAll();

	void addNewData(Figure figure);
}
