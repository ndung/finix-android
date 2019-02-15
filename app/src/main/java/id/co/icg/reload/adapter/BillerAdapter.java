package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Biller;

public class BillerAdapter extends RecyclerView.Adapter<BillerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Biller model);
    }

    private final OnItemClickListener listener;

    public BillerAdapter(Context context, List<Biller> billers, OnItemClickListener listener) {
        this.context = context;
        this.billers = billers;
        this.listener = listener;
    }

    private Context context;
    private List<Biller> billers;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_other_menu, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Biller biller = billers.get(position);

        if (biller.getIcon()!=null) {
            Picasso.with(context).load(biller.getIcon()).into(holder.iv);
        }else{
            Picasso.with(context).load(R.drawable.image).into(holder.iv);
        }
        holder.tv.setText(biller.getDescription());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(biller);
        });
    }

    @Override
    public int getItemCount() {
        return billers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);

        }
    }
}
