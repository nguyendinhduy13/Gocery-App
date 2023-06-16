package com.mobiledevelopment.feature.customer.product.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.CategoryDetail;
import com.mobiledevelopment.databinding.CategoryDetailItemBinding;

import java.util.List;

public class CategoryDetailAdapter extends RecyclerView.Adapter<CategoryDetailAdapter.ViewHolder> {
    private List<CategoryDetail> list;
    private LayoutInflater layoutInflater;

    public CategoryDetailAdapter(List<CategoryDetail> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        CategoryDetailItemBinding binding =
                CategoryDetailItemBinding.inflate(layoutInflater,  parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryDetail model = list.get(position);
        holder.binding.productImage.setImageResource(R.drawable.arrow_back);
        holder.binding.productName.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CategoryDetailItemBinding binding;
        public ViewHolder(CategoryDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
