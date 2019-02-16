package id.co.icg.reload.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.icg.reload.R;

public class ContactUsActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Contact Us");
    }
}
