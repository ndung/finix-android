package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.io.Serializable;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Biller;
import id.co.icg.reload.util.Global;

public class PhoneBillInquiryActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etMobileNumber;
    private TextView tvInquiry;
    private CustomCheckBox cbTelkom;
    private CustomCheckBox cbOther;
    private TextInputLayout tilCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_bill_inquiry);

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
        tvInquiry = findViewById(R.id.tv_inquiry);

        cbTelkom = findViewById(R.id.cb_telkom);
        cbOther = findViewById(R.id.cb_postpaid);

        cbTelkom.setChecked(true);

        cbTelkom.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked){
                cbOther.setChecked(false);
            }
        });

        cbOther.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked){
                cbTelkom.setChecked(false);
            }
        });

        tilCustomerId = findViewById(R.id.til_customer_id);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Pembayaran tagihan telepon pascabayar");

        tvInquiry.setOnClickListener(v -> {
            if (etMobileNumber.getText().equals("")){
                tilCustomerId.setError("Masukkan nomor telepon atau ID pelanggan");
            }
        });
    }

    private void inquiry(String phoneNumber){
        String billerId = "009";
        if (cbOther.isChecked()){
            boolean isPhoneNumberValid = !phoneNumber.equals("") && phoneNumber.length()>=10 && phoneNumber.startsWith("0");
            if (!isPhoneNumberValid){
                tilCustomerId.setError("Mobile phone number is not valid");
                return;
            }
            String prefix = phoneNumber.substring(1, 4);
            if (Global.tselPrefixes.contains(prefix)) {
                billerId = "002";
            }
            else if (Global.isatPrefixes.contains(prefix)) {
                billerId = "017";
            }
            else if (Global.xlPrefixes.contains(prefix)) {
                billerId = "004";
            }
            else if (Global.threePrefixes.contains(prefix)) {
                billerId = "017";
            }
            else if (Global.smartfrenPrefixes.contains(prefix)) {
                billerId = "006";
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
