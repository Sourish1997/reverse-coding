package network;

import data.User;
import data.QuestionList;

import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Multipart;

/**
 * <h1>Service.class</h1>
 * 
 * <p>
 * Contains necessary requests that need to be 
 * sent to the server.
 * 
 * @author Sourish Banerjee
 */
public interface Service {
	/**Path of server*/
	public String path = "path"; // Server path to be entered here.
	
	/**
	 * Posts the details of the user. Receives 
	 * info pertaining to all the questions for 
	 * the user in return.
	 * 
	 * @param obj Details of the user.
	 * @return Info pertaining to all the 
	 * questions for the user.
	 */
	@POST(path)
	public Call<QuestionList> sendInfoRequest(@Body User obj);
	
	/**
	 * Posts the solution uploaded by the user 
	 * for a particular question.
	 * 
	 * @param file Solution uploaded by the user.
	 * @param user Info pertaining to the user.
	 * @param question Info pertaining to the 
	 * question whose solution is uploaded.
	 * @return Success or failure value of post 
	 * operation.
	 */
	@Multipart
    @POST(path)
    Call<String> uploadFile(
            @Part("solution\"; filename=\"solution\" ") RequestBody file,
            @Part("user") RequestBody user,
            @Part("question") RequestBody question);
}
