package csp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Specialized list for semesters.
 * Used to reference all data used.
 */
public class SemesterList{
	private List<Semester> semesters;
	
	private ArrayList<String> map;
	private Map<Integer, String> mapIdToName;
	private Map<String, Integer> mapNameToID;
	private Map<Integer, Semester> mapIdToSemester;
	private Map<String, Semester> mapNameToSemester;
	private String valueSeparator = "\\s+"; 

	
	SemesterList(){
		this.mapIdToName = new HashMap<>();
		this.mapNameToID = new HashMap<>();
		this.mapIdToSemester = new HashMap<>();
		this.mapNameToSemester = new HashMap<>();
		this.semesters = new ArrayList<Semester>();
	}
	
	
	/**
	 * Replaces previous list with the imported list.
	 * Expected format of input file is:
	 * SeasonSPACEyearTABnumber.
	 * Will import season & year as one string, number as one Integer.
	 * @param f
	 */
	public void importSemesterMap(File f) {
		Scanner scanner;
		String line = "";
		this.semesters = new ArrayList<Semester>();
		try {
			scanner = new Scanner(f);
			int count = 0;
			while(scanner.hasNextLine()){
				line = ("\n"+scanner.nextLine());
				line = line.trim();
				String[] r = line.split(this.valueSeparator);
				String name = r[0] + " " + r[1];
				// instantiate new semester from the input line
				Semester s = new Semester(Season.valueOf(r[0].toUpperCase()), 
						Integer.parseInt(r[1]), Integer.parseInt(r[2]), name);
				// add new semester to list
				this.semesters.add(s);
				this.mapIdToName.put(s.getID(), s.getName());
				this.mapNameToID.put(s.getName(), s.getID());
				this.mapIdToSemester.put(s.getID(), s);
				this.mapNameToSemester.put(s.getName(), s);
				s.setIndex(count);
				count++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
		}
	}
	public Semester getSemesterByIndex(int index) {
		return this.semesters.get(index);
	}
	public String getSeason(int semesterID) {
		String s = this.mapIdToName.get(semesterID);
		s = s.split(" ")[0];
		
		return s;
	}
	/**
	 * For the parameter semesterID, returns the name
	 * @param id
	 * @return
	 */
	public String getSemesterName(int id) {
		return this.mapIdToName.get(id);
	}
	/**
	 * For the parameter semesterID, returns the semester
	 * @param id
	 * @return
	 */
	public Semester getSemesterByID(int id) {
		return this.mapIdToSemester.get(id);
	}
	public int getSemesterID(String name) {
		return this.mapNameToID.get(name);
	}
	public int getSemesterID(int index) {
		return this.semesters.get(index).getID();
	}
	/**
	 * Returns the semesterID for the parameter entry number.
	 * @param index
	 * @return
	 */
	public int getSemesterIDbyIndex(int index) {
		return this.getSemesterID(this.map.get(index));
	}
	/** 
	 * Returns the number of semesters (size of semester list)
	 * @return
	 */
	public int numberOfSemesters() {
		return this.semesters.size();
	}
	
}
