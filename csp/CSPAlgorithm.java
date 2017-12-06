package csp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
	 * Constraint Satisfaction for Course Semester Problem.
	 * 
	 * INPUT:
	 * 	V.		set of variables:	CourseList
	 * 	dom		domain function:	Course.domain
	 * 	A[V]	array of Values		Schedule
	 * 
	 */
public class CSPAlgorithm {
	// used in constructor
	private ConstraintList c;
	private static CourseList courseList;
	private SemesterList semesterList;
	// general variables
	private int iterationCount;
	private int restartCount;
	private int courseValueWalks;
	private double totalPossibleSchedules;
	private int minConflictsFound = Integer.MAX_VALUE;
	private static Queue<String> tabuCourses = new LinkedList<>();
	private boolean scheduleFound;
	
	
	// Settable maximum iterations & size variables
	private int randomSamplingMax = 5000;
	private int twoStageChoiceIterations = 1000   ;
	private int highestViolationsWalkMaxIterations = 10000;
	private int tabuCoursesSize = 4;
	private int holeIterationsUntilRandomRestart = 40;	// same concurrent schedule conflicts 

	
	/**
	 * An instantiation will use the same input lists when its search methods are used.
	 * @param c
	 * @param courseListIn
	 * @param sl
	 */
	CSPAlgorithm(ConstraintList c, CourseList courseListIn, 
			SemesterList sl){
		this.c = c;
		CSPAlgorithm.courseList = courseListIn;
		this.semesterList = sl;	
		calculateTotalPossibleSchedules();
	}

	/**
	 * Performs a local search in this way:
	 * 	Random Initialization
	 * 		Two-Stage Choice
	 * 			Random Selection
	 * 				Random Restart
	 * 
	 * Uses assistant method walkThroughCourseValues()
	 * @return
	 */
	public Schedule twoStageChoiceRandomSelectionAndRandomRestart() {
		int minConflictsFound = Integer.MAX_VALUE;
		this.iterationCount = 0;		// tracks iterations of every step 
		this.restartCount = -1;  	// tracks total random restarts of schedule values.
		Schedule schedule = new Schedule(this.semesterList, this.c, CSPAlgorithm.courseList);
		// random initialization of schedule
		for(int i = 0; i < this.twoStageChoiceIterations; i++) {
			this.iterationCount++;
			this.restartCount++;
			
			// create a random schedule & calculate violations
			schedule.createRandomSchedule();
			schedule.calculateAllViolations();
			
			// track all intermediate schedules
			Util.intermediateTracker(schedule.toString()+"\n");
			
			// set variables
			int conflictTotal = schedule.getTotalConflicts();
			int previousConflictTotal = 0;
			int sameResultCount = 0;
			
			// Track minimum conflicts found
			if(conflictTotal < minConflictsFound) {
				minConflictsFound = conflictTotal;
			}
			
			// stop and return schedule if no conflicts
			if(conflictTotal == 0) {
				this.scheduleFound = true;
				return schedule;
			}
			
			// two-stage choice & random walks
			// loop will terminate if X concurrent schedules have same conflict number
			for(int j = 0; j < this.highestViolationsWalkMaxIterations 
					&& sameResultCount < this.holeIterationsUntilRandomRestart ; j++) {
				this.iterationCount++;
				String courseToWalk = schedule.getCourseWithMostConflicts();
				
				// check tabu list of previously walked courses, use random course if so.
				if(tabuCourses.contains(courseToWalk)){
					courseToWalk = CSPAlgorithm.courseList.getRandomCourseName();
				}
				tabuCoursesAdd(courseToWalk);

				// create a new schedule: the lowest conflict schedule for the chosen course
				schedule = walkThroughCourseValues(schedule, courseToWalk);
				int totalConflicts = schedule.getTotalConflicts();
				
				// track intermediate schedules
				Util.intermediateTracker(schedule.toString()+"\n");
				
				// track number of concurrent schedules with same conflict number 
				// for random restart
				previousConflictTotal = totalConflicts;
				if(totalConflicts == 0) {
					this.scheduleFound = true;
					return schedule;
				}
				if(totalConflicts == previousConflictTotal) {
					sameResultCount++;
				}
				if(totalConflicts != previousConflictTotal){
					sameResultCount = 0;
				}
			}
		}		
		this.scheduleFound = false;
		return schedule;
	}
	
	/**
	 * Assistant Method to Most Improving search.
	 * Walks through the values of a single course, 
	 * and returns the schedule with the least constraint violations.
	 * @param schedule
	 * @return
	 */
	private Schedule walkThroughCourseValues(Schedule schedule, String courseInToWalk) {

		// set variables
		List<Schedule> scheduleList = new ArrayList<>();
		int bestScheduleIndex = 0;
		int minimumConflicts = Integer.MAX_VALUE;
		int iterationConflicts;
		
		Course courseToWalk = CSPAlgorithm.courseList.getCourseByName(courseInToWalk);
		for(int j = 0; j < courseToWalk.getDomainSize(); j++) {
			this.courseValueWalks++;
			Schedule loopSchedule = new Schedule(schedule.getCourseValueMap());
			scheduleList.add(j, loopSchedule);
			Integer newCourseValue = courseToWalk.getDomainEntry(j);

			loopSchedule.setCourseValue(courseInToWalk, newCourseValue);
			loopSchedule.calculateAllViolations();
			iterationConflicts = loopSchedule.getTotalConflicts();

			if(iterationConflicts < minimumConflicts) {
				bestScheduleIndex = j;
				minimumConflicts = iterationConflicts;
			}
		}
		return scheduleList.get(bestScheduleIndex);
	}
	
	/**
	 * Quote from textbook: "The while loop is never executed.
	 * Random sampling keeps picking random assignments until 
	 * it finds one that satisfies the constraints, 
	 * and otherwise it... " 
	 * this method will stop at set iterationMax.
	 */
	public Schedule randomSampling() {
		this.minConflictsFound = Integer.MAX_VALUE;
		this.iterationCount = 0;
		Schedule schedule = new Schedule(this.semesterList, this.c, CSPAlgorithm.courseList);
		
		// random schedule loop.
		for(int i = 0; i < this.randomSamplingMax; i++) {
			this.iterationCount++;
			// random initialization of schedule
			schedule.createRandomSchedule();
			
			// check if constraints are met
			schedule.calculateAllViolations();
			int conflictTotal = schedule.getTotalConflicts();
			
			// track minimum conflicts
			if(conflictTotal < this.minConflictsFound) {
				this.minConflictsFound = conflictTotal;
			}
			
			// terminate and return schedule if no conflicts
			if(schedule.getTotalConflicts() == 0) {
				this.scheduleFound = true;
				return schedule;
			}			
		}		
		this.scheduleFound = false;
		return schedule;
	}

	/**
	 * Used to create a max queue size for the tabu courses.
	 * if size is less than static parameter,
	 * adds, else removes the head of queue and recalls itself.
	 * @param schedule
	 */
	private void tabuCoursesAdd(String courseName) {
		if(tabuCourses.size() <= this.tabuCoursesSize) {
			tabuCourses.add(courseName);
		}
		else {
			tabuCourses.remove();
			tabuCoursesAdd(courseName);
		}
	}
	/**
	 * Calculates total of possible schedules by size of domain for each variable.
	 */
	private void calculateTotalPossibleSchedules() {
		this.totalPossibleSchedules = 1;
		int multiplier = 0;
		for(int i = 0; i < courseList.size(); i++) {
			Course c = courseList.get(i);
			multiplier = c.getDomainSize();
			this.totalPossibleSchedules *= multiplier;
		}
	}

	public void setIterationMax(int max) {
		this.twoStageChoiceIterations = max;
	}
	/**
	 * Returns iteration count of last ran search.
	 * @return
	 */
	public int getIterationCount() {
		return this.iterationCount;
	}
	/**
	 * gets iteration count of last ran search.
	 * @return
	 */
	public int getRestartCount() {
		return this.restartCount;
	}
	public int getMinConflictsFound() {
		return this.minConflictsFound;
	}
	public void setMostImprovingIterationsMax(int max) {
		this.twoStageChoiceIterations = max;
	}
	public void setHightestViolationsWalkMaxIterations(int max) {
		this.highestViolationsWalkMaxIterations = max;
	}
	public int getTabuCoursesSize() {
		return this.tabuCoursesSize;
	}
	public void setTabuCoursesSize(int tabuCoursesSize) {
		this.tabuCoursesSize = tabuCoursesSize;
	}
	public int getHoleIterationsUntilRandomRestart() {
		return this.holeIterationsUntilRandomRestart;
	}

	public void setHoleIterationsUntilRandomRestart(int holeIterationsUntilRandomRestart) {
		this.holeIterationsUntilRandomRestart = holeIterationsUntilRandomRestart;
	}
	
	public int getCourseValueWalks() {
		return this.courseValueWalks;
	}

	/**
	 * Returns a String statement describing the last results.
	 * @return String
	 */
	public String getLastStatsMostImproving() {
		String s = "";
		String formattedTotalCount = String.format("%.03e", this.totalPossibleSchedules);
		if(this.scheduleFound) {
			s = s.concat(
					"A successful schedule has been found.\nIt took "+
					this.iterationCount+" iterations including "+ this.restartCount+ 
					" random restarts, plus " +this.courseValueWalks+ " variable value checks to find the schedule"
							+ " out of a possible "+ formattedTotalCount +" number of schedules.");
		}
		if(!this.scheduleFound) {
			s = s.concat(
					"No successful schedule was found by Most Improving.\nSearch took "+
					this.iterationCount+" iterations including "+ this.restartCount+
					" random restarts, plus " +this.courseValueWalks+ " variable value checks looking for the schedule.");
		}
		return s;
	}
	
	/**
	 * Returns a String statement describing the last results.
	 * @return String
	 */
	public String getLastStatsRandomSampling() {
		String s = "";
		if(this.scheduleFound) {
			s = s.concat(
					"A successful schedule has been found by Random Sampling.\nIt took "+
					this.iterationCount+" iterations to find the schedule.");
		}
		if(!this.scheduleFound) {
			s = s.concat(
					"No successful schedule was found by Random Sampling.\nSearch took "+
					this.iterationCount+ 
					" iterations looking for the schedule.\n"
					+ "Minimum conflict schedule found by search had "+ this.minConflictsFound
							+ " conflicts.");
		}
		return s;
	}
}
