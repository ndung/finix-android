package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.icg.reload.R;

public class ElectricRechargeConfirmationActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvDateTime;
    private TextView tvProduct;
    private TextView tvPrice;
    private TextView tvCustomerName;
    private TextView tvPhoneNumber;
    private TextView tvCustomerId;
    private TextView tvPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_electric_recharge_confirmation);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(R.string.recharge_confirmation);

        tvDateTime = findViewById(R.id.tv_date_time);
        tvProduct = findViewById(R.id.tv_product);
        tvPrice = findViewById(R.id.tv_price);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvCustomerId = findViewById(R.id.tv_customer_id);

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

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
