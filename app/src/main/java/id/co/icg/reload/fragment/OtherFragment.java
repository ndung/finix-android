package id.co.icg.reload.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;

import id.co.icg.reload.R;
import id.co.icg.reload.activity.AccountInformationActivity;
import id.co.icg.reload.activity.ChangePasswordActivity;
import id.co.icg.reload.activity.LoginActivity;
import id.co.icg.reload.activity.PrinterActivity;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.model.Product;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.Global;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.KEYGUARD_SERVICE;

public class OtherFragment extends BaseFragment {

    private CheckBox cbFingerprint;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private LinearLayout llVerifyAccount;
    private LinearLayout llChangePassword;
    private LinearLayout llSettingPrinter;
    private LinearLayout tvLogout;

    private Boolean shouldUseFingerprint = false;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;

    private AuthService authService;

    private TextView tvPhoneNumber;
    private TextView tvDefaultPrinter;
    private ImageView ivVerified;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        cbFingerprint = view.findViewById(R.id.cb_fingerprint);

        authService = ApiUtils.AuthService(getActivity());

        mFingerprintManager = FingerprintManagerCompat.from(getActivity());

        llChangePassword = view.findViewById(R.id.ll_change_password);
        llChangePassword.setOnClickListener(v -> startChangePassword());

        llVerifyAccount = view.findViewById(R.id.ll_verify_account);
        llVerifyAccount.setOnClickListener(v -> startVerifyAccount());

        llSettingPrinter = view.findViewById(R.id.ll_setting_printer);
        llSettingPrinter.setOnClickListener(v -> startPrinterSetting());

        tvLogout = view.findViewById(R.id.layout_logout);
        tvLogout.setOnClickListener(v -> logout());

        tvPhoneNumber = view.findViewById(R.id.tv_phone_number);
        tvDefaultPrinter = view.findViewById(R.id.tv_default_printer);
        ivVerified = view.findViewById(R.id.iv_verified);

        Reseller rs = Preferences.getUser(getActivity());

        shouldUseFingerprint = Boolean.parseBoolean(rs.getUseFingerprint());

        cbFingerprint.setChecked(shouldUseFingerprint);

        cbFingerprint.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != shouldUseFingerprint && isFingerprintAvailable()){
                updateFingerprint(String.valueOf(isChecked));
            }else{
                cbFingerprint.setChecked(!cbFingerprint.isChecked());
            }
        });

        callbackManager = CallbackManager.Factory.create();

        loginButton = view.findViewById(R.id.fb_login);

        if (ivVerified.equals("Y")){
            loginButton.setEnabled(false);
        }else{
            loginButton.setEnabled(true);
        }

        loginButton.setReadPermissions("email", "public_profile", "user_friends", "user_birthday");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> {

                                    Map<String, String> map = new HashMap<>();
                                    map.put("info", object.toString());
                                    updateFacebookInfo(map);
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,verified,link,friends");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        tvPhoneNumber.setText(rs.getId());
        tvDefaultPrinter.setText(Preferences.getDefaultPrinter(getActivity()));

        if (rs.getVerified().equalsIgnoreCase("Y")){
            ivVerified.setVisibility(View.VISIBLE);
        }else{
            ivVerified.setVisibility(View.GONE);
        }
        return view;
    }

    private void startChangePassword(){
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void startVerifyAccount(){
        Intent intent = new Intent(getActivity(), AccountInformationActivity.class);
        startActivity(intent);
    }

    private void startPrinterSetting(){
        Intent intent = new Intent(getActivity(), PrinterActivity.class);
        startActivity(intent);
    }

    private void logout(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }


    private void updateFacebookInfo(Map<String,String> map){
        showPleaseWaitDialog();

        authService.updateFacebookInfo(map).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();
                        Reseller user = gson.fromJson(jsonObject, Reseller.class);
                        Preferences.setUser(getActivity(), user);
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    protected FingerprintManagerCompat mFingerprintManager;

    private boolean isFingerprintAvailable() {

        final boolean isFingerprintPermissionGranted = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT)
                == PackageManager.PERMISSION_GRANTED;

        KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);

        if (!isFingerprintPermissionGranted) {
            showMessage("Mohon berikan izin (permission) untuk mengakses fingerprint di device handphone anda");
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            showMessage("Ver. OS Android di device handphone anda tidak mendukung autentikasi melalui fingerprint");
        } else if (!mFingerprintManager.isHardwareDetected()) {
            showMessage("Device handphone anda tidak mendukung autentikasi melalui fingerprint");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !keyguardManager.isKeyguardSecure()) {
            showMessage("Secure lock screen hasn't set up.\n" + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint");
        } else if (!mFingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            showMessage("Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint");
        } else {
            try {
                mKeyStore = KeyStore.getInstance(Global.FINGERPRINT_KEYSTORE);
                mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Global.FINGERPRINT_KEYSTORE);
                return true;
            } catch (Exception e) {
                Log.e("fingerprint", "error", e);
            }
        }
        return false;
    }

    private void updateFingerprint(String bool){
        showPleaseWaitDialog();

        authService.useFingerprint(bool).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        Reseller user = gson.fromJson(jsonObject, Reseller.class);
                        if (user!=null){
                            shouldUseFingerprint = Boolean.parseBoolean(user.getUseFingerprint());
                            Preferences.setUser(getActivity(), user);
                            if (shouldUseFingerprint && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                createKey();
                            }
                        }
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @TargetApi(Build.VERSION_CODES.M)
    protected void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(Global.FINGERPRINT_KEY,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //    builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            //}
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (Exception e) {
            Log.e("fingerprint", "error", e);
        }
    }

}
