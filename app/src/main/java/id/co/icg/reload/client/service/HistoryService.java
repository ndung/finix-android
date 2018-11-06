package id.co.icg.reload.client.service;

import id.co.icg.reload.client.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HistoryService  {

    @GET("history/get-account-logs/{date}")
    Call<Response> getAccountLogs(@Path("date") String date);
}
