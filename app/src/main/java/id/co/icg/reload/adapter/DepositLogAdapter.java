package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Deposit;

public class DepositLogAdapter extends RecyclerView.Adapter<DepositLogAdapter.ViewHolder> {

    private Context context;
    private List<Deposit> list;

    public DepositLogAdapter(Context context, List<Deposit> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_deposit_log, parent, false);
        return new ViewHolder(view);
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Deposit log = list.get(position);
        holder.tvStan.setText("");
        String amount = "+"+decimalFormat.format(log.getCredit());
        if (log.getStan()!=null) {
            holder.tvStan.setText(log.getStan());
            if (log.getStan().startsWith("R")){
                amount = "-"+decimalFormat.format(log.getDebit());
            }
        }

        if (log.getDescription().contains("BCA")) {
            Picasso.with(context).load(R.drawable.bca).into(holder.ivBank);
        }else if (log.getDescription().contains("BNI")) {
            Picasso.with(context).load(R.drawable.bni).into(holder.ivBank);
        }else if (log.getDescription().contains("BRI")) {
            Picasso.with(context).load(R.drawable.bri).into(holder.ivBank);
        }else if (log.getDescription().contains("MANDIRI")) {
            Picasso.with(context).load(R.drawable.bank_mandiri).into(holder.ivBank);
        }else{
            Picasso.with(context).load(R.drawable.image).into(holder.ivBank);
        }

        holder.tvAmount.setText(amount);

        holder.tvDescription.setText(log.getDescription());

        if (log.getDescription()!=null && log.getDescription().contains("]")){
            holder.tvDescription.setText(log.getDescription().substring(log.getDescription().indexOf("]")+1).trim());
        }
        holder.tvTimestamp.setText(sdf.format(log.getDatetime()));

        if (log.getStatus()==0){
            holder.tvStatus.setText("Menunggu");
        }else if (log.getStatus()==1){
            holder.tvStatus.setText("Berhasil");
        }else{
            holder.tvStatus.setText("Gagal");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBank;
        TextView tvStan;
        TextView tvAmount;
        TextView tvTimestamp;
        TextView tvDescription;
        TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBank = itemView.findViewById(R.id.iv_bank);
            tvStan = itemView.findViewById(R.id.tv_stan);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvTimestamp = itemView.findViewById(R.id.tv_time_stamp);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
