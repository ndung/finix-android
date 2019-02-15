package id.co.icg.reload.client.service;

import java.util.Map;

import id.co.icg.reload.client.Response;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface AuthService {

    @POST("user/sign-in")
    Call<Response> signIn(@Body Map<String, String> map);

    @POST("user/create-newpwd")
    Call<Response> createNewPassword(@Body Map<String, String> map);

    @POST("user/change-pwd")
    Call<Response> changePassword(@Body Map<String, String> map);

    @GET("user/get-info")
    Call<Response> getInfo();

    @POST("user/get-user/{id}")
    Call<Response> getUser(@Path("id") String id);

    @GET("user/get-downlines")
    Call<Response> getDownlines();

    @POST("user/add-downline/{downline}")
    Call<Response> addDownline(@Path("downline") String downline);

    @POST("user/remove-downline/{downline}")
    Call<Response> removeDownline(@Path("downline") String downline);

    @POST("user/use-fingerprint/{bool}")
    Call<Response> useFingerprint(@Path("bool") String bool);

    @POST("user/update-facebook")
    Call<Response> updateFacebookInfo(@Body Map<String,String> map);

    @POST("user/gen-qr")
    Call<Response> genQr();

    @Multipart
    @POST("user/update-account")
    Call<Response> updateAccount(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part pp, @Part MultipartBody.Part id);

}
