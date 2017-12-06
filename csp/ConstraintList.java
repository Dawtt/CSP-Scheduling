package csp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contains specialized constraint functionality.
 * An instantiation is full list of constraints for a CSP.
 *
 */
public class ConstraintList {
	private String valueSeparator = "\\s+";
	private String stringOfConstraints = "";
	private List<String[]> constraintsString;
	
/**
 * Imports constraints from a File.
 * @param f
 */
	public void importConstraintsToString(File f) {
			Scanner scanner;
			String line = "";
			String[] rowTokens;
			this.constraintsString = new ArrayList<String[]>();
			try {
				scanner = new Scanner(f);
				while(scanner.hasNextLine()){
					line = scanner.nextLine();
					rowTokens = line.split(this.valueSeparator);
					this.constraintsString.add(rowTokens);
					this.stringOfConstraints = this.stringOfConstraints.concat(line + "\n");
				}
				scanner.close();
			} catch (FileNotFoundException e) {
			}
			line.trim();
			this.stringOfConstraints = line;

	}
	/**
	 * Returns number of constraints
	 * @return
	 */
	public int size() {
		return this.constraintsString.size();
	}
	public String toString() {
		return this.stringOfConstraints;
	}
	/**
	 * Returns the name of the first course of the constraint at (index).
	 * @param index
	 * @return
	 */
	public String courseName1(int index) {
		return this.constraintsString.get(index)[0];
	}
	/**
	 * Returns the name of the last course of a constraint at (index).
	 * @param index
	 * @return
	 */
	public String courseName2(int index) {
		return this.constraintsString.get(index)[2];
	}
	/**
	 * Returns a string representation of the operator value for constraint at parameter index.
	 * @param index
	 * @return
	 */
	public String op(int index) {
		return this.constraintsString.get(index)[1];
	}
	/**
	 * Returns the constraint at parameter index of the imported from file constraints String.
	 * @param index
	 * @return
	 */
	public String line(int index) {
		return this.constraintsString.get(index)[0] + " " +
				this.constraintsString.get(index)[1] + " " +
				this.constraintsString.get(index)[2];
	}

}

