package com.mobiledevelopment.feature.admin.product;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.ItemProductBinding;

import java.util.Objects;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {
    private final OnItemProductClickListener itemProductClickListener;
    private final Context context;


    public ProductAdapter(Context context, OnItemProductClickListener itemProductClickListener) {
        super(new ProductDiffUtils());
        this.itemProductClickListener = itemProductClickListener;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(ItemProductBinding.inflate
                (LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = getItem(position);

        if(product == null) return;

        Glide
                .with(context)
                .load(product.getImages().get(0))
                .centerCrop()
                .into(holder.imgProduct);

        holder.txtName.setText(product.getName());
        holder.txtQuantity.setText(product.getUnit());
        holder.txtPrice.setText(StringFormatterUtils.toCurrency(product.getPrice()));

        if(product.getDiscount() != 0){
            holder.txtDiscountPrice.setText(StringFormatterUtils.toCurrency(product.getPrice() * (100 - product.getDiscount()) * 0.01));
            holder.txtPrice.setTextColor(Color.parseColor("#000000"));
            holder.txtPrice.setVisibility(View.VISIBLE);
            holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.txtPrice.setTextColor(Color.parseColor("#FFB930"));
            holder.txtDiscountPrice.setVisibility(View.GONE);
        }

        holder.wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemProductClickListener.onClick(product.getId());
            }
        });
    }


    public interface OnItemProductClickListener {
        void onClick(String idProduct);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtPrice;
        private TextView txtDiscountPrice;
        private TextView txtQuantity;
        private ImageView imgProduct;
        private LinearLayout wrapper;

        public ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            txtName = binding.txtName;
            txtPrice = binding.txtPrice;
            txtQuantity = binding.txtQuantity;
            imgProduct = binding.imgProduct;
            txtDiscountPrice = binding.txtDiscountPrice;
            wrapper = binding.wrapper;
        }
    }

    private static class ProductDiffUtils extends DiffUtil.ItemCallback<Product> {
        @Override
        public boolean areItemsTheSame(
                @NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getName().equals(newItem.getName())
                    ;
        }
    }
}