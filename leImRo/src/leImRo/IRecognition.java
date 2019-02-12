package leImRo;

public interface IRecognition {

	public Figure recognizeSVM();
	
	public Figure recognizeKNN();

	void removeAll();

	void train(Figure figure);
}
