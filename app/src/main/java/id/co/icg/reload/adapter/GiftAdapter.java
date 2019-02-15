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
import id.co.icg.reload.model.Gift;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.Preferences;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(Gift gift);
    }

    private final OnItemClickListener listener;

    private Context context;
    private List<Gift> list;

    public GiftAdapter(Context context, List<Gift> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public GiftAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gift, parent, false);
        return new GiftAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Gift gift = list.get(position);

        if (gift.getImage()!=null) {
            Picasso.with(context).load(gift.getImage()).into(holder.ivGift);
        }else{
            Picasso.with(context).load(R.drawable.image).into(holder.ivGift);
        }

        holder.tvGift.setText(gift.getName());
        holder.tvPts.setText(String.valueOf(gift.getPts())+" poin");

        Reseller user = Preferences.getUser(context);
        if (user.getPoints()>=gift.getPts()) {
            holder.ivLock.setImageResource(R.drawable.ic_unlock);
            holder.itemView.setOnClickListener(view -> listener.onItemClick(gift));
        }else{
            holder.ivLock.setImageResource(R.drawable.ic_lock);
        }
    }

    public void add(Gift gift, int position) {
        list.add(position, gift);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivGift;
        TextView tvGift;
        TextView tvPts;
        ImageView ivLock;

        public ViewHolder(View itemView) {
            super(itemView);
            ivGift = itemView.findViewById(R.id.iv_gift);
            tvGift = itemView.findViewById(R.id.tv_gift);
            tvPts = itemView.findViewById(R.id.tv_pts);
            ivLock = itemView.findViewById(R.id.iv_lock);
        }
    }
}