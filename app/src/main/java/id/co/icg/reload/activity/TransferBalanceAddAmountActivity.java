package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.TransactionService;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class TransferBalanceAddAmountActivity extends BaseActivity{

    private TextView tvUserInitials;
    private TextView tvUserName;
    private TextView tvMobileNumber;
    private TextView tvAmount;
    private TextView tvBeneficiaryDescription;
    private TextView tvSend;
    private ImageView ivBack;
    private TextView tvTitle;
    private Reseller rs;
    private TransactionService transactionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transactionService = ApiUtils.TransactionService(this);

        setContentView(R.layout.activity_transfer_balance_add_amount);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvUserInitials = findViewById(R.id.tv_user_initials);
        tvUserName = findViewById(R.id.tv_user_name);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvAmount = findViewById(R.id.tv_amount);
        tvBeneficiaryDescription = findViewById(R.id.tv_beneficiary_desc);
        tvSend = findViewById(R.id.tv_send);

        rs = (Reseller) getIntent().getSerializableExtra("user");
        if (rs.getName()!=null){
            String name = rs.getName();
            tvUserInitials.setText(name.substring(0,1).toUpperCase());
            tvUserName.setText(name);
        }
        tvMobileNumber.setText(rs.getId());

        ivBack.setOnClickListener(v -> finish());

        tvSend.setOnClickListener(v -> transfer());

        tvTitle.setText(R.string.balance_transfer);
    }

    private void transfer(){
        if (TextUtils.isEmpty(tvAmount.getText().toString())){
            tvAmount.setError("Nominal transfer harus diisi");
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("rs", rs.getId());
        map.put("amount", tvAmount.getText().toString());
        map.put("description", tvBeneficiaryDescription.getText().toString());
        showPleaseWaitDialog();
        transactionService.transferBalance(map).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        String resp = gson.fromJson(jsonObject, String.class);

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

    private void next(){
        Intent intent = new Intent(this, TransferBalanceNotificationActivity.class);
        startActivity(intent);
        finish();
        TransferBalanceActivity.activity.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
