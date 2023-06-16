package com.mobiledevelopment.feature.customer.order;


import static com.mobiledevelopment.core.utils.StringFormatterUtils.format;
import static com.mobiledevelopment.core.utils.StringFormatterUtils.toCurrency;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.databinding.ItemOrderBinding;

import java.util.Objects;

class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder> {

    private final OnItemClickListener itemClickListener;
    private final Context context;

    protected OrderAdapter(Context context, OnItemClickListener itemClickListener) {
        super(new OrderAdapter.OrderDiffUtils());
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new com.mobiledevelopment.feature.customer.order.OrderAdapter.OrderViewHolder(
                ItemOrderBinding.inflate
                        (LayoutInflater.from(parent.getContext()), parent, false));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(
            @NonNull OrderViewHolder holder,
            int position) {
        holder.textviewOrderId.setText(getItem(position).getId());
        holder.textviewTotalPayment.setText(toCurrency(getItem(position).getTotalCost()));
        holder.textviewStatus.setText(getItem(position).getStatus());
        holder.textviewCreatedAt.setText(format(getItem(position).getCreatedAt()));
        holder.textviewAddress.setText(getItem(position).getAddress());

        holder.itemWrapper.setOnClickListener((view -> itemClickListener.onClick(getItem(position))));

        String currentStatusAsString = getItem(position).getStatus();

        Order.OrderStatus currentStatusAsWrapper = Order.OrderStatus.findByFieldValue(
                currentStatusAsString);

        assert currentStatusAsWrapper != null;

        holder.textviewStatus.setTextColor(ContextCompat.getColor(
                context,
                currentStatusAsWrapper.textColorResId));
        holder.textviewStatus.setBackgroundColor(ContextCompat.getColor(
                context,
                currentStatusAsWrapper.backgroundColorResId));

    }

    public interface OnItemClickListener {
        void onClick(Order order);
    }

    protected static class OrderViewHolder extends RecyclerView.ViewHolder {
        public final TextView textviewStatus;
        public final TextView textviewCreatedAt;
        public final TextView textviewOrderId;
        public final TextView textviewAddress;
        public final TextView textviewTotalPayment;
        public final LinearLayout itemWrapper;

        public OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            textviewStatus = binding.textviewStatus;
            textviewCreatedAt = binding.textviewCreatedAt;
            textviewOrderId = binding.textviewOrderId;
            textviewAddress = binding.textviewAddress;
            textviewTotalPayment = binding.textviewTotalPayment;
            itemWrapper = binding.itemWrapper;
        }
    }

    private static class OrderDiffUtils extends DiffUtil.ItemCallback<Order> {

        @Override
        public boolean areItemsTheSame(
                @NonNull Order oldItem, @NonNull Order newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getIdUser().equals(newItem.getIdUser()) &&
                    oldItem.getAmountOfProducts().equals(newItem.getAmountOfProducts()) &&
                    oldItem.getAddress().equals(newItem.getAddress()) &&
                    oldItem.getStatus().equals(newItem.getStatus());
        }
    }
}


