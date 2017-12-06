package csp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Container for Courses.
 * Retrieves course by name.
 * Use to import & instantiate Courses from file.
 */
public class CourseList extends ArrayList<Course>{
	private Map<String, Course> mapNameToCourse;
	private String stringOfInputCourses= "";
	private String valueSeparator = "\\s+";
	private String[] inputStringArray;
	
	/**
	 * Instantiates courses from a parameter file.
	 * A semesterList is a necessary parameter in order to create
	 * the domain for each course.
	 * @param f File of courses
	 * @param m SemesterList to obtain value domains from.
	 */
	public void importCoursesFromFile(File f, SemesterList m) {
		Scanner scanner;
		String line = "";
		try {
			scanner = new Scanner(f);
			while(scanner.hasNextLine()){
				line = line.concat("\n"+scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
		}
		this.stringOfInputCourses = line;
		this.inputStringArray = line.split(this.valueSeparator);
		createCourses();
		createCourseDomains(m);
		createCourseMaps();
	}
	
	/**
	 * Assistant method to importCourses.
	 * Creates multiple courses from inputStringArray.
	 */
	private void createCourses() {
		String[] r = this.inputStringArray;
		//Starts at index 4 to skip headers, skips to next course index for loop.
		for(int i = 5; i < this.inputStringArray.length-3; i= i+4) {
			Course c = new Course(r[i], r[i+1], r[i+2], r[i+3]);
			this.add(c);
		}
	}
	/**
	 * Assistant method to importCourses to create domains for each course.
	 */
	private void createCourseDomains(SemesterList m) {
		// for each course in this courseList
		for(int i = 0; i < this.size(); i++) {
			// call its createDomain method 
			this.get(i).createDomain(m);
		}
	}
	/**
	 * Assistant method to importCourses.
	 * Creates course maps for retrieval.
	 */
	private void createCourseMaps() {
		this.mapNameToCourse = new HashMap<String, Course>();
		for(int i = 0; i < this.size(); i++) {
			this.mapNameToCourse.put(this.get(i).getName(), this.get(i)); 
		}
	}
	/**
	 * Retrieves a course by name.
	 * @param name of the course
	 * @return Course
	 */
	public Course getCourseByName(String name) {
		return this.mapNameToCourse.get(name);
	}
	/** 
	 * Returns a random course name from the list of Courses.
	 * @return String: courseName
	 */
	public String getRandomCourseName() {
		Random r = new Random();
		int range = this.size();
		return this.get(r.nextInt(range)).getName();
	}
	/**
	 * Returns the string captured while importing Courses from file.
	 * @return
	 */
	public String toStringInputFile() {
		return this.stringOfInputCourses;
	}
	/**
	 * Returns a string containing the domains of all courses in the list.
	 * @return
	 */
	public String toStringAllCourseDomains() {
		String s = "";
		for(int i = 0; i < this.size(); i++) {
			Course c = this.get(i);
			s = s.concat(c.toStringDomainBySpaces()+"\n");
		}
		return s;
	}
	private static final long serialVersionUID = 7843602543576923584L;
}
