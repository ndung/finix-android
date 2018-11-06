package id.go.lipi.informatika.woodid.xylarium.client;

import android.content.Context;

import id.go.lipi.informatika.woodid.xylarium.client.service.AssessmentService;
import id.go.lipi.informatika.woodid.xylarium.client.service.AuthService;
import id.go.lipi.informatika.woodid.xylarium.client.service.InboxService;

public class ApiUtils {

    public static AuthService AuthService(Context context){
        return RetrofitClient.getClient(context).create(AuthService.class);
    }

    public static InboxService InboxService(Context context){
        return RetrofitClient.getClient(context).create(InboxService.class);
    }

    public static AssessmentService AssessmentService(Context context){
        return RetrofitClient.getClient(context).create(AssessmentService.class);
    }
}
