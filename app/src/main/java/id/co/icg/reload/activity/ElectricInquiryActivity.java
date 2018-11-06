package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import id.co.icg.reload.R;
import id.co.icg.reload.model.Product;

public class ElectricInquiryActivity extends BaseActivity{

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etCustomerId;
    private EditText etMobileNumber;
    private CustomCheckBox cbPostpaid;
    private CustomCheckBox cbPrepaid;
    private MaterialSpinner spinner;
    private TextInputLayout tilCustomerId;
    private TextView tvInquiry;

    private List<Product> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_electric_inquiry);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etCustomerId = findViewById(R.id.et_customer_id);
        etMobileNumber = findViewById(R.id.et_mobile_number);
        cbPostpaid = findViewById(R.id.cb_postpaid);
        cbPrepaid = findViewById(R.id.cb_prepaid);
        spinner = findViewById(R.id.spinner);
        tilCustomerId = findViewById(R.id.til_customer_id);
        tvInquiry = findViewById(R.id.tv_inquiry);
        ivBack.setOnClickListener(v -> finish());

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

        cbPrepaid.setChecked(true);

        cbPostpaid.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked){
                cbPrepaid.setChecked(false);
                etMobileNumber.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
            }
        });

        cbPrepaid.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked){
                cbPostpaid.setChecked(false);
                etMobileNumber.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
            }
        });

        tvTitle.setText("Pembelian token dan pembayaran tagihan listrik");

        tvInquiry.setOnClickListener(v -> inquiry());
    }

    private void inquiry(){
        if (etCustomerId.getText().equals("")){
            tilCustomerId.setError("Masukkan ID pelanggan");
            return;
        }
        if (cbPrepaid.isSelected() && spinner.getSelectedItemPosition()==0){
            spinner.setError("Pilih denom yang diinginkan");
            return;
        }

    }
}
