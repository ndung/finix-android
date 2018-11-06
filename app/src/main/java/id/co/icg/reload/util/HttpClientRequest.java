package id.co.icg.reload.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import id.co.icg.reload.App;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClientRequest {
    static SimpleDateFormat sdf = new SimpleDateFormat();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Map<String,String> getHeaders(String requestType){
        Map<String,String> map = new HashMap<String,String>();
        TimeZone tz = TimeZone.getDefault();
        map.put("app_version", Base64.encodeToString(App.getInstance().getAppVersion().getBytes(), Base64.NO_WRAP));
        map.put("tz_id", Base64.encodeToString(tz.getID().getBytes(), Base64.NO_WRAP));
        map.put("tz_name", Base64.encodeToString(tz.getDisplayName().getBytes(), Base64.NO_WRAP));
        map.put("tz_date", Base64.encodeToString(sdf.format(new Date()).getBytes(),  Base64.NO_WRAP));
        map.put("req_type", Base64.encodeToString(requestType.getBytes(),  Base64.NO_WRAP));
        return map;
    }

    public static String execute(String url, String requestType) {
        String str = null;

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .build();

            Map<String,String> headers = getHeaders(requestType);
            Request.Builder requestBuilder = new Request.Builder();
            for (String key : headers.keySet()){
                requestBuilder.addHeader(key, headers.get(key));
            }
            Request request = requestBuilder.url(url).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }
        } catch (Exception e) {
            Log.e("", "err", e);
        }
        return str;
    }
}
