package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.RedeemLog;

public class RedeemLogAdapter extends RecyclerView.Adapter<RedeemLogAdapter.ViewHolder> {

    private Context context;
    private List<RedeemLog> list;

    public RedeemLogAdapter(Context context, List<RedeemLog> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_redeem_log, parent, false);
        return new ViewHolder(view);
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final RedeemLog log = list.get(position);

        if (log.getGift().getImage()!=null) {
            Picasso.with(context).load(log.getGift().getImage()).into(holder.ivGift);
        }else{
            Picasso.with(context).load(R.drawable.image).into(holder.ivGift);
        }

        holder.tvGift.setText(log.getGift().getName());
        holder.tvPts.setText(String.valueOf(log.getGift().getPts())+" poin");
        holder.tvDescription.setText(log.getDescription());
        holder.tvTimestamp.setText(sdf.format(log.getTimestamp()));

        if (log.getStatus()==0){
            holder.tvStatus.setText("Sedang diproses");
        }else if (log.getStatus()==1){
            holder.tvStatus.setText("Sudah diproses");
        }else{
            holder.tvStatus.setText("Tidak diproses");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivGift;
        TextView tvGift;
        TextView tvPts;
        TextView tvTimestamp;
        TextView tvDescription;
        TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivGift = itemView.findViewById(R.id.iv_gift);
            tvGift = itemView.findViewById(R.id.tv_gift);
            tvPts = itemView.findViewById(R.id.tv_pts);
            tvTimestamp = itemView.findViewById(R.id.tv_time_stamp);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
