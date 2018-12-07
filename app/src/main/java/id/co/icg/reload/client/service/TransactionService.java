package id.co.icg.reload.client.service;

import java.util.Map;

import id.co.icg.reload.client.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TransactionService {

    @GET("transaction/get-all-products")
    Call<Response> getAllProducts();

    @GET("transaction/get-billers-by-category/{id}")
    Call<Response> getBillersByCategory(@Path("id") int id);

    @GET("transaction/get-products-by-category/{id}")
    Call<Response> getProductsByCategory(@Path("id") int id);

    @GET("transaction/get-products-by-biller/{id}")
    Call<Response> getProductsByBiller(@Path("id") String id);

    @POST("transaction/transfer-balance")
    Call<Response> transferBalance(@Body Map<String, String> map);

    @POST("transaction/recharge-mobile")
    Call<Response> rechargeMobile(@Body Map<String, String> map);

    @POST("transaction/recharge-other")
    Call<Response> rechargeOther(@Body Map<String, String> map);

    @POST("transaction/inquiry-phone-bill")
    Call<Response> inquiryPhoneBill(@Body Map<String, String> map);

    @POST("transaction/payment-phone-bill")
    Call<Response> paymentPhoneBill(@Body Map<String, String> map);

    @POST("transaction/inquiry-electric-recharge")
    Call<Response> inquiryElectricRecharge(@Body Map<String, String> map);

    @POST("transaction/payment-electric-recharge")
    Call<Response> paymentElectricRecharge(@Body Map<String, String> map);

    @POST("transaction/inquiry-electric-bill")
    Call<Response> inquiryElectricBill(@Body Map<String, String> map);

    @POST("transaction/payment-electric-bill")
    Call<Response> paymentElectricBill(@Body Map<String, String> map);

    @POST("transaction/inquiry-water-bill")
    Call<Response> inquiryWaterBill(@Body Map<String, String> map);

    @POST("transaction/payment-water-bill")
    Call<Response> paymentWaterBill(@Body Map<String, String> map);

    @POST("transaction/inquiry-paytv-bill")
    Call<Response> inquiryPaytvBill(@Body Map<String, String> map);

    @POST("transaction/payment-paytv-bill")
    Call<Response> paymentPaytvBill(@Body Map<String, String> map);

    @POST("transaction/inquiry-insurance-bill")
    Call<Response> inquiryInsuranceBill(@Body Map<String, String> map);

    @POST("transaction/payment-insurance-bill")
    Call<Response> paymentInsuranceBill(@Body Map<String, String> map);
}
