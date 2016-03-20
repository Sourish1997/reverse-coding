package data;

public class Question  {
	public String name, status, marked, marksObtained, markedBy;
	
	public Question(String name, String status, String marked, String marksObtained, String markedBy) {
		this.name = name;
		this.status = status;
		this.marked = marked;
		this.marksObtained = marksObtained;
		this.markedBy = markedBy;
	}
}
