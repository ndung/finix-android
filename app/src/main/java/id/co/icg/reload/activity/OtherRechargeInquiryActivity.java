package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.ganfra.materialspinner.MaterialSpinner;
import id.co.icg.reload.R;
import id.co.icg.reload.adapter.ProductAdapter;
import id.co.icg.reload.model.Product;
import id.co.icg.reload.util.Global;

public class OtherRechargeInquiryActivity extends BaseActivity {

    private static final String TAG = OtherRechargeInquiryActivity.class.toString();

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etMobileNumber;
    private RecyclerView rvProducts;
    private MaterialSpinner spinner;
    private TextInputLayout tilMobileNumber;

    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other_recharge_inquiry);

        tilMobileNumber = findViewById(R.id.til_mobile_number);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etMobileNumber = findViewById(R.id.et_mobile_number);

        etMobileNumber.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etMobileNumber.getRight() - etMobileNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    pickContacts(etMobileNumber);
                    return true;
                }
            }
            return false;
        });

        spinner = findViewById(R.id.spinner);
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5", "6");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(1);

        List<Product> products = (List<Product>) getIntent().getSerializableExtra("products");

        rvProducts = findViewById(R.id.rv_products);
        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText(R.string.mobile_reguler_recharge);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvProducts.setLayoutManager(layoutManager);
        rvProducts.setItemAnimator(new DefaultItemAnimator());

        adapter = new ProductAdapter(this, products, model -> confirm(etMobileNumber.getText().toString(), model));
        rvProducts.setAdapter(adapter);
    }

    private boolean confirm(String phoneNumber, Product product) {
        boolean isPhoneNumberValid = !phoneNumber.equals("");

        if (isPhoneNumberValid) {
            String noTrx = "1";
            if (spinner.getSelectedItem() != null) {
                noTrx = spinner.getSelectedItem().toString();
            }

            Intent intent = new Intent(this, MobileRechargeConfirmationActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("product", product);
            intent.putExtra("trxNo", noTrx);
            startActivity(intent);

            return true;
        }else{
            tilMobileNumber.setError("Nomor pelanggan harus diisi");
            return false;
        }
    }
}
