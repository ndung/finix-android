package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Product;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> implements Filterable{

    public PriceAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.mFilterList = products;
    }

    private Context context;
    private List<Product> products;
    private List<Product> mFilterList;
    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");
    private ValueFilter valueFilter;

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_price, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product product = products.get(position);
        if (product.getIcon()!=null) {
            Picasso.with(context).load(product.getIcon()).into(holder.ivIcon);
        }else{
            Picasso.with(context).load(R.drawable.image).into(holder.ivIcon);
        }
        holder.tvId.setText(product.getId());
        if (product.getName()!=null) {
            holder.tvName.setText(product.getName());
        }else{
            holder.tvName.setText("");
        }
        holder.tvPts.setText(product.getPoin()+" poin");
        if (product.getCashback()!=null && product.getCashback()>0 ) {
            holder.tvCashback.setText("Cashback "+decimalFormat.format(product.getCashback()));
        }else{
            holder.tvCashback.setText("");
        }
        if (product.getCategory()==3){
            holder.tvPrice.setText("Admin fee "+decimalFormat.format(product.getBuyingPrice()));
        }else{
            holder.tvPrice.setText(decimalFormat.format(product.getBuyingPrice()));
        }
        if (product.getDescription()!=null) {
            holder.tvDescription.setText(product.getDescription());
        }else{
            holder.tvDescription.setText(" ");
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvId,tvName,tvDescription,tvPrice,tvCashback,tvPts;
        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvId = itemView.findViewById(R.id.tv_product_id);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDescription = itemView.findViewById(R.id.tv_product_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCashback = itemView.findViewById(R.id.tv_cashback);
            tvPts = itemView.findViewById(R.id.tv_poin);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Product> filterList = new ArrayList<>();
                for (int i = 0; i < mFilterList.size(); i++) {
                    if ((mFilterList.get(i).getId().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mFilterList.get(i));
                    }else if ((mFilterList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
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
            products = (List<Product>) results.values;
            notifyDataSetChanged();
        }

    }
}
