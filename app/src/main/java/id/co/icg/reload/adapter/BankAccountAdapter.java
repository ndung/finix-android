package id.co.icg.reload.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.BankAccount;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.ViewHolder>{

    private Context context;
    private List<BankAccount> bankAccounts;

    public interface OnItemSelectedListener {
        void onItemSelected(BankAccount model);
    }

    private final OnItemSelectedListener listener;

    public BankAccountAdapter(Context context, List<BankAccount> bankAccounts, BankAccountAdapter.OnItemSelectedListener listener) {
        this.context = context;
        this.bankAccounts = bankAccounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BankAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bank_account, parent,false);

        return new BankAccountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankAccountAdapter.ViewHolder holder, final int position) {
        final BankAccount bankAccount = bankAccounts.get(position);
        if (bankAccount.getBankName().equalsIgnoreCase("BCA")){
            Picasso.with(context).load(R.drawable.bca).into(holder.ivBank);
        }else if (bankAccount.getBankName().equalsIgnoreCase("BNI")){
            Picasso.with(context).load(R.drawable.bni).into(holder.ivBank);
        }else if (bankAccount.getBankName().equalsIgnoreCase("BRI")){
            Picasso.with(context).load(R.drawable.bri).into(holder.ivBank);
        }else{
            Picasso.with(context).load(R.drawable.bank_mandiri).into(holder.ivBank);
        }
        holder.tvAccountNo.setText(bankAccount.getAccountNumber());
        holder.tvAccountName.setText(bankAccount.getAccountName());
        if (!bankAccount.isSelected()){
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    listener.onItemSelected(bankAccount);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBank;
        TextView tvAccountNo,tvAccountName;
        CustomCheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);

            ivBank = itemView.findViewById(R.id.iv_bank);
            tvAccountNo = itemView.findViewById(R.id.tv_account_no);
            tvAccountName = itemView.findViewById(R.id.tv_account_name);
            checkBox = itemView.findViewById(R.id.cb_c);
        }
    }
}
