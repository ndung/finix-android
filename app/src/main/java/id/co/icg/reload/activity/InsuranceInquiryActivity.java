package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Biller;

public class InsuranceInquiryActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etCustomerId;
    private TextView tvInquiry;
    private CustomCheckBox cbBpjs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_insurance_inquiry);

        cbBpjs = findViewById(R.id.cb_bpjs);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etCustomerId = findViewById(R.id.et_customer_id);
        tvInquiry = findViewById(R.id.tv_inquiry);
        ivBack.setOnClickListener(v -> finish());

        cbBpjs.setChecked(true);

        tvTitle.setText("Pembayaran premi asuransi");
        tvInquiry.setOnClickListener(v -> {

        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
