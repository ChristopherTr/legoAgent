package leImRo;

/*
 * Interfacebeschreibung der zentralen Recognition Klasse
 * alle benötigten Methoden (Einstiegspunkte vom Menue aus) werden hier angegeben 
 */
public interface IRecognition {

	public Figure recognizeSVM();
	
	public Figure recognizeKNN();

	void removeAll();

	void train(Figure figure);
}
