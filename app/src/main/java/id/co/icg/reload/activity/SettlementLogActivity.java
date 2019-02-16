package id.co.icg.reload.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.SettlementLogAdapter;
import id.co.icg.reload.model.ProfitSettlement;

public class SettlementLogActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvLogs;
    private SettlementLogAdapter settlementLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_redeem_logs);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvLogs = findViewById(R.id.rv_logs);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Riwayat pencairan komisi");

        List<ProfitSettlement> list = (List<ProfitSettlement>) getIntent().getSerializableExtra("logs");

        settlementLogAdapter = new SettlementLogAdapter(this, list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(layoutManager);
        rvLogs.setItemAnimator(new DefaultItemAnimator());
        rvLogs.setAdapter(settlementLogAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
