package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.HistoryService;
import id.co.icg.reload.model.ProfitSettlement;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class ConvertToBalanceActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvBonusFee;
    private TextView tvMaxFee;
    private TextView tvFeeAmount;
    private ImageView ivHistory;

    private HistoryService historyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_convert_to_balance);

        ivBack = findViewById(R.id.iv_back);
        ivHistory = findViewById(R.id.iv_history);
        tvTitle = findViewById(R.id.tv_title);

        tvBonusFee = findViewById(R.id.tv_bonus_fee);
        tvMaxFee = findViewById(R.id.tv_max_fee);
        tvFeeAmount = findViewById(R.id.tv_fee_amount);

        ivBack.setOnClickListener(v -> finish());

        ivHistory.setOnClickListener(v -> getLogs());

        tvTitle.setText("Pencairan komisi");

        historyService = ApiUtils.HistoryService(this);
    }

    private void getLogs(){
        showPleaseWaitDialog();

        historyService.getSettlementLogs().enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<ProfitSettlement> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<ProfitSettlement>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            next(list);
                        }else{
                            showMessage("Anda belum pernah melakukan pencairan komisi");
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

    private void next(List<ProfitSettlement> list){
        Intent intent = new Intent(this, SettlementLogActivity.class);
        intent.putExtra("logs", (Serializable) list);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
