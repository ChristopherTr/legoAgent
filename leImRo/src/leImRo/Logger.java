package leImRo;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Logger {
	
	public static final String fileLocation = "logger.txt";
	private static FileWriter fw; 
	private static BufferedWriter bw;
	static {
		try {
			fw = new FileWriter(fileLocation);
			bw = new BufferedWriter(fw);
		} catch (Exception e) {
			// Silently ignore Exceptions ;)
		}
	}
	
	public static void log(String str) {
		Long time = System.currentTimeMillis();
		String logline = String.format("%d - " + str, time);
		System.out.println(logline);
		try {
			bw.write(logline);
		} catch (Exception e) {
			// Silently ignore Exceptions ;)
		}
	}
	
}
