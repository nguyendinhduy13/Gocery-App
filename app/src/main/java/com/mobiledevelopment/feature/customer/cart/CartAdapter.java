package com.mobiledevelopment.feature.customer.cart;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Cart;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.ItemCartBinding;

import java.util.List;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final Context context;
    private List<Product.ProductInCart> list;
    private OnButtonCartAdapterClick listener;
    public CartAdapter(Context context,
                       List<Product.ProductInCart> list,
                       OnButtonCartAdapterClick listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(ItemCartBinding.inflate
                (LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private ItemCartBinding binding;

        public CartViewHolder(@NonNull ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product.ProductInCart item)
        {
            binding.txtName.setText(item.getProduct().getName());
            binding.txtQuantity.setText(item.getQuantity() + "");
            binding.txtPrice.setText(StringFormatterUtils.toCurrency(item.getProduct().getPrice()));
            binding.txtUnit.setText(item.getProduct().getUnit());
            Glide.with(binding.getRoot().getContext())
                    .load(item.getProduct().getImages().get(0))
                    .into(binding.imgProduct);

            binding.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(binding.txtQuantity.getText().toString());

                    if(quantity > 1){
                        quantity-=1;
                        binding.txtQuantity.setText(Integer.toString(quantity));
                        listener.onClick(quantity, item.getId());
                    }else{
                        binding.btnMinus.setColorFilter(context.getResources().getColor(R.color.gray_light));
                    }
                }
            });

            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnMinus.setColorFilter(context.getResources().getColor(R.color.green_bright));
                    int quantity = Integer.parseInt(binding.txtQuantity.getText().toString());
                    quantity+=1;
                    binding.txtQuantity.setText(Integer.toString(quantity));
                    listener.onClick(quantity, item.getId());
                }
            });
        }
    }

    private static class CartDiffUtils extends DiffUtil.ItemCallback<Cart> {

        @Override
        public boolean areItemsTheSame(
                @NonNull Cart oldItem, @NonNull Cart newItem) {
            return Objects.equals(oldItem.getIdCart(), newItem.getIdCart());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Cart oldItem, @NonNull Cart newItem) {
            return oldItem.getIdCart().equals(newItem.getIdCart()) &&
                    oldItem.getIdUser().equals(newItem.getIdUser()) &&
                    oldItem.getCart().equals(newItem.getCart());
        }
    }

    public interface OnButtonCartAdapterClick
    {
        void onClick(int quantity, String id);
    }
}

