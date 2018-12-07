package id.co.icg.reload.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import id.co.icg.reload.MyFcmListenerService;
import id.co.icg.reload.R;
import id.co.icg.reload.activity.AccountInformationActivity;
import id.co.icg.reload.activity.AddBalanceActivity;
import id.co.icg.reload.activity.ConvertToBalanceActivity;
import id.co.icg.reload.activity.ElectricInquiryActivity;
import id.co.icg.reload.activity.InsuranceInquiryActivity;
import id.co.icg.reload.activity.MobileDataRechargeActivity;
import id.co.icg.reload.activity.MobileRechargeInquiryActivity;
import id.co.icg.reload.activity.OtherBillInquiryActivity;
import id.co.icg.reload.activity.OtherMenuActivity;
import id.co.icg.reload.activity.PendingBalanceActivity;
import id.co.icg.reload.activity.PhoneBillInquiryActivity;
import id.co.icg.reload.activity.RedeemPointsActivity;
import id.co.icg.reload.activity.TransferBalanceActivity;
import id.co.icg.reload.activity.WaterBillInquiryActivity;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.client.service.RedeemService;
import id.co.icg.reload.client.service.TopupService;
import id.co.icg.reload.client.service.TransactionService;
import id.co.icg.reload.model.Biller;
import id.co.icg.reload.model.Deposit;
import id.co.icg.reload.model.Gift;
import id.co.icg.reload.model.PendingBalance;
import id.co.icg.reload.model.Product;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.DialogUtils;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends BaseFragment {

    private SliderLayout slider;
    private TextView tvBalance, tvTrxFee, tvPoint, tvLiabilities;
    private Reseller rs;
    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");
    private DecimalFormat numberFormat = new DecimalFormat("###,###,###");

    private AuthService authService;
    private TopupService topupService;
    private TransactionService transactionService;
    private RedeemService redeemService;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = HomeFragment.class.toString();

    private ImageView ivNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        authService = ApiUtils.AuthService(getActivity());
        topupService = ApiUtils.TopupService(getActivity());
        transactionService = ApiUtils.TransactionService(getActivity());
        redeemService = ApiUtils.RedeemService(getActivity());

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rs = Preferences.getUser(getActivity());
        tvBalance = view.findViewById(R.id.tv_balance);
        tvTrxFee = view.findViewById(R.id.tv_trx_fee);
        tvPoint = view.findViewById(R.id.tv_point);
        tvLiabilities = view.findViewById(R.id.tv_liabilities);
        ivNotification = view.findViewById(R.id.iv_notification);

        ivNotification.setOnClickListener(v -> startChatActivity());

        LinearLayout layoutBalance = view.findViewById(R.id.layout_balance);
        layoutBalance.setOnClickListener(v -> startAddBalanceActivity());

        LinearLayout layoutBonus = view.findViewById(R.id.layout_convert_to_balance);
        layoutBonus.setOnClickListener(v -> convertToBalance());

        LinearLayout layoutPoints = view.findViewById(R.id.layout_redeem_points);
        layoutPoints.setOnClickListener(v -> redeemPoints());

        LinearLayout layoutCredit = view.findViewById(R.id.layout_credit);
        layoutCredit.setOnClickListener(v -> creditRequest());

        LinearLayout layoutMobileRecharge = view.findViewById(R.id.layout_mobile_recharge);
        layoutMobileRecharge.setOnClickListener(v -> startMobileRechargeActivity());

        LinearLayout layoutDataRecharge = view.findViewById(R.id.layout_data_recharge);
        layoutDataRecharge.setOnClickListener(v -> startDataRechargeActivity());

        LinearLayout layoutMobilePostpaid = view.findViewById(R.id.layout_mobile_postpaid);
        layoutMobilePostpaid.setOnClickListener(v -> startMobilePostpaidActivity());

        LinearLayout layoutPayTvPostpaid = view.findViewById(R.id.layout_pay_tv);
        layoutPayTvPostpaid.setOnClickListener(v -> startPayTvPostpaidActivity());

        LinearLayout layoutElectricPrepaid = view.findViewById(R.id.layout_electric_prepaid);
        layoutElectricPrepaid.setOnClickListener(v -> startElectricActivity());

        LinearLayout layoutInsurance = view.findViewById(R.id.layout_insurance);
        layoutInsurance.setOnClickListener(v -> startInsuranceActivity());

        LinearLayout layoutWaterBill = view.findViewById(R.id.layout_water_bill);
        layoutWaterBill.setOnClickListener(v -> startWaterBillActivity());

        LinearLayout layoutTransfer = view.findViewById(R.id.layout_transfer);
        layoutTransfer.setOnClickListener(v -> startTransferActivity());

        LinearLayout layoutOther = view.findViewById(R.id.layout_other);
        layoutOther.setOnClickListener(v -> startOtherMenuActivity());

        slider = view.findViewById(R.id.slider);
        HashMap<String, String> url_maps = new HashMap<>();
        url_maps.put("http://www.golfrecruitmentcentral.com.au/wp-content/uploads/2013/10/this-space-available1.jpg", "Space Available");
        url_maps.put("http://pertaminaretail.com/img/pertamina-retail.jpg", "PTPR");
        url_maps.put("https://farm4.staticflickr.com/3941/15521965358_14e71ee198_o.jpg", "Bright Store");
        loadSlider(url_maps);

        refreshPanel();

        getActivity().registerReceiver(myReceiver, new IntentFilter(MyFcmListenerService.INTENT_FILTER));

        swipeRefreshLayout.setOnRefreshListener(() -> refreshInfo());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    private void refreshPanel(){
        tvBalance.setText(decimalFormat.format(rs.getBalance()));
        tvTrxFee.setText(decimalFormat.format(rs.getTrxFee()));
        tvPoint.setText(numberFormat.format(rs.getPoints()));
        tvLiabilities.setText(decimalFormat.format(rs.getLiabilities()));
    }

    private void refreshInfo(){
        authService.getInfo().enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                    Response body = response.body();
                    JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                    Reseller user = gson.fromJson(jsonObject, Reseller.class);
                    Preferences.setUser(getActivity(), user);
                    if (user != null) {
                        rs = user;
                        refreshPanel();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void startAddBalanceActivity(){
        //check if there is a pending balance
        showPleaseWaitDialog();

        topupService.getUnapprovedDeposit().enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                        List<Deposit> deposits = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Deposit>>() {
                        }.getType());
                        if (deposits!=null && deposits.size()>0){
                            Deposit deposit = deposits.get(0);
                            startPendingBalanceActivity(deposit);
                        }else{
                            startTopupBalanceActivity();
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

    private void loadSlider(HashMap<String, String> url_maps) {
        for (String name : url_maps.keySet()) {
            DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
            textSliderView
                    //.description(url_maps.get(name))
                    .image(name)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", url_maps.get(name));

            slider.addSlider(textSliderView);
        }
        slider.setPresetTransformer(SliderLayout.Transformer.Default);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);
        //slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(4000);
        slider.setVisibility(View.VISIBLE);

    }

    private void convertToBalance(){
        Reseller user = Preferences.getUser(getActivity());
        //if (user.getVerified().equals("Y")){
        if (true){
            Intent intent = new Intent(getActivity(), ConvertToBalanceActivity.class);
            startActivity(intent);
        }else {
            DialogUtils dialogUtils = new DialogUtils(getActivity());
            final Dialog dialog = dialogUtils.createDialog(R.layout.dialog_account_not_verified, true);
            TextView tvDescription = dialog.findViewById(R.id.tv_description);
            tvDescription.setText("Untuk menukarkan komisi ke saldo, akun kamu harus diverifikasi terlebih dahulu");
            Button btnVerify = dialog.findViewById(R.id.btn_verify);
            btnVerify.setOnClickListener(v -> {
                dialog.dismiss();
                startAccountInformationActivity();
            });
            dialog.show();
        }
    }

    private void redeemPoints(){
        Reseller user = Preferences.getUser(getActivity());
        //if (user.getVerified().equals("Y")){
        if (true){
            showPleaseWaitDialog();

            redeemService.getGifts().enqueue(new Callback<Response>() {

                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    dissmissPleaseWaitDialog();
                    try {
                        if (response.isSuccessful()) {
                            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                            JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                            List<Gift> gifts = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Gift>>() {
                            }.getType());
                            if (gifts != null && gifts.size() > 0) {
                                Intent intent = new Intent(getActivity(), RedeemPointsActivity.class);
                                intent.putExtra("gifts", (Serializable) gifts);
                                startActivity(intent);
                            }else{
                                showMessage("Tidak ada hadiah yang dapat ditukarkan saat ini");
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
        }else {
            DialogUtils dialogUtils = new DialogUtils(getActivity());
            final Dialog dialog = dialogUtils.createDialog(R.layout.dialog_account_not_verified, true);
            TextView tvDescription = dialog.findViewById(R.id.tv_description);
            tvDescription.setText("Untuk menukarkan poin ke hadiah, akun kamu harus diverifikasi terlebih dahulu");
            Button btnVerify = dialog.findViewById(R.id.btn_verify);
            btnVerify.setOnClickListener(v -> {
                dialog.dismiss();
                startAccountInformationActivity();
            });
            dialog.show();
        }
    }

    private void creditRequest(){Reseller user = Preferences.getUser(getActivity());
        if (user.getVerified().equals("Y")){
            showMessage("Mohon maaf, untuk sementara fitur ini tidak tersedia");
        }else {
            DialogUtils dialogUtils = new DialogUtils(getActivity());
            final Dialog dialog = dialogUtils.createDialog(R.layout.dialog_account_not_verified, true);
            TextView tvDescription = dialog.findViewById(R.id.tv_description);
            tvDescription.setText("Untuk mengajukan pinjaman saldo, akun kamu harus diverifikasi terlebih dahulu");
            Button btnVerify = dialog.findViewById(R.id.btn_verify);
            btnVerify.setOnClickListener(v -> {
                dialog.dismiss();
                startAccountInformationActivity();
            });
            dialog.show();
        }
    }

    private void startMobileRechargeActivity() {

        showPleaseWaitDialog();

        transactionService.getProductsByCategory(1).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Product> products = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Product>>() {
                        }.getType());
                        if (products != null && products.size() > 0) {
                            Intent intent = new Intent(getActivity(), MobileRechargeInquiryActivity.class);
                            intent.putExtra("products", (Serializable) products);
                            startActivity(intent);
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


    private void startDataRechargeActivity(){
        showPleaseWaitDialog();

        transactionService.getProductsByCategory(2).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Product> products = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Product>>() {
                        }.getType());
                        if (products != null && products.size() > 0) {
                            Intent intent = new Intent(getActivity(), MobileDataRechargeActivity.class);
                            intent.putExtra("products", (Serializable) products);
                            startActivity(intent);
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

    private void startMobilePostpaidActivity(){
        Intent intent = new Intent(getActivity(), PhoneBillInquiryActivity.class);
        startActivity(intent);
    }

    private void startInsuranceActivity(){
        Intent intent = new Intent(getActivity(), InsuranceInquiryActivity.class);
        startActivity(intent);
    }

    private void startPayTvPostpaidActivity(){
        transactionService.getProductsByBiller("045").enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Product> products = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Product>>() {
                        }.getType());
                        if (products != null && products.size() > 0) {
                            Intent intent = new Intent(getActivity(), OtherBillInquiryActivity.class);
                            intent.putExtra("products", (Serializable) products);
                            startActivity(intent);
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

    private void startElectricActivity(){
        //query products
        Intent intent = new Intent(getActivity(), ElectricInquiryActivity.class);
        startActivity(intent);
    }

    private void startWaterBillActivity(){
        showPleaseWaitDialog();

        transactionService.getProductsByBiller("016").enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Product> products = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Product>>() {
                        }.getType());
                        if (products != null && products.size() > 0) {
                            Intent intent = new Intent(getActivity(), WaterBillInquiryActivity.class);
                            intent.putExtra("products", (Serializable) products);
                            startActivity(intent);
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

    private void startOtherMenuActivity(){
        showPleaseWaitDialog();

        transactionService.getBillersByCategory(4).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Biller> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Biller>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            Intent intent = new Intent(getActivity(), OtherMenuActivity.class);
                            intent.putExtra("billers", (Serializable) list);
                            startActivity(intent);
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

    private void startTransferActivity(){
        Intent intent = new Intent(getActivity(), TransferBalanceActivity.class);
        startActivity(intent);
    }

    private void startTopupBalanceActivity(){
        Intent intent = new Intent(getActivity(), AddBalanceActivity.class);
        startActivity(intent);
    }

    private void startPendingBalanceActivity(Deposit deposit){
        Intent intent = new Intent(getActivity(), PendingBalanceActivity.class);
        PendingBalance pendingBalance = new PendingBalance();
        pendingBalance.setId(deposit.getId());
        pendingBalance.setAmount(deposit.getCredit());
        pendingBalance.setProof(deposit.getProof());
        pendingBalance.setBankAccount(deposit.getDescription());
        pendingBalance.setDatetime(deposit.getDatetime());
        pendingBalance.setTransactionId(deposit.getStan());
        intent.putExtra("model", pendingBalance);
        startActivity(intent);
    }


}
