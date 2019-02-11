package leImRo;

public interface IRecognition {

	public Figure recognize();

	void removeAll();

	void train(Figure figure);
}
