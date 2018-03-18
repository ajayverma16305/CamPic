package com.androidteam.campic.MainModule.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidteam.campic.Helper.TouchImageView;
import com.androidteam.campic.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class PreviewAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Uri> photoUrisList = new ArrayList<>();

    public PreviewAdapter(Context context, ArrayList<Uri> photoUrisList) {
        this.context = context;
        this.photoUrisList = photoUrisList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.preview_current_image_view, container, false);

        TouchImageView imageView = (TouchImageView) view.findViewById(R.id.currentImageView);
        Glide.with(context)
                .load(photoUrisList.get(position))
                .skipMemoryCache(true)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate().into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return photoUrisList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}