package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.icg.reload.R;

public class BillPaymentActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvDateTime;
    private TextView tvProduct;
    private TextView tvBillAmount;
    private TextView tvCustomerName;
    private TextView tvAdminFee;
    private TextView tvCustomerId;
    private TextView tvCashback;
    private TextView tvPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_confirmation);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(R.string.recharge_confirmation);

        tvDateTime = findViewById(R.id.tv_date_time);
        tvProduct = findViewById(R.id.tv_product);
        tvBillAmount = findViewById(R.id.tv_bill_amount);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvAdminFee = findViewById(R.id.tv_admin_fee);
        tvCustomerId = findViewById(R.id.tv_customer_id);
        tvCashback = findViewById(R.id.tv_cashback);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvPay = findViewById(R.id.tv_pay);
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvPay.getText().equals(getString(R.string.pay))){

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
