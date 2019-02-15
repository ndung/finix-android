package id.co.icg.reload.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;

import id.co.icg.reload.R;
import id.co.icg.reload.activity.AccountInformationActivity;
import id.co.icg.reload.activity.ChangePasswordActivity;
import id.co.icg.reload.activity.ContactUsActivity;
import id.co.icg.reload.activity.LoginActivity;
import id.co.icg.reload.activity.PrinterActivity;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.model.QrCode;
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
    private RelativeLayout layoutContactUs;
    private RelativeLayout layoutRefer;
    private RelativeLayout layoutLogout;

    private Boolean shouldUseFingerprint = false;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;

    private AuthService authService;

    private TextView tvPhoneNumber;
    private TextView tvDefaultPrinter;
    private ImageView ivVerified;
    private ImageView ivQr;
    private ImageView ivNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        cbFingerprint = view.findViewById(R.id.cb_fingerprint);

        authService = ApiUtils.AuthService(getActivity());

        ivNotification = view.findViewById(R.id.iv_notification);
        ivNotification.setOnClickListener(v -> startChatActivity());

        mFingerprintManager = FingerprintManagerCompat.from(getActivity());

        llChangePassword = view.findViewById(R.id.ll_change_password);
        llChangePassword.setOnClickListener(v -> startChangePassword());

        llVerifyAccount = view.findViewById(R.id.ll_verify_account);
        llVerifyAccount.setOnClickListener(v -> startVerifyAccount());

        llSettingPrinter = view.findViewById(R.id.ll_setting_printer);
        llSettingPrinter.setOnClickListener(v -> startPrinterSetting());

        layoutRefer = view.findViewById(R.id.layout_refer);
        layoutRefer.setOnClickListener(v -> share());

        layoutContactUs = view.findViewById(R.id.layout_contact_us);
        layoutContactUs.setOnClickListener(v -> startContactUs());

        layoutLogout = view.findViewById(R.id.layout_logout);
        layoutLogout.setOnClickListener(v -> logout());

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
        ivQr = view.findViewById(R.id.iv_qr);
        genQr();
        return view;
    }

    private void startChangePassword(){
        startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
    }

    private void startVerifyAccount(){
        startActivity(new Intent(getActivity(), AccountInformationActivity.class));
    }

    private void startPrinterSetting(){
        startActivity(new Intent(getActivity(), PrinterActivity.class));
    }

    private void startContactUs(){
        startActivity(new Intent(getActivity(), ContactUsActivity.class));
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

    private void share() {
        //String link = Constants.REFEREAL_URL + referal;
        String textShared = "Ayo Daftar iReload Sekarang! " +
                "\nMasukkan Kode Referral: " + "aaa" + " Pada Saat Daftar Atau ";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textShared);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void genQr(){
        authService.genQr().enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        QrCode qrCode = gson.fromJson(jsonObject, QrCode.class);
                        if (qrCode!=null){
                            QRCodeWriter writer = new QRCodeWriter();
                            BitMatrix bitMatrix = writer.encode(qrCode.getCode(), BarcodeFormat.QR_CODE, 512, 512);
                            int width = bitMatrix.getWidth();
                            int height = bitMatrix.getHeight();
                            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                }
                            }
                            ivQr.setImageBitmap(bmp);
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
                showMessage(t.getMessage());
            }
        });
    }



}
