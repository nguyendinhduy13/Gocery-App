package com.mobiledevelopment.feature.admin.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.databinding.ItemAddImageBinding;
import com.mobiledevelopment.databinding.ItemImageBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageAdapter extends ListAdapter<Uri, RecyclerView.ViewHolder> {
    private final int VIEW_ADD_IMAGE = 0;
    private OnItemAddImageClickListener itemAddImageClickListener;
    private OnItemRemoveImageClickListener itemRemoveImageClickListener;
    private final int VIEW_IMAGE = 1;
    private final Context context;

    public ImageAdapter(Context context, OnItemRemoveImageClickListener itemRemoveImageClickListener, OnItemAddImageClickListener itemAddImageClickListener) {
        super(new ImageDiffUtils());
        this.itemAddImageClickListener = itemAddImageClickListener;
        this.context = context;
        this.itemRemoveImageClickListener = itemRemoveImageClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_ADD_IMAGE : VIEW_IMAGE;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_ADD_IMAGE:
                return new AddImageViewHolder(ItemAddImageBinding.inflate
                        (LayoutInflater.from(parent.getContext()), parent, false));
            case VIEW_IMAGE:
                return new ImageViewHolder(ItemImageBinding.inflate
                        (LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        switch (getItemViewType(position)) {
            case VIEW_ADD_IMAGE:
                AddImageViewHolder addImageViewHolder = (AddImageViewHolder) holder;
                addImageViewHolder.wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemAddImageClickListener.onClick();
                    }
                });
                break;
            case VIEW_IMAGE:
                Uri uri = getItem(position - 1);

                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;

                try {
                    Glide.with(context)
                            .load(uri) // Uri of the picture
                            .into(imageViewHolder.imgProduct);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                imageViewHolder.btnRemoveImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemRemoveImageClickListener.onClick(position);
                    }
                });

                break;
        }
    }

    public void removeIndex(int index){
        List<Uri> uris = new ArrayList<>(getCurrentList());

        uris.remove(index - 1);

        submitList(null);
        submitList(uris);
    }

    public interface OnItemRemoveImageClickListener {
        void onClick(int index);
    }

    public interface OnItemAddImageClickListener {
        void onClick();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView imgProduct;
        private ImageView btnRemoveImage;

        public ImageViewHolder(ItemImageBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgCategory;
            btnRemoveImage = binding.btnRemoveImage;
        }
    }

    public class AddImageViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout wrapper;

        public AddImageViewHolder(ItemAddImageBinding binding) {
            super(binding.getRoot());
            wrapper = binding.wrapper;
        }
    }

    private static class ImageDiffUtils extends DiffUtil.ItemCallback<Uri> {

        @Override
        public boolean areItemsTheSame(
                @NonNull Uri oldItem, @NonNull Uri newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Uri oldItem, @NonNull Uri newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }
}
