package data;

/**
 * <h1>User.class</h1>
 * 
 * <p>
 * Stores the details of a particular user.
 * 
 * @author Sourish Banerjee
 */
public class User {
	/**User name of user*/
	public String id;
	/**Password of user*/
	public String password;
	
	/**
	 * Initializes the object with info passed to it.
	 * 
	 * @param id User name of the user.
	 * @param password Password of the user.
	 */
	public User(String id, String password) {
		this.id = id;
		this.password = password;
	}
}
