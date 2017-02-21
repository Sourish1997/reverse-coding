package data;

/**
 * <h1>QuestionList.class</h1>
 * 
 * <p>
 * Stores all the questions for a particular user.
 * 
 * @author Sourish Banerjee
 */
public class QuestionList {
	/**Stores truth value of validity of the user.*/
	public boolean status;
	/**Stores the name of the user.*/
	public String name;
	/**
	 * Stores info pertaining to all questions for 
	 * the particular user.
	 */
	public Question questions[];
	
	/**
	 * Initializes the object with info for all the 
	 * questions passed to it.
	 * 
	 * @param status Whether user is valid or not. 
	 * In case of invalid user, null should be passed 
	 * in place of the info for all the questions.
	 * @param name Name of user.
	 * @param questions Info for all the questions 
	 * for the particular user.
	 */
	public QuestionList(boolean status, String name, Question questions[]) {
		this.status = status;
		this.name = name;
		this.questions = new Question[questions.length];
		for(int a = 0; a < questions.length; a++)
			this.questions[a] = questions[a];
	}
}
