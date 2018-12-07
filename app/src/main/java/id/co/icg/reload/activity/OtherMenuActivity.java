package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
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
import java.util.Map;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.BillerAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.TransactionService;
import id.co.icg.reload.model.Biller;
import id.co.icg.reload.model.Product;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class OtherMenuActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvMenu;

    private TransactionService transactionService;
    private BillerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other_menu);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvMenu = findViewById(R.id.rv_menus);

        tvTitle.setText(R.string.other_menu);

        transactionService = ApiUtils.TransactionService(this);

        ivBack.setOnClickListener(v -> finish());

        rvMenu = findViewById(R.id.rv_menus);

        ArrayList<Biller> billers = (ArrayList<Biller>) getIntent().getSerializableExtra("billers");
        adapter = new BillerAdapter(this, billers, model -> next(model));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMenu.setLayoutManager(layoutManager);
        rvMenu.setAdapter(adapter);
        rvMenu.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void next(Biller biller){
        showPleaseWaitDialog();

        transactionService.getProductsByBiller(biller.getId()).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Product> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Product>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            next(list);
                        }else{
                            showMessage("Tidak ada produk yang aktif saat ini");
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

    private void next(List<Product> products){
        Intent intent = new Intent(this, OtherRechargeInquiryActivity.class);
        intent.putExtra("products", (Serializable)products);
        startActivity(intent);
    }
}
