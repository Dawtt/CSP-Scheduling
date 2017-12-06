package csp;

/**
 * Generalized utility methods,
 * to refactor to other classes as details implemented.
 *
 */
public class Util {

	private static String intermediateSchedules = "";
	
	
	public static void intermediateTracker(String s) {
		intermediateSchedules = intermediateSchedules.concat(s);
	}
	
	public static String getTracker() {
		return intermediateSchedules;
	}

}
enum Season {FALL, SPRING, SUMMER};
enum Day{M,T,W,H,F,S,O;};
