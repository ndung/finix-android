package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ChangePasswordActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmedNewPassword;
    private TextView tvSave;
    private TextInputLayout tilOldPassword;
    private TextInputLayout tilNewPassword;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authService = ApiUtils.AuthService(this);

        setContentView(R.layout.activity_change_password);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(R.string.change_password);

        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmedNewPassword = findViewById(R.id.et_confirmed_new_password);
        tvSave = findViewById(R.id.tv_save);

        tilOldPassword = findViewById(R.id.til_old_password);
        tilNewPassword = findViewById(R.id.til_new_password);

        tilNewPassword.setHint(getString(R.string.password_hint));

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(etOldPassword.getText().toString(), etNewPassword.getText().toString(), etConfirmedNewPassword.getText().toString());
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void changePassword(String oldPassword, String newPassword, String confirmedNewPassword){
        if (TextUtils.isEmpty(oldPassword)){
            tilOldPassword.setError("Isikan password lama");
            return;
        }
        if (TextUtils.isEmpty(newPassword)){
            tilNewPassword.setError("Isikan password baru");
            return;
        }
        if (TextUtils.isEmpty(confirmedNewPassword)){
            tilNewPassword.setError("Ketik ulang password baru");
            return;
        }
        if (!isValidPassword(newPassword)){
            tilNewPassword.setError(getString(R.string.password_hint));
            return;
        }
        if (!etNewPassword.getText().toString().equalsIgnoreCase(etConfirmedNewPassword.getText().toString())){
            tilNewPassword.setError(getString(R.string.new_password_not_macth));
            return;
        }
        tilOldPassword.setError("");
        tilNewPassword.setError("");
        showPleaseWaitDialog();
        Map<String, String> map = new HashMap<>();
        map.put("oldPassword", oldPassword);
        map.put("newPassword", newPassword);
        authService.changePassword(map).enqueue(new Callback<Response>() {
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
                            Preferences.setUser(getApplicationContext(), user);
                            showMessage("Berhasil ubah password!");
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
