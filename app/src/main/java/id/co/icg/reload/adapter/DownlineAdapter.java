package id.co.icg.reload.adapter;

import android.content.Context;
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
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Reseller;

public class DownlineAdapter extends RecyclerView.Adapter<DownlineAdapter.ViewHolder> implements Filterable{

    private Context context;
    private List<Reseller> downlines;
    private List<Reseller> mFilterList = new ArrayList<>();
    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private ValueFilter valueFilter;

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public interface OnItemSelectedListener {
        void onTransfer(Reseller reseller);
        void onDelete(Reseller reseller);
    }

    private final DownlineAdapter.OnItemSelectedListener listener;

    public DownlineAdapter(Context context, List<Reseller> downlines, DownlineAdapter.OnItemSelectedListener listener) {
        this.context = context;
        this.downlines = downlines;
        this.mFilterList = downlines;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_downline, parent,false);

        return new DownlineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Reseller rs = downlines.get(position);
        String name = rs.getName();
        String account = rs.getId();
        String initial = "0";
        if (name!=null){
            initial = name.substring(0,1);
            account = account+" ("+name+")";
        }
        holder.tvAccount.setText(account);
        holder.tvInitial.setText(initial);
        holder.tvBalance.setText("Saldo : "+decimalFormat.format(rs.getBalance()));

        String status = sdf.format(rs.getJoinedDate());
        if (rs.getStatus()==-1){
            status = "Belum  aktif, terdaftar "+status;
        }else if (rs.getStatus()==-1){
            status = "Status aktif, terdaftar "+status;
        }else{
            status = "Tidak  aktif, terdaftar "+status;
        }
        holder.tvStatus.setText(status);

        holder.ivTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTransfer(rs);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(rs);
            }
        });
    }

    @Override
    public int getItemCount() {
        return downlines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTransfer, ivDelete;
        TextView tvInitial,tvAccount,tvBalance,tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            ivTransfer = itemView.findViewById(R.id.iv_transfer);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            tvInitial = itemView.findViewById(R.id.tv_initial);
            tvAccount = itemView.findViewById(R.id.tv_account);
            tvBalance = itemView.findViewById(R.id.tv_balance);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Reseller> filterList = new ArrayList<>();
                for (int i = 0; i < mFilterList.size(); i++) {
                    if ((mFilterList.get(i).getId().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mFilterList.get(i));
                    }else if ((mFilterList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
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
            downlines = (List<Reseller>) results.values;
            notifyDataSetChanged();
        }

    }
}
