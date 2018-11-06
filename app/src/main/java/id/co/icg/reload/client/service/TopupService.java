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

public interface TopupService {

    @GET("topup/banks")
    Call<Response> getBankAccounts();

    @POST("topup/add")
    Call<Response> addBalance(@Body Map<String, String> map);


    @GET("topup/getUnapprovedDeposit")
    Call<Response> getUnapprovedDeposit();

    @Multipart
    @POST("topup/upload/{id}")
    Call<Response> uploadTransferProof(@Path("id") Long id, @Part MultipartBody.Part image);
}
