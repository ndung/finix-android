package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.GiftAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.RedeemService;
import id.co.icg.reload.model.Gift;
import id.co.icg.reload.model.RedeemLog;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class RedeemPointsActivity extends BaseActivity {

    private ImageView ivBack;
    private ImageView ivHistory;
    private TextView tvTitle;
    private RecyclerView rvGifts;
    private GiftAdapter giftAdapter;

    private RedeemService redeemService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_redeem_points);

        ivBack = findViewById(R.id.iv_back);
        ivHistory = findViewById(R.id.iv_history);
        tvTitle = findViewById(R.id.tv_title);
        rvGifts = findViewById(R.id.rv_gifts);

        redeemService = ApiUtils.RedeemService(this);

        ivBack.setOnClickListener(v -> finish());

        ivHistory.setOnClickListener(v -> getLogs());

        tvTitle.setText(R.string.redeem_points);

        ArrayList<Gift> gifts = (ArrayList<Gift>) getIntent().getSerializableExtra("gifts");

        giftAdapter = new GiftAdapter(this, gifts, gift -> redeemGift(gift));

        int numberOfColumns = 3;
        rvGifts.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvGifts.setItemAnimator(new DefaultItemAnimator());
        rvGifts.setAdapter(giftAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void redeemGift(final Gift gift){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage("Anda ingin menukarkan "+gift.getPts()+" poin dengan hadiah "+gift.getName()+" ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    showPleaseWaitDialog();
                    redeemService.redeemGift(gift.getId()).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            dissmissPleaseWaitDialog();
                            try {
                                if (response.isSuccessful()) {
                                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                                    Response body = response.body();
                                    JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                                    Reseller user = gson.fromJson(jsonObject, Reseller.class);
                                    if (user == null) {
                                        showMessage(Static.SOMETHING_WRONG);
                                    }else{
                                        getLogs();
                                        finish();
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
                })

                .setNegativeButton("Tidak", (dialog, which) -> {

                }).show();
    }

    private void getLogs(){
        showPleaseWaitDialog();

        redeemService.getLogs().enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<RedeemLog> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<RedeemLog>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            next(list);
                        }else{
                            showMessage("Anda belum pernah melakukan penukaran poin");
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

    private void next(List<RedeemLog> list){
        Intent intent = new Intent(this, RedeemLogActivity.class);
        intent.putExtra("logs", (Serializable) list);
        startActivity(intent);
    }
}
