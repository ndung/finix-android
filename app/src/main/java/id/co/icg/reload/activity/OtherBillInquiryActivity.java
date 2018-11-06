package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;
import id.co.icg.reload.R;
import id.co.icg.reload.model.Biller;
import id.co.icg.reload.model.Product;

public class OtherBillInquiryActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private MaterialSpinner spinner;
    private EditText etCustomerId;
    private TextView tvInquiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other_bill_inquiry);

        ArrayList<Product> products = (ArrayList<Product>) getIntent().getSerializableExtra("products");

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        spinner = findViewById(R.id.spinner);
        tvInquiry = findViewById(R.id.tv_inquiry);
        etCustomerId = findViewById(R.id.et_customer_id);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        tvTitle.setText("Pembayaran tagihan lainnya");
        tvInquiry.setOnClickListener(new View.OnClickListener() {
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
