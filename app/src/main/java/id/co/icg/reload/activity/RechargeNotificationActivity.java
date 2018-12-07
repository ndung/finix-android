package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.icg.reload.R;

public class RechargeNotificationActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvDateTime;
    private TextView tvProduct;
    private TextView tvPrice;
    private TextView tvTransactionId;
    private TextView tvPhoneNumber;
    private TextView tvVoucherSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recharge_notification);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(R.string.recharge_notification);

        tvDateTime = findViewById(R.id.tv_date_time);
        tvProduct = findViewById(R.id.tv_product);
        tvPrice = findViewById(R.id.tv_price);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvVoucherSn = findViewById(R.id.tv_voucher_sn);

        ivBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
