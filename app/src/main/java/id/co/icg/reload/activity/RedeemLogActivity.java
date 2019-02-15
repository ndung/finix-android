package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.RedeemLogAdapter;
import id.co.icg.reload.model.RedeemLog;

public class RedeemLogActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvLogs;
    private RedeemLogAdapter redeemLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_redeem_logs);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvLogs = findViewById(R.id.rv_logs);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Riwayat penukaran poin");

        List<RedeemLog> list = (List<RedeemLog>) getIntent().getSerializableExtra("logs");

        redeemLogAdapter = new RedeemLogAdapter(this, list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(layoutManager);
        rvLogs.setItemAnimator(new DefaultItemAnimator());
        rvLogs.setAdapter(redeemLogAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
