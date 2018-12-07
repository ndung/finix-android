package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.BankAccountAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.TopupService;
import id.co.icg.reload.fragment.BaseFragment;
import id.co.icg.reload.model.BankAccount;
import id.co.icg.reload.model.Deposit;
import id.co.icg.reload.model.PendingBalance;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.ui.NumberTextWatcher;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class AddBalanceActivity extends BaseActivity {

    private ImageView ivBack;
    private ImageView ivHistory;
    private TextView tvTitle;
    private EditText etAmt;
    private EditText etDescription;
    private TextView tvDeposit;
    private RecyclerView rvBankAccount;

    private List<BankAccount> list;
    private BankAccountAdapter adapter;
    private BankAccount selected;

    private TopupService topupService;

    private static final String TAG = AddBalanceActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topupService = ApiUtils.TopupService(this);

        setContentView(R.layout.activity_add_balance);

        ivBack = findViewById(R.id.iv_back);
        ivHistory = findViewById(R.id.iv_history);
        tvTitle = findViewById(R.id.tv_title);
        etAmt = findViewById(R.id.et_amt);
        etDescription = findViewById(R.id.et_description);
        tvDeposit = findViewById(R.id.tv_deposit);
        rvBankAccount = findViewById(R.id.rv_bank_account);

        etAmt.addTextChangedListener(new NumberTextWatcher(etAmt));

        tvTitle.setText(R.string.add_balance);

        tvDeposit.setOnClickListener(v -> topup());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvBankAccount.setLayoutManager(layoutManager);
        rvBankAccount.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();

        adapter = new BankAccountAdapter(this, list, model -> refreshBankAccounts(model));
        rvBankAccount.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        topupService.getBankAccounts().enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<BankAccount> bankAccounts = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<BankAccount>>() {
                        }.getType());
                        list.clear();
                        for (BankAccount bankAccount : bankAccounts){
                            list.add(bankAccount);
                        }
                        adapter.notifyDataSetChanged();
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
                showMessage(Static.SOMETHING_WRONG);

            }
        });

        ivHistory.setOnClickListener(v -> showHistory());
    }

    private void showHistory(){
        startActivity(new Intent(this, DepositLogActivity.class));
    }

    private void refreshBankAccounts(BankAccount bankAccount){
        this.selected = bankAccount;
        List<BankAccount> bankAccounts = new ArrayList<>(list);
        list.clear();
        for (BankAccount account : bankAccounts){
            if (!account.getBankId().equals(bankAccount.getBankId())){
                account.setSelected(false);
            }else {
                account.setSelected(true);
            }
            list.add(account);
        }
        adapter.notifyDataSetChanged();
    }

    private void topup(){
        if (selected==null){
            showMessage("Pilih bank tujuan transfer");
            return;
        }
        if (TextUtils.isEmpty(etAmt.getText().toString())){
            etAmt.setError("Isikan jumlah transfer dalam ribuan rupiah");
            return;
        }
        if (!etAmt.getText().toString().endsWith("000")){
            etAmt.setError("Jumlah transfer harus dalam ribuan rupiah, misalnya 50.000, 100.000, 125.000, dstnya");
            return;
        }
        showPleaseWaitDialog();
        Map<String,String> map = new HashMap<>();
        map.put("bankAccountId", selected.getBankId());
        map.put("amount", etAmt.getText().toString().replaceAll("\\,","").replaceAll("\\.",""));
        map.put("description", etDescription.getText().toString());
        topupService.addBalance(map).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                    if (response.isSuccessful()) {
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        Deposit deposit = gson.fromJson(jsonObject, Deposit.class);
                        if (deposit != null) {
                            next(deposit);
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

    private void next(Deposit deposit){
        Intent intent = new Intent(this, PendingBalanceActivity.class);
        PendingBalance pendingBalance = new PendingBalance();
        pendingBalance.setId(deposit.getId());
        pendingBalance.setAmount(deposit.getCredit());
        pendingBalance.setBankAccount(deposit.getDescription());
        pendingBalance.setDatetime(deposit.getDatetime());
        pendingBalance.setTransactionId(deposit.getStan());
        intent.putExtra("model", pendingBalance);
        startActivity(intent);
    }

}
