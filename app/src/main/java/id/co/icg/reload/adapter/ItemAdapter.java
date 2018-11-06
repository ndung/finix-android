package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.co.icg.reload.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    private Context context;
    private List<String> items;

    public interface OnItemSelectedListener {
        void onItemSelected(String item);
    }

    private OnItemSelectedListener onItemSelectedListener;

    public ItemAdapter(Context context, List<String> items, OnItemSelectedListener onItemSelectedListener){
        this.context = context;
        this.items = items;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent,false);

        return new ItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        String item = items.get(position);
        holder.tvItem.setText(item);
        holder.itemView.setOnClickListener(v -> onItemSelectedListener.onItemSelected(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHolder(View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.tv_item);
        }
    }
}
