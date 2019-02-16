package id.co.icg.reload.client.service;

import id.co.icg.reload.client.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RedeemService {

    @GET("redeem/get-gifts")
    Call<Response> getGifts();

    @POST("redeem/redeem-gift/{id}")
    Call<Response> redeemGift(@Path("id") Long id);

    @POST("redeem/get-logs")
    Call<Response> getLogs();
}
