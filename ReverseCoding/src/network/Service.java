package network;

import data.User;
import data.QuestionList;

import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Multipart;

public interface Service {
	public String path="path";
	
	@POST(path)
	public Call<QuestionList> sendInfoRequest(@Body User obj);
	
	@Multipart
    @POST(path)
    Call<String> uploadFile(
            @Part("solution\"; filename=\"solution\" ") RequestBody file,
            @Part("user") RequestBody user,
            @Part("question") RequestBody question);
}
