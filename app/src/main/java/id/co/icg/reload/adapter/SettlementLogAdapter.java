package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.ProfitSettlement;

public class SettlementLogAdapter extends RecyclerView.Adapter<SettlementLogAdapter.ViewHolder> {

    private Context context;
    private List<ProfitSettlement> list;

    public SettlementLogAdapter(Context context, List<ProfitSettlement> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_settlement_log, parent, false);
        return new ViewHolder(view);
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ProfitSettlement log = list.get(position);

        holder.tvDatetime.setText(sdf.format(log.getTimestamp()));
        holder.tvFeeAmount.setText(decimalFormat.format(log.getFee()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDatetime;
        TextView tvFeeAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDatetime = itemView.findViewById(R.id.tv_date_time);
            tvFeeAmount = itemView.findViewById(R.id.tv_fee_amount);
        }
    }
}
