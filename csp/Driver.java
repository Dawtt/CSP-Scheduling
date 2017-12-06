package csp;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Driver {



	public static void main(String[] args) {
		
		// Input file variables
		File workingDirectory = new File(System.getProperty("user.dir"));
		String inputFileClassesName = "classes.txt";
		String inputFileConstraintsName = "constraints.txt";
		String inputFileSemesterMapName = "semesterMapping.txt";
		File inputFileClasses;
		File inputFileSemesterMap;
		File inputFileConstraints;
		
		// Input storage lists
		ConstraintList constraintList = new ConstraintList();
		CourseList courseList = new CourseList();
		SemesterList semesters = new SemesterList();
		
		// Initialize input files
		inputFileClasses = new File(workingDirectory, inputFileClassesName);
		inputFileConstraints = new File(workingDirectory, inputFileConstraintsName);
		inputFileSemesterMap = new File(workingDirectory, inputFileSemesterMapName);
		
		// Import: classes, constraints, & semester map
		constraintList.importConstraintsToString(inputFileConstraints);
		semesters.importSemesterMap(inputFileSemesterMap);
		courseList.importCoursesFromFile(inputFileClasses, semesters);
		
		// Instantiate CSP File
		CSPAlgorithm alg = new CSPAlgorithm(constraintList, courseList, semesters);
	
		// Run search
		Schedule returnedSchedule = alg.twoStageChoiceRandomSelectionAndRandomRestart();
		
		// Print final schedule to system out, and final & intermediate results to output file.
		System.out.println("\n"+alg.getLastStatsMostImproving()+"\n"+returnedSchedule.toString());
		pf(Util.getTracker()+"\n\n"+alg.getLastStatsMostImproving()+"\n"+returnedSchedule.toString());

		
	}
	/**
	 * print String to file
	 * @param s
	 */
	public static void pf(String s) {
		File workingDirectory = new File(System.getProperty("user.dir"));
		String outputFileName = "ConstraintSatisfactionOutput.txt";

		File outputFile = new File(workingDirectory, outputFileName);
		try (FileWriter fw = new FileWriter(outputFile, true)) {
			fw.write(s);
			fw.close();
		} catch (IOException e) {
		}	
		System.out.println("\nFinal and intermediate schedule results printed to file:\n\n" +outputFile);
	}
}
