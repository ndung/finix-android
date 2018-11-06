package id.co.icg.reload.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import id.co.icg.reload.App;
import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.fragment.FingerprintAuthenticationDialogFragment;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.ChipperUtils;
import id.co.icg.reload.util.Global;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends BaseActivity {

    private EditText txtID, txtPwd;
    private Switch mySwitch;
    private ImageView ivFingerprint;
    private AuthService authService;
    private Button loginButton;
    private TextInputLayout tilUsername, tilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authService = ApiUtils.AuthService(this);

        setContentView(R.layout.activity_login);

        txtID = findViewById(R.id.et_id);
        txtPwd = findViewById(R.id.et_pwd);
        tilUsername = findViewById(R.id.input_layout_id);
        tilPassword = findViewById(R.id.input_layout_pin);
        loginButton = findViewById(R.id.btn_login);

        mySwitch = findViewById(R.id.mySwitch);

        if (!Global.LOGIN_USER.equals("")) {
            txtID.setText(Global.LOGIN_USER);
        }
        if (App.getInstance().getPreferencesManager().isPasswordRemembered()) {
            txtPwd.setText(App.getInstance().getPreferencesManager().getPassword());
        }

        //FirebaseMessaging.getInstance().subscribeToTopic("global");

        //set the switch to ON
        mySwitch.setChecked(App.getInstance().getPreferencesManager().isPasswordRemembered());

        loginButton.setOnClickListener(v -> login(txtID.getText().toString(), txtPwd.getText().toString()));

        //App.getUiImageLoader().clearCache();

        /**ivFingerprint = findViewById(R.id.iv_fingerprint);

         ivFingerprint.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        boolean isFingerprintAvailable = isFingerprintAvailable();
        if (isFingerprintAvailable) {
        if (App.getInstance().getPreferencesManager().getUserName().equals("")) {
        showToast(getString(R.string.fingerprint_activation_message));
        }else {
        Map<String, String> map = new HashMap<>();
        map.put("type", "checkFingerprint");
        map.put("user", App.getInstance().getPreferencesManager().getUserName());
        connectServerApi("checkFingerprint", map);
        }
        }
        }
        });*/
    }

    public void onSuccessfullApiConnect(String requestHeader, String result) {
        if (requestHeader.equals("checkFingerprint")) {
            boolean b = Boolean.parseBoolean(result);
            Global.FINGERPRINT_ACCESS = b;
            if (b == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

                    //createKey();

                    boolean bool = initCipher(cipher);
                    if (bool) {
                        // Show the fingerprint dialog. The user has the option to use the fingerprint with
                        // crypto, or you can fall back to using a server-side verified password.
                        FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
                        fragment.setCryptoObject(new FingerprintManager.CryptoObject(cipher));
                        fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                    } else {
                        // This happens if the lock screen has been disabled or or a fingerprint got
                        // enrolled. Thus show the dialog to authenticate with their password first
                        // and ask the user if they want to authenticate with fingerprints in the
                        // future
                        Map<String, String> map = new HashMap<>();
                        map.put("type", "updateFingerprint");
                        map.put("bool", String.valueOf(false));
                        map.put("user", App.getInstance().getPreferencesManager().getUserName());
                        Global.FINGERPRINT_ACCESS = false;
                        //connectServerApi("updateFingerprint", map);
                    }
                } catch (Exception e) {
                    Log.e("fingerprint", "error", e);
                }
            } else {
                showMessage(getString(R.string.fingerprint_activation_message));
            }
        } else if (requestHeader.equals("updateFingerprint")) {
            showMessage(getString(R.string.fingerprint_update_message));
        }
    }

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String TAG = LoginActivity.class.toString();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(Global.FINGERPRINT_KEY, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        return false;
    }


    private static final int FRAMEWORK_REQUEST_CODE = 1;

    private int nextPermissionsRequestCode = 4000;
    private final Map<Integer, OnCompleteListener> permissionsListeners = new HashMap<>();

    private interface OnCompleteListener {
        void onComplete();
    }

    public void onLoginEmail(final View view) {
        onLogin(LoginType.EMAIL);
    }

    public void onLoginPhone(final View view) {
        onLogin(LoginType.PHONE);
    }

    public void onLoginWithFingerprint() {

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != FRAMEWORK_REQUEST_CODE) {
            return;
        }

        final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
        if (loginResult == null || loginResult.wasCancelled()) {
            //Snackbar.make(txtID, "Login Cancelled", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (loginResult.getError() != null) {
            showMessage(loginResult.getError().getErrorType().getMessage());
        } else {
            final AccessToken accessToken = loginResult.getAccessToken();
            if (accessToken != null) {
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        final PhoneNumber number = account.getPhoneNumber();
                        if (number != null) {
                            String phoneNumber = number.toString().replaceFirst("\\+62", "0");
                            gotoCreatePassword(phoneNumber);
                        }
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        Log.e("error", "AccountKitError=" + error);
                        showMessage(error.getErrorType().getMessage());
                    }
                });
            } else {
                showMessage("Unknown response type");
            }
        }
    }

    private void gotoCreatePassword(String phoneNumber) {
        Intent intent = new Intent(this, CreatePasswordActivity.class);
        intent.putExtra("phone_number", phoneNumber);
        startActivity(intent);
    }

    private void onLogin(final LoginType loginType) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType,
                AccountKitActivity.ResponseType.TOKEN);
        configurationBuilder.setTheme(R.style.AppLoginTheme_iReload);
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration);
        OnCompleteListener completeListener = () -> startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
        switch (loginType) {
            case EMAIL:
                final OnCompleteListener getAccountsCompleteListener = completeListener;
                completeListener = () -> requestPermissions(
                        Manifest.permission.GET_ACCOUNTS,
                        R.string.permissions_get_accounts_title,
                        R.string.permissions_get_accounts_message,
                        getAccountsCompleteListener);
                break;
            case PHONE:
                if (configuration.isReceiveSMSEnabled()) {
                    final OnCompleteListener receiveSMSCompleteListener = completeListener;
                    completeListener = () -> requestPermissions(
                            Manifest.permission.RECEIVE_SMS,
                            R.string.permissions_receive_sms_title,
                            R.string.permissions_receive_sms_message,
                            receiveSMSCompleteListener);
                }
                if (configuration.isReadPhoneStateEnabled()) {
                    final OnCompleteListener readPhoneStateCompleteListener = completeListener;
                    completeListener = () -> requestPermissions(
                            Manifest.permission.READ_PHONE_STATE,
                            R.string.permissions_read_phone_state_title,
                            R.string.permissions_read_phone_state_message,
                            readPhoneStateCompleteListener);
                }
                break;
        }
        completeListener.onComplete();
    }

    private void requestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final OnCompleteListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        checkRequestPermissions(
                permission,
                rationaleTitleResourceId,
                rationaleMessageResourceId,
                listener);
    }

    @TargetApi(23)
    private void checkRequestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final OnCompleteListener listener) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        final int requestCode = nextPermissionsRequestCode++;
        permissionsListeners.put(requestCode, listener);

        if (shouldShowRequestPermissionRationale(permission)) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(rationaleTitleResourceId)
                    .setMessage(rationaleMessageResourceId)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> requestPermissions(new String[]{permission}, requestCode))
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // ignore and clean up the listener
                        permissionsListeners.remove(requestCode);
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    @TargetApi(23)
    @SuppressWarnings("unused")
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String permissions[],
                                           final @NonNull int[] grantResults) {
        final OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
        if (permissionsListener != null
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsListener.onComplete();
        }
    }

    private void login(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Mohon isi ID/username");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Mohon isi pin/password");
            return;
        }
        tilUsername.setError("");
        tilPassword.setError("");
        showPleaseWaitDialog();
        final String publicKey = ChipperUtils.getPublicKey(username, password);
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("publicKey", publicKey);
        authService.signIn(map).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                    if (response.isSuccessful()) {
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        Reseller user = gson.fromJson(jsonObject, Reseller.class);
                        if (user != null) {
                            String token = response.headers().get("Token");
                            Preferences.setUser(getApplicationContext(), user);
                            Preferences.setPublicKey(getApplicationContext(), publicKey);
                            Preferences.setToken(getApplicationContext(), token);
                            Preferences.setLoginFlag(getApplicationContext(), true);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            FirebaseMessaging.getInstance().subscribeToTopic(user.getId());
                            FirebaseMessaging.getInstance().subscribeToTopic("global");
                            finish();
                        } else {
                            showMessage(body.getMessage());
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
                Log.e(TAG, "error", t);
                showMessage(Static.SOMETHING_WRONG);

            }
        });
    }
}