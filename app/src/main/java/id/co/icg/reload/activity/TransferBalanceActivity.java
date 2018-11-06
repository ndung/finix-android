package id.co.icg.reload.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;

import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class TransferBalanceActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etMobileNumber;
    private TextView tvInquiry;
    private TextInputLayout tilMobileNumber;
    private AuthService authService;

    static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        authService = ApiUtils.AuthService(this);

        setContentView(R.layout.activity_transfer_balance);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etMobileNumber = findViewById(R.id.et_mobile_number);
        tilMobileNumber = findViewById(R.id.til_mobile_number);
        etMobileNumber.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etMobileNumber.getRight() - etMobileNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    pickContacts(etMobileNumber);
                    return true;
                }
            }
            return false;
        });
        tvInquiry = findViewById(R.id.tv_inquiry);
        tvInquiry.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etMobileNumber.getText().toString())){
                tilMobileNumber.setError("User tujuan penerima harus diisi");
            }else {
                inquiry(etMobileNumber.getText().toString());
            }
        });

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText(R.string.balance_transfer);
    }

    private void inquiry(String userId){
        Reseller rs = Preferences.getUser(this);
        if (rs.getId().equals(userId)){
            tilMobileNumber.setError("Tujuan penerima transfer tidak boleh sama dengan pengirim");
            return;
        }

        tilMobileNumber.setError("");
        showPleaseWaitDialog();
        authService.getUser(userId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        Reseller user = gson.fromJson(jsonObject, Reseller.class);
                        if (user != null) {
                            next(user);
                        }else{
                            showMessage(Static.SOMETHING_WRONG);
                        }
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                }catch (Exception ex){
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

    private void next(Reseller rs){
        Intent intent = new Intent(this, TransferBalanceAddAmountActivity.class);
        intent.putExtra("user", rs);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
