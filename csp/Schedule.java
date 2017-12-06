package csp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** 
 * The primary tracking class of course values.
 * Calculates and tracks conflicts for each course as well as total conflicts for schedule.
 * 
 * A semester is mapped to a list which contains it's assigned courses.
 * Each course is mapped to the list it has the value of.
 * 
 * An instantiation represents one schedule.
 * A deep copy of a schedule is made by sending the constructor the map of course value 
 *
 */
public class Schedule {
	
	// static variables
	private static ConstraintList constraintList;
	private static SemesterList semesterList;
	private static CourseList courseList;
	private static int scheduleCount = 0;

	// scheduleMain is the primary schedule container, each semesters entry is formated:   
	// semesterName  |  semesterID  |  courseID  | day 
	// note first two entries of each semesters list are the semester name & semester ID.
	private List<ArrayList<String>> scheduleMain;  

	private int scheduleID; // assigned by scheduleCount 
	private int totalConflicts;
	private String courseWithMostConflicts;

	// maps to reduce iteration workloads
	private Map<String, Integer> mapCourseNameToValue; // Course Value is the SemesterID+dayValue
	private Map<String, ArrayList<String>> mapCourseNameToScheduleList; // CourseName  |  schedule index containing
	private Map<String, Integer> mapCourseNameToConflicts;
	private Map<ArrayList<String>, String> mapScheduleEntryToSemesterName;
	private Map<Integer, ArrayList<String>> mapSemesterIndexToScheduleEntry;
	private Map<String, ArrayList<String>> mapSemesterNameToScheduleEntry;
		
	/**
	 * Use this constructor to make a deep copy of a different schedule.
	 * Send this constructor the value map of the schedule to copy.
	 * @param inputCourseValueMap
	 */
	Schedule(Map<String, Integer> inputCourseValueMap){
		scheduleCount++;
		
		//initialize basic variables
		this.scheduleID = scheduleCount;
		this.scheduleMain = new ArrayList<>();
		this.totalConflicts = 0;
		this.mapCourseNameToScheduleList = new HashMap<>();
		this.mapSemesterNameToScheduleEntry = new HashMap<>();
		this.mapSemesterIndexToScheduleEntry = new HashMap<>();
		this.mapScheduleEntryToSemesterName = new HashMap<>();
		
		// call methods to set values for primary schedule & maps
		initializeScheduleMain();
		copyInputValueMaps(inputCourseValueMap);
		initializeCourseMap();

		// set course with most conflicts to arbitrary starting course
		this.courseWithMostConflicts = courseList.get(0).getName();
	}
	
	/**
	 * From a valueMap, creates:
	 * mapCourseNameToValue
	 * mapCourseNameToScheduleList
	 * @param inputMap
	 */
	private void copyInputValueMaps(Map<String, Integer> inputMap) {
					
			// initialize courseValueMap
			this.mapCourseNameToValue = new HashMap<>();
			this.mapCourseNameToScheduleList = new HashMap<>();
			
			// set the values for the maps
			for(int i = 0; i < courseList.size(); i++) {
				
				// set variables to be added
				String courseNameLoop = courseList.get(i).getName();
				int courseVal = inputMap.get(courseNameLoop);
				Day d = Day.values()[courseVal % 10]; // extract the day from the course value
				int semID = courseVal / 10; // remove the day value from the end of course value
				Semester semLoop = semesterList.getSemesterByID(semID);
				ArrayList<String> semesterCoursesLoop = 
						this.mapSemesterNameToScheduleEntry.get(semLoop.getName());
				
				// add variables
				this.mapCourseNameToScheduleList.put(courseNameLoop, semesterCoursesLoop);
				this.mapCourseNameToValue.put(courseNameLoop, courseVal);
				semesterCoursesLoop.add(courseNameLoop);
				semesterCoursesLoop.add(d.toString());
			}
	}

	/**
	 * Constructor for initial schedule.
	 * @param s
	 * @param cl
	 * @param courseListIn
	 */
	Schedule(SemesterList s, ConstraintList cl, CourseList courseListIn){
		scheduleCount++;
		
		//imported data lists
		semesterList = s;
		constraintList = cl;
		courseList = new CourseList();
		courseList = courseListIn;
		
		this.scheduleID = scheduleCount;
		this.scheduleMain = new ArrayList<>();
		this.totalConflicts = 0;
		
		//instantiate maps
		this.mapCourseNameToValue = new HashMap<>();
		this.mapCourseNameToScheduleList = new HashMap<>();
		this.mapSemesterNameToScheduleEntry = new HashMap<>();
		this.mapSemesterIndexToScheduleEntry = new HashMap<>();
		this.mapScheduleEntryToSemesterName = new HashMap<>();
		
		//call initialization methods
		initializeScheduleMain();
		initializeCourseMap();
		
		// set course with most conflicts to arbitrary starting course
		Course courseToCompare = courseList.get(0);
		String courseNameToCompare = courseToCompare.getName();
		this.courseWithMostConflicts = courseNameToCompare;
	}

	/**
	 * Replaces the previous scheduleMain with one with no values for each semester in it.
	 * Sets mapping values for new schedule
	 * @param sL
	 */
	public void initializeScheduleMain() {
		this.scheduleMain = new ArrayList<>();
		
		// Create each semester's starting entry in scheduleMain
		for(int i = 0; i < semesterList.numberOfSemesters(); i++) {
			ArrayList<String> list = new ArrayList<>();
			Integer semesterID = semesterList.getSemesterID(i);
			String semesterName = semesterList.getSemesterName(semesterID);
			list.add(semesterName);
			list.add(String.valueOf(semesterID));
			this.scheduleMain.add(i, list);
			
			// set the map values to the scheduleMain entries
			this.mapSemesterNameToScheduleEntry.put(semesterName, this.scheduleMain.get(i));
			this.mapSemesterIndexToScheduleEntry.put(i, this.scheduleMain.get(i));
			this.mapScheduleEntryToSemesterName.put(list, semesterName);
		}
	}

	/**
	 * called when a new schedule is created to set starting values of zero
	 * to the course conflict tracking map. 
	 * Schedule's addConflict() method assumes that all courses are mapped, 
	 * and does not check to reduced workload of that method.
	 */
	private void initializeCourseMap() {
		this.mapCourseNameToConflicts = new HashMap<>();
		for (int i = 0; i < courseList.size(); i++) {
			String name = courseList.get(i).getName();
			this.mapCourseNameToConflicts.put(name, 0);
		}
	}
	/**
	 * Creates a random starting schedule.
	 * Can be used for a random restart.
	 * This needs to be run for a new schedule.
	 */
	public void createRandomSchedule(){
		scheduleCount++;
		initializeScheduleMain();
	    Random r = new Random();
	    // r.setSeed( 129 );
	    
	    for ( int courseCount = 0; courseCount < 30; courseCount++ ) {
	    	// get this iterations course & domain size
	        Course course = courseList.get(courseCount);
	    	int domainSize = course.getDomainSize();
	    	
	        // get a random value for the course from its domain
	        int randomCourseValue = r.nextInt( domainSize );
	        int value = course.getDomainEntry(randomCourseValue);
	        
	        // assign that value to this schedule
	        setCourseValueComplete(course.getName(), value);
	    }
	}

	/**
	 * calculate all constraint violations
	 */
	public void calculateAllViolations() {
		//int course1;
		//int course2;
		String courseName1;
		String courseName2;
		String opString;
		int course1Semester;
		int course2Semester;
		//int countNotViolated; // counts number of non-violations to assign to schedule
		
		//remove previous counts
		clearConflicts();
		
		for(int i = 0; i < constraintList.size(); i++) {
			
			// Get course names from current constraint line
			courseName1 = constraintList.courseName1(i);
			courseName2 = constraintList.courseName2(i);
			
			// Get course values of schedule to compare for constraint i
			// Note that courseValue needs last character removed to compare semesters
			//    and not compare semester + day
			course1Semester = getCourseValue(courseName1) / 10;
			course2Semester = getCourseValue(courseName2) / 10;
			
			// Get operator from constraint list:
			opString = constraintList.op(i);
			
			// convert opString to to operation
			if(opString.equals("<")){
				
				// compare the values assigned to the courses by the schedule
				if(course1Semester >= course2Semester) {
					addConflict(courseName1);
					addConflict(courseName2);
				}
			}
			if(opString.equals("<=")) {
				if(course1Semester > course2Semester) {
					addConflict(courseName1);
					addConflict(courseName2);
				}
			}
		}
		calculateSameDayViolations();
	}
	
	/** 
	 * Iterates through each semester, checking if any courses on same day.
	 */
	public void calculateSameDayViolations() {
		for(int i = 0; i < this.scheduleMain.size(); i++) {
			List<String> semesterClassList = this.scheduleMain.get(i);
			
			// course days are on odd indexes for course at previous index.
			for(int j = 3; j < semesterClassList.size(); j=j+2) {
				for(int k = j+2; k < semesterClassList.size(); k=k+2) {
					if(semesterClassList.get(j).equals(semesterClassList.get(k))) {
						
						// add a conflict for each course on the same day.
						addConflict(semesterClassList.get(j-1));
						addConflict(semesterClassList.get(k-1));
					}
				}
			}
		}
	}
	/**
	 * increases the conflict count for an input course by name
	 * @param courseName
	 */
	public void addConflict(String courseName) {
		this.totalConflicts++;
		int conflicts = this.mapCourseNameToConflicts.get(courseName);
		conflicts++;
		this.mapCourseNameToConflicts.put(courseName, conflicts);
		
		// update highest conflict course
		if(conflicts > this.mapCourseNameToConflicts.get(this.courseWithMostConflicts)) {
			this.courseWithMostConflicts = courseName;
		}
	}
	
	/**
	 * Removes current conflicts.
	 */
	public void clearConflicts() {
		initializeCourseMap();
		this.totalConflicts = 0;
	}
	
		/**
	 * Checks if course has a preset value and will delete it.
	 * Sends course value to courseValueComplete after.
	 * @param courseName
	 * @param value The semester ID+Day
	 */
	public void setCourseValue(String courseName, int value) {
		// check if the course is already assigned a value, and delete
		if(this.mapCourseNameToScheduleList.get(courseName) != null) {
			ArrayList<String> list = this.mapCourseNameToScheduleList.get(courseName);
			int index = list.indexOf(courseName);
			list.remove(index+1);
			list.remove(index);
		}
		// establish the new value for the course
		setCourseValueComplete(courseName, value);
	} 
	
	/**
	 * Sets all required attributes for the input course value.
	 * Used when initializing the schedule.
	 * Course value is semesterID+day
	 * @param courseName
	 * @param Value
	 */
	private void setCourseValueComplete(String courseName, int value) {
		
		int semesterId; 
		Day day = Day.values()[value % 10];
		semesterId = value / 10; // remove the day value from the end of course value
		
		Semester semester = semesterList.getSemesterByID(semesterId);
		ArrayList<String> semesterCourses = this.mapSemesterNameToScheduleEntry.get(semester.getName());
		
		this.mapCourseNameToValue.put(courseName, value);
		this.mapCourseNameToScheduleList.put(courseName, semesterCourses);
		semesterCourses.add(courseName);
		
		// add the day of the course
		semesterCourses.add(day.toString());
	}
	/**
	 * returns the name of the course with most conflicts
	 * @return
	 */
	public String getCourseWithMostConflicts() {
		return this.courseWithMostConflicts;
	}
	/**
	 * Returns the value given to the parameter course by this schedule.
	 * @param c Course to get the schedules value for.
	 * @return int of the course value.
	 */
	public Integer getCourseValue(Course c) {
		return this.mapCourseNameToValue.get(c.getName());
	}
	/**
	 * Returns the value given to the parameter course by this schedule.
	 * @param courseName String name of the course to get value for.
	 * @return int of the course value.
	 */
	public Integer getCourseValue(String courseName) {
		return this.mapCourseNameToValue.get(courseName);
	}
	/**
	 * Returns the map of course names to course values.
	 * @return
	 */
	public Map<String, Integer> getCourseValueMap(){
		return this.mapCourseNameToValue;
	}
	/**
	 * Returns the schedule instantiation number (ID)
	 * @return
	 */
	public int getScheduleID() {
		return this.scheduleID;
	}
	/**
	 * Returns the semester a course is assigned in the schedule.
	 * Assumes semester ID is an int.
	 * @param c
	 * @return semesterID 
	 */
	public static int getSemesterAssignmentForCourse(Course c) {
		String name = c.getName();
		name = name.substring(0, name.length()-1);
		return Integer.getInteger(name);
	}
	/**
	 * Returns the semester a course is assigned in the schedule.
	 * Assumes semester ID is an int.
	 * @param String courseName
	 * @return semesterID 
	 */
	public static int getSemesterAssignmentForCourse(String cName) {
		String assignment = cName.substring(0, cName.length()-1);
		return Integer.getInteger(assignment);
	}
	/**
	 * Returns the total number of schedules instantiated for this session.
	 * @return
	 */
	public static int getTotalSchedulesMade() {
		return scheduleCount;
	}
	/**
	 * Returns the total number of constraint conflicts this schedule has.
	 * Assumes that calculateTotalConstraints() has been run for this schedule.
	 * @return
	 */
	public int getTotalConflicts() {
		return this.totalConflicts;
	}

	/** 
	 * This returns an output string of the schedule in the format 
	 * requested by assignment.
	 */
	public String toString() {
		String s = "";
		List<String> semesterCourses = new ArrayList<>();
		//String semesterName = "";
		Semester semester;
		String courseName = "";
		String day = "";
		
		// for each semester get each class & day, add it to next line of string
		for(int i = 0; i < this.scheduleMain.size(); i++) {
			semesterCourses = this.scheduleMain.get(i);
			semester = semesterList.getSemesterByIndex(i);
			
			//semesterName = semester.getName();
			s = s.concat(String.valueOf(semester.getID())+":");
			for(int j = 2; j < semesterCourses.size(); j=j+2) {
				courseName = semesterCourses.get(j);
				day = semesterCourses.get(j+1);
				s = s.concat("\t"+courseName+"-"+day);
			}
			s = s.concat("\n");
		}
		s.trim();
		return s;
	}

	/**
	 * this performs the same logic as calculateAllViolations(),
	 * excluding the same-day calculations.
	 * however it outputs details of the calculations for examination.
	 */
	public String examineConstraintListViolations() {
		String s = "";
		String courseName1;
		String courseName2;
		String opString;
		int course1Semester;
		int course2Semester;
		
		//remove previous counts
		clearConflicts();
		
		for(int i = 0; i < constraintList.size(); i++) {
			
			// Get course names from current constraint line
			courseName1 = constraintList.courseName1(i);
			courseName2 = constraintList.courseName2(i);
			
			// Get course values of schedule to compare for constraint i
			// Note that courseValue needs last character removed to compare semesters
			//    and not semester + day
			course1Semester = getCourseValue(courseName1) / 10;
			course2Semester = getCourseValue(courseName2) / 10;
			
			// Get operator from constraint list:
			opString = constraintList.op(i);
			
			s = s.concat("Constraint Line Test:\n"
					+ "File Input : "+constraintList.line(i) + "\n"
					+ "courseName1: " + courseName1 +" "
					+ "course1Semester: "+ course1Semester+"\n"
					+ "courseName2: " + courseName2 +" " 
					+ "course2Semester: "+ course2Semester+"\n"
					);
			
			
			// convert opString to to operation: probably call method if constraint violated, with courses violated for. 
			// or leave a boolean to run the constraint violation
			if(opString.equals("<")){
	
				
				// compare the values assigned to the courses by the schedule
				if(course1Semester >= course2Semester) {
					s = s.concat(course1Semester + " is greater than or equal to "
							+ course2Semester);
					addConflict(courseName1);
					addConflict(courseName2);
				}
				
			}
			if(opString.equals("<=")) {
				if(course1Semester > course2Semester) {
					s = s.concat(course1Semester + " is greater than "
							+ course2Semester);
					addConflict(courseName1);
					addConflict(courseName2);
				}
			}
			if(course1Semester == course2Semester) {
				s = s.concat(course1Semester + " is equal to "
						+ course2Semester);
			}
			if(course1Semester > course2Semester) {
				s = s.concat(course1Semester + " is greater than "
						+ course2Semester);
			}
			if(course1Semester < course2Semester) {
				s = s.concat(course1Semester + " is less than "
						+ course2Semester);
			}
			s = s.concat("\n");
		}
		return s;
	}

	/**
	 * this performs the same logic as calculateSameDayViolations(),
	 * however it outputs
	 * details of the calculations.
	 */
	public void examineSameDayViolations() {
		for(int i = 0; i < this.scheduleMain.size(); i++) {
			List<String> semesterClassList = this.scheduleMain.get(i);
			
			// course days are on odd indexes for course at previous index.
			for(int j = 3; j < semesterClassList.size(); j=j+2) {
				for(int k = j+2; k < semesterClassList.size(); k=k+2) {
					if(semesterClassList.get(j).equals(semesterClassList.get(k))) {
						
						// add a conflict for each course on the same day.
						addConflict(semesterClassList.get(j-1));
						addConflict(semesterClassList.get(k-1));
					}
				}
			}
		}
	}
}
