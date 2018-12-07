package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Product;

public class MobileRechargeConfirmationActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvDateTime;
    private TextView tvProduct;
    private TextView tvPrice;
    private TextView tvPhoneNumber;
    private TextView tvPay;
    private TextView tvTrxNo;
    private String phoneNumber;
    private Product product;
    private String trxNo;


    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mobile_recharge_confirmation);

        trxNo = getIntent().getStringExtra("trxNo");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        product = (Product) getIntent().getSerializableExtra("product");

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText(R.string.recharge_confirmation);

        tvTrxNo = findViewById(R.id.tv_trx_no);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvProduct = findViewById(R.id.tv_product);
        tvPrice = findViewById(R.id.tv_price);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);

        tvPhoneNumber.setText(phoneNumber);
        tvProduct.setText(product.getId()+" - "+product.getName());
        tvPrice.setText(decimalFormat.format(product.getBuyingPrice()));
        tvTrxNo.setText(trxNo);

        ivBack.setOnClickListener(v -> finish());

        tvPay = findViewById(R.id.tv_pay);
        tvPay.setOnClickListener(v -> {

        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
