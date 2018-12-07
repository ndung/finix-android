package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.TopupService;
import id.co.icg.reload.model.Deposit;
import id.co.icg.reload.model.PendingBalance;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.PartUtils;
import id.co.icg.reload.util.Static;
import okhttp3.MultipartBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PendingBalanceActivity extends BaseActivity {

    private ImageView ivBack;
    private ImageView ivHistory;
    private TextView tvTitle;
    private TextView tvDateTime;
    private TextView tvBankAccount;
    private TextView tvAmount;
    private TextView tvTransactionId;
    private TextView tvExpiredDate;
    private TextView tvAddEvidence;
    private TextView tvProof;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    private TopupService topupService;
    private PendingBalance pendingBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topupService = ApiUtils.TopupService(this);

        setContentView(R.layout.activity_pending_balance);

        pendingBalance = (PendingBalance) getIntent().getSerializableExtra("model");

        ivBack = findViewById(R.id.iv_back);
        tvProof = findViewById(R.id.tv_proof);
        tvTitle = findViewById(R.id.tv_title);
        ivHistory = findViewById(R.id.iv_history);

        tvTitle.setText(R.string.add_balance);

        String date = sdf.format(pendingBalance.getDatetime());

        tvDateTime = findViewById(R.id.tv_date_time);
        tvDateTime.setText(sdf.format(pendingBalance.getDatetime()));

        tvBankAccount = findViewById(R.id.tv_bank_account);
        tvBankAccount.setText(pendingBalance.getBankAccount());

        tvAmount = findViewById(R.id.tv_amount);
        tvAmount.setText(decimalFormat.format(pendingBalance.getAmount()));

        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvTransactionId.setText(pendingBalance.getTransactionId());

        tvExpiredDate = findViewById(R.id.tv_expired_date);
        tvExpiredDate.setText(date.substring(0, 10) + " 21:59:59");

        ivBack.setOnClickListener(v -> finish());

        tvAddEvidence = findViewById(R.id.tv_add_evidence);
        tvAddEvidence.setOnClickListener(v -> attach());

        if (!TextUtils.isEmpty(pendingBalance.getProof())){
            String link = Static.BASE_URL+"files/"+pendingBalance.getProof();
            SpannableString content = new SpannableString(link);
            content.setSpan(new UnderlineSpan(), 0, link.length(), 0);
            tvProof.setText(content);
        }
        tvProof.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(tvProof.getText().toString())){
                new DownloadFileAsync().execute(tvProof.getText().toString());
            }
        });

        ivHistory.setOnClickListener(v -> showHistory());
    }


    private void showHistory(){
        startActivity(new Intent(this, DepositLogActivity.class));
    }

    private void attach() {
        TedPermission.with(this)
                .setPermissionListener(
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                pickAttachment();
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }

                        })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void pickAttachment() {
        EasyImage.openChooserWithDocuments(this, "Pilih", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                showMessage(e.getMessage());
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                if (imagesFiles.size()>0){
                    uploadProof(imagesFiles.get(0));
                }
            }
        });
    }

    private void uploadProof(File path) {
        MultipartBody.Part body = PartUtils.prepareFilePart("proof", path);
        topupService.uploadTransferProof(pendingBalance.getId(), body).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();
                        Deposit deposit = gson.fromJson(jsonObject, Deposit.class);
                        String link = Static.BASE_URL+"files/"+deposit.getProof();
                        SpannableString content = new SpannableString(link);
                        content.setSpan(new UnderlineSpan(), 0, link.length(), 0);
                        tvProof.setText(content);
                        showMessage("Upload bukti transfer sukses");
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}
