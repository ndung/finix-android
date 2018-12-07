package id.co.icg.reload.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.AccountLog;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> implements Filterable {

    private static final String TAG = TransactionAdapter.class.toString();

    private Context context;
    private List<AccountLog> logs;
    private List<AccountLog> mFilterList = new ArrayList<>();

    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    public TransactionAdapter(Context context, List<AccountLog> logs, TransactionAdapter.OnItemClickListener listener) {
        this.context = context;
        this.logs = logs;
        this.mFilterList = logs;
        this.listener = listener;
    }

    private ValueFilter valueFilter;

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public interface OnItemClickListener {
        boolean onItemClick(AccountLog model);
    }

    private final TransactionAdapter.OnItemClickListener listener;

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction, parent,false);

        return new TransactionAdapter.ViewHolder(view);
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onBindViewHolder(@NonNull final TransactionAdapter.ViewHolder holder, final int position) {
        final AccountLog log = logs.get(position);
        holder.tvTime.setText(sdf.format(log.getDate())+ " | "+log.getStan());
        holder.tvDescription.setText(log.getDescription());
        if (log.getCredit()>0){
            holder.tvPrice.setText(decimalFormat.format(log.getCredit()));
            holder.tvPrice.setTextColor(Color.parseColor("#2dbc61"));
        }else{
            holder.tvPrice.setText("-"+decimalFormat.format(log.getDebit()));
            holder.tvPrice.setTextColor(Color.parseColor("#fd5c63"));
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(log));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTime,tvDescription,tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<AccountLog> filterList = new ArrayList<>();
                for (int i = 0; i < mFilterList.size(); i++) {
                    if ((mFilterList.get(i).getStan().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mFilterList.get(i));
                    }else if ((mFilterList.get(i).getDescription().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mFilterList.size();
                results.values = mFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            logs = (List<AccountLog>) results.values;
            notifyDataSetChanged();
        }

    }
}
