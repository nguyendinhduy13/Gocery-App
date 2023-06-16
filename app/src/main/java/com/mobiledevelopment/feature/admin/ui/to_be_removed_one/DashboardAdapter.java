package com.mobiledevelopment.feature.admin.ui.to_be_removed_one;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.TransactionItemBinding;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Dashboard;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.to_be_removed_three.TransactionDetailBottomSheet;
import com.mobiledevelopment.feature.customer.order.OrderDetailBottomSheet;

import java.util.List;
import java.util.Objects;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private List<Dashboard> list;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnClickItemMenuTransaction onClickItemMenuTransaction;
    private OnTransactionItemClickListener onTransactionClick;

    public DashboardAdapter(List<Dashboard> list) {
        this.list = list;
    }

    public DashboardAdapter(
            List<Dashboard> list,
            Context context,
            OnClickItemMenuTransaction onClickItemMenuTransaction,
            OnTransactionItemClickListener onTransactionClick) {
        this.list = list;
        this.context = context;
        this.onClickItemMenuTransaction = onClickItemMenuTransaction;
        this.onTransactionClick = onTransactionClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        TransactionItemBinding binding =
                TransactionItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dashboard transaction = list.get(position);

        holder.binding.transactionIDTextView.setText(transaction.getId());
        holder.binding.totalPaymentTextView.setText(StringFormatterUtils.toCurrency(transaction.getTotalPayment()));
        holder.binding.UserNameTextView.setText(transaction.getUserName());
        holder.binding.textViewDate.setText(transaction.getDate());
        holder.binding.textViewState.setText(transaction.getStatus());
        holder.binding.itemWrapper.setOnClickListener(view -> {
            onTransactionClick.onClick(transaction);
        });


        holder.binding.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.btnFinished:
                                onClickItemMenuTransaction.onClick(transaction, "Completed");
                                return true;
                            case R.id.btnCancelled:
                                onClickItemMenuTransaction.onClick(transaction, "Cancelled");
                                return true;
                            default:
                                onClickItemMenuTransaction.onClick(transaction, "In Progress");
                                return true;
                        }
                    }
                });

                popupMenu.inflate(R.menu.transaction_menu);
                popupMenu.show();
            }
        });

        if (Objects.equals(transaction.getStatus(), "In progress")) {
            holder.binding.textViewState.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.in_progress_text));
            holder.binding.textViewState.setBackgroundColor(context.getResources()
                    .getColor(R.color.in_progress_background));
        }
        if (Objects.equals(transaction.getStatus(), "Cancelled")) {
            holder.binding.textViewState.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.cancelled_text));
            holder.binding.textViewState.setBackgroundColor(context.getResources()
                    .getColor(R.color.cancelled_background));
        }
        if (Objects.equals(transaction.getStatus(), "Waiting for Payment")) {
            holder.binding.textViewState.setBackgroundColor(ContextCompat.getColor(
                    holder.binding.textViewState.getContext(),
                    R.color.waiting_payment_background));
            holder.binding.textViewState.setTextColor(ContextCompat.getColor(
                    context,
                    R.color.waiting_payment_text));

        }
        if (Objects.equals(transaction.getStatus(), "Finished")) {
            holder.binding.textViewState.setTextColor(ContextCompat.getColor(
                    holder.binding.textViewState.getContext(),
                    R.color.completed_text));
            holder.binding.textViewState.setBackgroundColor(ContextCompat.getColor(
                    holder.binding.textViewState.getContext(),
                    R.color.completed_background));
        }
    }

    public interface OnClickItemMenuTransaction {
        public void onClick(Dashboard transaction, String status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TransactionItemBinding binding;

        public ViewHolder(TransactionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}

interface OnTransactionItemClickListener {
    void onClick(Dashboard transaction);
}
