package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.DepositLogAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.HistoryService;
import id.co.icg.reload.model.Deposit;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class DepositLogActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvLogs;
    private DepositLogAdapter depositLogAdapter;
    private NestedScrollView nestedScrollView;


    private static final int PAGE_START = 0;
    private int currentPage = PAGE_START;

    private HistoryService historyService;
    private List<Deposit> deposits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deposit_logs);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvLogs = findViewById(R.id.rv_logs);
        nestedScrollView = findViewById(R.id.scroll_view);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Riwayat penambahan saldo");

        deposits = new ArrayList<>();

        depositLogAdapter = new DepositLogAdapter(this, deposits);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(layoutManager);
        rvLogs.setItemAnimator(new DefaultItemAnimator());
        rvLogs.setAdapter(depositLogAdapter);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    currentPage += 1;
                    getLogs();
                }
            }
        });

        historyService = ApiUtils.HistoryService(this);
        getLogs();
    }

    private void getLogs(){
        showPleaseWaitDialog();

        historyService.getDepositLogs(currentPage).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Deposit> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Deposit>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            for (Deposit log : list){
                                deposits.add(log);
                            }
                            depositLogAdapter.notifyDataSetChanged();
                        }else{
                            showMessage("Tidak ada log deposit");
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
