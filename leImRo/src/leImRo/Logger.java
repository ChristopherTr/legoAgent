package legoAgent;

public class Logger {
	
	public static final String fileLocation = "logger.txt";
	
	public static void log(String str) {
		Long time = System.currentTimeMillis();
		String logline = String.format("%d - " + str, time);
		System.out.println(logline);
	}
	
}
