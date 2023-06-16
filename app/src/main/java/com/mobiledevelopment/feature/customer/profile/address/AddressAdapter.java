package com.mobiledevelopment.feature.customer.profile.address;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.address.Address;
import com.mobiledevelopment.databinding.ItemAddressBinding;

import java.util.Objects;

class AddressAdapter extends ListAdapter<Address, AddressAdapter.AddressViewHolder> {

    private final OnItemClickListener itemClickListener;

    private final Context context;

    protected AddressAdapter(Context context, OnItemClickListener itemClickListener) {
        super(new AddressDiffUtils());
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new AddressViewHolder(ItemAddressBinding.inflate
                (LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.AddressViewHolder holder, int position) {
        holder.textViewLabel.setText(getItem(position).getLabel());
        holder.textViewAddressDetail.setText(getItem(position).getDetailAddress());
        holder.itemWrapper.setOnClickListener((view -> itemClickListener.onClick(getItem(position).getId())));

        if (getItem(position).isPrimary()) {
            holder.textviewPrimaryIndicator.setVisibility(View.VISIBLE);
            holder.layoutOuterFrame.setBackground(AppCompatResources.getDrawable(
                    context,
                    R.drawable.border_all_thin_green));

            Log.d("AddressAdapter", "onBindViewHolder()getItem(position): " + getItem(position));
        } else {
            holder.textviewPrimaryIndicator.setVisibility(View.GONE);
            holder.layoutOuterFrame.setBackground(null);
            Log.d("AddressAdapter", "onBindViewHolder()getItem(position): " + getItem(position));
        }
    }

    public interface OnItemClickListener {
        void onClick(String addressId);
    }

    protected static class AddressViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewLabel;
        public final TextView textViewAddressDetail;
        public final LinearLayout itemWrapper;

        public final FrameLayout layoutOuterFrame;

        public final TextView textviewPrimaryIndicator;

        public AddressViewHolder(ItemAddressBinding binding) {
            super(binding.getRoot());
            textViewLabel = binding.textviewLabel;
            textViewAddressDetail = binding.textviewAddressDetail;
            itemWrapper = binding.itemWrapper;
            layoutOuterFrame = binding.layoutOuterFrame;
            textviewPrimaryIndicator = binding.textviewPrimaryIndicator;
        }
    }

    private static class AddressDiffUtils extends DiffUtil.ItemCallback<Address> {

        @Override
        public boolean areItemsTheSame(
                @NonNull Address oldItem, @NonNull Address newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Address oldItem, @NonNull Address newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getIdUser().equals(newItem.getIdUser()) &&
                    oldItem.getDetailAddress().equals(newItem.getDetailAddress()) &&
                    oldItem.getLabel().equals(newItem.getLabel()) &&
                    oldItem.isPrimary().equals(newItem.isPrimary());
        }
    }
}

