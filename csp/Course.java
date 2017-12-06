package csp;
import java.util.ArrayList;
import java.util.List;

/**
 * A course and it's domain of possible values.
 * Can calculate and return total course domain.
 *
 */
public class Course {
	private String name;

	// Lists of available days by season
	private Day[] fallDaysList;
	private Day[] springDaysList;
	private Day[] summerDaysList;
	
	private String fallString;
	private String springString;
	private String summerString;
	
	private String[] fallIn;
	private String[] springIn;
	private String[] summerIn;
	
	
	// list of all possible values of the course variable, in numerical order.
	private List<Integer> domain;
	//private int domainLength; // 
	
	Course(String name, String fallPar, String springPar, String summerPar){
		this.name = name;
		this.fallString = fallPar;
		this.springString = springPar;
		this.summerString = summerPar;
		this.fallIn = fallPar.split("");
		this.springIn = springPar.split("");
		this.summerIn = summerPar.split("");
		
		// Listing of Days course is offered on by season. Null list if no offers.
		this.fallDaysList= new Day[fallPar.length()];
		this.springDaysList = new Day[springPar.length()];
		this.summerDaysList = new Day[summerPar.length()];
		
		createDaysArrays(fallPar, springPar, summerPar);

	}
	
	/**
	 * Creates the entire domain of values for this course.
	 * Iterates through every Semester in given list, 
	 * adding an entry to the domain for each day the course
	 * is available in that Semester.
	 * 
	 * Format of each entry is an integer composed of courseID + enum Day value.
	 * @param m List of all semesters.
	 */
	public void createDomain(SemesterList m) {
		this.domain = new ArrayList<Integer>();
		
		// for each semester {add courses offered during}
		for(int i = 0; i < m.numberOfSemesters(); i++) {
			String domainEntry = "";
			Semester s = m.getSemesterByIndex(i);
			Season season = s.getSeason();

			// Add domain entries for the correct Season.
			switch(season) {
			case FALL:
				if(this.fallDaysList[0] != null) {
					for(Day day :this.fallDaysList) {
						domainEntry = ""+s.getID() + day.ordinal();
						this.domain.add(Integer.parseInt(domainEntry));
					}
				}
					break;
			case SPRING:
				if(this.springDaysList[0] != null) {
					for(Day day : this.springDaysList) {
						domainEntry = ""+s.getID() + day.ordinal();
						this.domain.add(Integer.parseInt(domainEntry));
					}
				}
				break;
			case SUMMER:
				if(this.summerDaysList[0] != null) {
					for(Day day : this.summerDaysList) {
						domainEntry = ""+s.getID() + day.ordinal();
						this.domain.add(Integer.parseInt(domainEntry));
					}
				}
				break;
			default:
			}
		}
	}

	/**
	 * Assistant method to constructor. Creates the Day[] for each season for this course.
	 * @param fallPar
	 * @param springPar
	 * @param summerPar
	 */
	private void createDaysArrays(String fallPar, String springPar, String summerPar) {
		// for non dash values add days to Day array
		if(!this.fallIn[0].equals("-")) {
			for(int i = 0; i < this.fallIn.length; i ++) {
				this.fallDaysList[i] = Day.valueOf(this.fallIn[i]);
			}
		}
		if(!this.springIn[0].equals("-")) {
			for(int i = 0; i < this.springIn.length; i++) {
				this.springDaysList[i] = Day.valueOf(this.springIn[i]);
			}
		}
		if(!this.summerIn[0].equals("-")) {
			for(int i = 0; i < this.summerIn.length; i++) {
				this.summerDaysList[i] = Day.valueOf(this.summerIn[i]);
			}
		}
	}

	public int getDomainSize() {
		return this.domain.size();
	}
	/**
	 * For parameter season, returns a Day Array of days for that season.
	 * @param season
	 * @return Day[]
	 */
	public Day[] getSeasonDomain(Season season) {
		switch(season) {
		case FALL:
			return this.fallDaysList;
		case SPRING:
			return this.springDaysList;
		case SUMMER:
			return this.summerDaysList;
		}	
		return null;
	}
	
	/**
	 * For an input number, returns the domain value at that index.
	 * @param Index
	 * @return
	 */
	public int getDomainEntry(int index) {
		return this.domain.get(index);
	}
	
	public String toStringInputStyled() {
			String s = "";
			s = s.concat(this.name+"\t"
					+ this.fallString   +"\t"
					+ this.springString.toString() +"\t"
					+ this.summerString.toString() +"\t"
					);
			return s;
		}

	public String toStringDomainByLine() {
		String s = "";
		for(int i = 0; i < this.domain.size(); i++)
			s = s.concat(String.valueOf(this.domain.get(i))+"\n");
		s.trim();
		return s;
	}
	public String toStringDomainBySpaces() {
		String s = "";
		for(int i = 0; i < this.domain.size(); i++)
			s = s.concat(String.valueOf(this.domain.get(i))+"  ");
		s.trim();
		return s;
	}
	public String getName() {
		return this.name;
	}
	
}
