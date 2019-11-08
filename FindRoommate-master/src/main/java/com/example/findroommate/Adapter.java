package com.example.findroommate;

import android.content.Context;
import android.net.Uri;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Adapter extends PagerAdapter {
    private List<ImagesPost> mModels;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<ImagesPost> models, Context context) {
        mModels = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_pager, container, false);
        ImageView imageView;
        imageView = view.findViewById(R.id.imageView_detail);
        Picasso.get().load(mModels.get(position).getImages()).into(imageView);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
