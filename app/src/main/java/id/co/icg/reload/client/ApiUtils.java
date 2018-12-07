package id.co.icg.reload.client;

import android.content.Context;

import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.client.service.ChatService;
import id.co.icg.reload.client.service.HistoryService;
import id.co.icg.reload.client.service.RedeemService;
import id.co.icg.reload.client.service.TopupService;
import id.co.icg.reload.client.service.TransactionService;

public class ApiUtils {

    public static AuthService AuthService(Context context){
        return RetrofitClient.getClient(context).create(AuthService.class);
    }

    public static TopupService TopupService(Context context){
        return RetrofitClient.getClient(context).create(TopupService.class);
    }

    public static TransactionService TransactionService(Context context){
        return RetrofitClient.getClient(context).create(TransactionService.class);
    }

    public static HistoryService HistoryService(Context context){
        return RetrofitClient.getClient(context).create(HistoryService.class);
    }

    public static ChatService ChatService(Context context){
        return RetrofitClient.getClient(context).create(ChatService.class);
    }

    public static RedeemService RedeemService(Context context){
        return RetrofitClient.getClient(context).create(RedeemService.class);
    }
}
