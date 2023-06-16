package com.mobiledevelopment.feature.customer.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.databinding.ItemCategoryBinding;

import java.util.Objects;

public class CategoryAdapter extends ListAdapter<Category, RecyclerView.ViewHolder> {
    private final OnItemCategoryClickListener itemCategoryClickListener;
    private final Context context;


    public CategoryAdapter(Context context, OnItemCategoryClickListener itemCategoryClickListener) {
        super(new CategoryDiffUtils());
        this.itemCategoryClickListener = itemCategoryClickListener;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(ItemCategoryBinding.inflate
                (LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Category category = getItem(position);
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        categoryViewHolder.txtCategory.setText(category.getName());
        Glide
                .with(context)
                .load(category.getImage())
                .centerCrop()
                .into(categoryViewHolder.imgCategory);
        categoryViewHolder.itemWrapper.setOnClickListener((view -> itemCategoryClickListener.onClick(category.getId())));
    }

    public interface OnItemCategoryClickListener {
        void onClick(String idCategory);
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCategory;
        private ShapeableImageView imgCategory;
        private LinearLayout itemWrapper;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            txtCategory = binding.txtCategory;
            imgCategory = binding.imgCategory;
            itemWrapper = binding.itemWrapper;
        }
    }

    private static class CategoryDiffUtils extends DiffUtil.ItemCallback<Category> {

        @Override
        public boolean areItemsTheSame(
                @NonNull Category oldItem, @NonNull Category newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getImage().equals(newItem.getImage());
        }
    }
}
