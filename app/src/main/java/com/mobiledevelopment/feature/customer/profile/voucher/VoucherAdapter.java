package com.mobiledevelopment.feature.customer.profile.voucher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.ItemVoucherBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VoucherAdapter extends ListAdapter<Voucher, VoucherAdapter.VoucherViewHolder> {

    private static final Map<Range<Long>, Integer> VOUCHER_VALUE_RANGE_AND_DRAWABLE = new HashMap<Range<Long>, Integer>() {{
        put(Range.create(0L, 34L), R.drawable.gradient_linear_blue);
        put(Range.create(35L, 69L), R.drawable.gradient_linear_orange);
        put(Range.create(70L, 100L), R.drawable.gradient_linear_red);
    }};

    private final OnClickListener itemClickListener;
    private final Context context;

    protected VoucherAdapter(Context context, OnClickListener itemClickListener) {
        super(new VoucherAdapter.VoucherDiffUtils());
        this.context = context;
        this.itemClickListener = itemClickListener;

    }

    @NonNull
    @Override
    public VoucherAdapter.VoucherViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new VoucherAdapter.VoucherViewHolder(ItemVoucherBinding.inflate
                (LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.VoucherViewHolder holder, int position) {
        Long voucherValue = getItem(position).getValue();

        holder.textViewValue.setText("Voucher " + voucherValue.toString() + "%");

        holder.textViewDateExpired.setText("Expires: " + StringFormatterUtils.format(
                getItem(position).getDateExpired()));

        holder.textViewDecorationValue.setText(voucherValue + "%");
        for (Map.Entry<Range<Long>, Integer> valueRangeAndDrawable : VOUCHER_VALUE_RANGE_AND_DRAWABLE.entrySet()) {
            if (valueRangeAndDrawable.getKey().contains(voucherValue)) {
                holder.textViewDecorationValue.setBackground(ContextCompat.getDrawable(
                        context,
                        valueRangeAndDrawable.getValue()));
            }
        }

        holder.itemWrapper.setOnClickListener((view ->
                itemClickListener.onWholeItemClick(getItem(position))));

        holder.buttonUse.setOnClickListener((view -> itemClickListener.onButtonUseClick()));
    }

    public interface OnClickListener {
        void onButtonUseClick();

        void onWholeItemClick(Voucher voucher);
    }

    protected static class VoucherViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewValue;
        public final TextView textViewDecorationValue;
        public final TextView textViewDateExpired;
        public final Button buttonUse;
        public final LinearLayout itemWrapper;

        public VoucherViewHolder(ItemVoucherBinding binding) {
            super(binding.getRoot());
            textViewValue = binding.textviewValue;
            textViewDateExpired = binding.textviewDateExpired;
            textViewDecorationValue = binding.textviewDecorationValue;
            buttonUse = binding.buttonUse;
            itemWrapper = binding.itemWrapper;
        }
    }

    private static class VoucherDiffUtils extends DiffUtil.ItemCallback<Voucher> {

        @Override
        public boolean areItemsTheSame(@NonNull Voucher oldItem, @NonNull Voucher newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Voucher oldItem, @NonNull Voucher newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getIdUser().equals(newItem.getIdUser()) &&
                    oldItem.getValue().equals(newItem.getValue());
        }
    }
}
