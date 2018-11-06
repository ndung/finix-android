package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.Global;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class CreatePasswordActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvPhoneNumber;
    private EditText etNewPassword;
    private EditText etConfirmedNewPassword;
    private TextView tvSave;
    private TextInputLayout tilNewPassword;
    private EditText etReferralCode;
    private TextInputLayout tilReferral;
    private String phoneNumber;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authService = ApiUtils.AuthService(this);

        phoneNumber = getIntent().getStringExtra("phone_number");

        setContentView(R.layout.activity_create_password);

        tilNewPassword = findViewById(R.id.til_new_password);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(R.string.create_new_password);

        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvPhoneNumber.setText(phoneNumber);

        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmedNewPassword = findViewById(R.id.et_confirmed_new_password);
        etReferralCode = findViewById(R.id.et_referral);
        tilReferral = findViewById(R.id.til_referral);

        tilReferral.setHint(getString(R.string.referral_hint));
        tilNewPassword.setHint(getString(R.string.password_hint));
        tvSave = findViewById(R.id.tv_save);

        tilNewPassword.setHint(getString(R.string.password_hint));

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPassword(etNewPassword.getText().toString(), etConfirmedNewPassword.getText().toString());
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void createPassword(String newPassword, String confirmedNewPassword){
        if (TextUtils.isEmpty(newPassword)){
            tilNewPassword.setError("Isikan password baru");
            return;
        }
        if (TextUtils.isEmpty(confirmedNewPassword)){
            tilNewPassword.setError("Ketik ulang password baru");
            return;
        }
        if (!newPassword.equalsIgnoreCase(confirmedNewPassword)) {
            tilNewPassword.setError(getString(R.string.new_password_not_macth));
            return;
        }
        if (!isValidPassword(newPassword)) {
            tilNewPassword.setError(getString(R.string.password_hint));
            return;
        }
        tilNewPassword.setError("");
        showPleaseWaitDialog();
        Map<String, String> map = new HashMap<>();
        map.put("username", phoneNumber);
        map.put("newPassword", newPassword);
        authService.createNewPassword(map).enqueue(new Callback<Response>() {
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
                showMessage(Static.SOMETHING_WRONG);

            }
        });
    }

    private boolean isValidPassword(String password){
        Pattern pattern = Pattern.compile(Global.PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
