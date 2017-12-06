package csp;

/**
 * Represents one semester.
 * Used only for generalized semester attributes.
 */
public class Semester {
	private int ID;
	private int index;
	private int year;
	private String name;
	private Season season;
	
	Semester(Season season,int year,int ID, String name){
		this.ID = ID;
		this.year = year;
		this.season = season;
		this.name = name;
	}
	Semester(String season, int year , int ID, String name){
		this.ID = ID;
		this.year = year;
		this.season = Season.valueOf(season);
		this.name = name;
	}
	
	public int getID() {
		return this.ID;
	}
	public String getName() {
		return this.name;
	}
	public Season getSeason() {
		return this.season;
	}
	public void setIndex(int i) {
		this.index = i;
	}
	public int getIndex() {
		return this.index;
	}
	public int getYear() {
		return this.year;
	}
}
