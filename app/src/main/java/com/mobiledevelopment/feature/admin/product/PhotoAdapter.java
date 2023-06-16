package com.mobiledevelopment.feature.admin.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.R;

import java.util.List;

public class PhotoAdapter extends PagerAdapter {
    private Context context;
    private List<String> urls;

    public PhotoAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_photo, container, false);
        ImageView img = view.findViewById(R.id.imgPhoto);

        String url = urls.get(position);

        if(!url.isEmpty())
            Glide.with(context).load(url).into(img);

        // add view to view group
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        if (urls != null) {
            return urls.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
