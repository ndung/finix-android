package id.co.icg.reload;

import android.app.Application;
import android.content.pm.PackageInfo;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.appevents.AppEventsLogger;

import id.co.icg.reload.util.Preferences;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Retrofit;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        AccountKit.initialize(getApplicationContext());

        EasyImage.configuration(this)
                .setImagesFolderName("id.finix");
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public String getAppVersion() {
        int verCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionCode;
        } catch (Exception ex) {
        }
        return String.valueOf(verCode);
    }



    private Preferences preferencesManager;

    public Preferences getPreferencesManager() {
        if (preferencesManager == null) {
            preferencesManager = new Preferences(this);
        }

        return preferencesManager;
    }
}
