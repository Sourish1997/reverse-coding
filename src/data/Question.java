package data;

/**
 *<h1>Question.class</h1>
 *
 *<p> 
 * Stores the details of a particular question.
 * 
 * @author Sourish Banerjee
 */
public class Question  {
	/**Info pertaining to the question.*/
	public String name, status, marked, marksObtained, markedBy;
	
	/**
	 * To initialize the question object with data 
	 * passed to it.
	 * 
	 * @param name Title of the question.
	 * @param status Attempted or not attempted.
	 * @param marked Whether the question has been marked or not.
	 * @param marksObtained Marks awarded for the question.
	 * @param markedBy Name of evaluator.
	 */
	public Question(String name, String status, String marked, String marksObtained, String markedBy) {
		this.name = name;
		this.status = status;
		this.marked = marked;
		this.marksObtained = marksObtained;
		this.markedBy = markedBy;
	}
}
