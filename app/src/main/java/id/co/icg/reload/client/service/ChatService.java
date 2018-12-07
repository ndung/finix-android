package id.co.icg.reload.client.service;

import java.util.Map;

import id.co.icg.reload.client.Response;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ChatService {

    @GET("chat/fcmRead/{id}")
    Call<Response> getChatMessages(@Path("id") String id);

    @POST("chat/fcmSend")
    Call<Response> sendChatMessage(@Body Map<String,String> map);

    @Multipart
    @POST("chat/fcmUpload")
    Call<Response> uploadMessageAttachment(@Body Map<String,String> map, @Part MultipartBody.Part attachment);
}
