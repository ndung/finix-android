package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.DecimalFormat;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private static final String TAG = ProductAdapter.class.toString();

    private Context context;
    private List<Product> products;

    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    public ProductAdapter(Context context, List<Product> products, OnItemClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        boolean onItemClick(Product model);
    }

    private final OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Product product = products.get(position);
        holder.tvId.setText(product.getId());
        holder.tvName.setText(product.getName());
        holder.tvDescription.setVisibility(View.GONE);
        if (product.getDescription()!=null && !product.getDescription().isEmpty()){
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(product.getDescription());
        }
        if (!product.isSelected()){
            holder.checkBox.setChecked(false);
        }
        holder.tvPrice.setText(decimalFormat.format(product.getBuyingPrice()));
        holder.checkBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    boolean bool = listener.onItemClick(product);
                    if (!bool) {
                        holder.checkBox.setChecked(false);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId,tvName,tvDescription,tvPrice;
        CustomCheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_c);
            tvId = itemView.findViewById(R.id.tv_id);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
