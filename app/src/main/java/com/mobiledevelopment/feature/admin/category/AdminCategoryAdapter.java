package com.mobiledevelopment.feature.admin.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.databinding.ItemAddCategoryBinding;
import com.mobiledevelopment.databinding.ItemCategoryBinding;

import java.util.Objects;

public class AdminCategoryAdapter extends ListAdapter<Category, RecyclerView.ViewHolder> {
    private final OnItemCategoryClickListener itemCategoryClickListener;
    private final OnItemAddCategoryClickListener itemAddCategoryClickListener;
    private final int VIEW_ADD_CATEGORY = 0;
    private final int VIEW_CATEGORY = 1;
    private final Context context;


    public AdminCategoryAdapter(Context context, OnItemCategoryClickListener itemCategoryClickListener, OnItemAddCategoryClickListener itemAddCategoryClickListener) {
        super(new CategoryDiffUtils());
        this.itemCategoryClickListener = itemCategoryClickListener;
        this.context = context;
        this.itemAddCategoryClickListener = itemAddCategoryClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_ADD_CATEGORY : VIEW_CATEGORY;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_ADD_CATEGORY:
                return new AddCategoryViewHolder(ItemAddCategoryBinding.inflate
                        (LayoutInflater.from(parent.getContext()), parent, false));
            case VIEW_CATEGORY:
                return new CategoryViewHolder(ItemCategoryBinding.inflate
                        (LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_ADD_CATEGORY:
                AddCategoryViewHolder addCategoryViewHolder = (AddCategoryViewHolder) holder;
                addCategoryViewHolder.itemWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemAddCategoryClickListener.onClick();
                    }
                });
                break;
            case VIEW_CATEGORY:
                Category category = getItem(position - 1);
                CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
                categoryViewHolder.txtCategory.setText(category.getName());
                Glide
                        .with(context)
                        .load(category.getImage())
                        .centerCrop()
                        .into(categoryViewHolder.imgCategory);
                categoryViewHolder.itemWrapper.setOnClickListener((view -> itemCategoryClickListener.onClick(category.getId())));
                break;
        }
    }

    public interface OnItemCategoryClickListener {
        void onClick(String idCategory);
    }

    public interface OnItemAddCategoryClickListener {
        void onClick();
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

    public class AddCategoryViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout itemWrapper;
        public AddCategoryViewHolder(ItemAddCategoryBinding binding) {
            super(binding.getRoot());
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

