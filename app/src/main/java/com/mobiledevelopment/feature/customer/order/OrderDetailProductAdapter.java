package com.mobiledevelopment.feature.customer.order;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.CartOrderItemBinding;
import com.mobiledevelopment.databinding.ItemOrderDetailProductBinding;

import java.util.List;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.ViewHolder> {
    private List<Product.ProductInCart> list;
    private LayoutInflater layoutInflater;

    public OrderDetailProductAdapter(List<Product.ProductInCart> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public OrderDetailProductAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemOrderDetailProductBinding binding =
                ItemOrderDetailProductBinding.inflate(layoutInflater, parent, false);
        return new OrderDetailProductAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull OrderDetailProductAdapter.ViewHolder holder,
            int position) {
        Product.ProductInCart model = list.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public double getTotalCost() {
        double result = 0L;
        for (Product.ProductInCart cartReview : list) {
            result += cartReview.getProduct().getPrice() * cartReview.getQuantity();
        }
        return result;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderDetailProductBinding _binding;

        public ViewHolder(ItemOrderDetailProductBinding binding) {
            super(binding.getRoot());
            _binding = binding;
        }

        public void bind(Product.ProductInCart model) {
            _binding.productNameTextView.setText(model.getProduct().getName());
            _binding.productQuantityTextView.setText("x" + model.getQuantity());
            //_binding.productSalePriceTextView.setText(StringFormatterUtils.toCurrency((long) model.getProduct()
            //        .getPrice()));
            //_binding.costTextView.setText(StringFormatterUtils.toCurrency((long) model.getProduct()
            //        .getPrice() * model.getQuantity()));

            Glide.with(_binding.getRoot().getContext())
                    .load(model.getProduct().getImages().get(0))
                    .into(_binding.imageView);
        }


    }

}