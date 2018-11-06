package id.co.icg.reload.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.icg.reload.R;

public class TransferBalanceNotificationActivity extends BaseActivity{

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvDateTime;
    private TextView tvBeneficiary;
    private TextView tvAmount;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transfer_balance_notification);

        tvDateTime = findViewById(R.id.tv_date_time);
        tvBeneficiary = findViewById(R.id.tv_beneficiary);
        tvAmount = findViewById(R.id.tv_amount);
        tvDescription = findViewById(R.id.tv_description);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTitle.setText(R.string.balance_transfer);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
