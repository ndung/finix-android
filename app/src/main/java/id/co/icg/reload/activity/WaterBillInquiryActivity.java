package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;
import id.co.icg.reload.R;
import id.co.icg.reload.model.Product;

public class WaterBillInquiryActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etCustomerId;
    private TextView tvInquiry;
    private CustomCheckBox cbPostpaid;
    private TextInputLayout tilCustomerId;
    private MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_water_inquiry);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvInquiry = findViewById(R.id.tv_inquiry);

        cbPostpaid = findViewById(R.id.cb_postpaid);
        cbPostpaid.setChecked(true);

        etCustomerId = findViewById(R.id.et_customer_id);
        tilCustomerId = findViewById(R.id.til_customer_id);

        spinner = findViewById(R.id.spinner);

        ArrayList<Product> products = (ArrayList<Product>) getIntent().getSerializableExtra("products");

        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Pembayaran tagihan air");

        tvInquiry.setOnClickListener(v -> inquiry());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void inquiry(){
        if (etCustomerId.getText().equals("")){
            tilCustomerId.setError("Masukkan nomor telepon atau ID pelanggan");
            return;
        }
        if (spinner.getSelectedItemPosition()==0){
            spinner.setError("Pilih produk yang diinginkan");
            return;
        }

    }
}
