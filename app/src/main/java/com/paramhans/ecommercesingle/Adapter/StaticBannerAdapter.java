package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Models.StaticBanners;
import com.paramhans.ecommercesingle.R;


import java.util.ArrayList;

public class StaticBannerAdapter extends RecyclerView.Adapter<StaticBannerAdapter.SellerCategoryViewHolder> {
    private Context mContext;
    private ArrayList<StaticBanners> categories;

    public StaticBannerAdapter(Context mContext, ArrayList<StaticBanners> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @NonNull
    @Override
    public StaticBannerAdapter.SellerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_static, viewGroup, false);
        return new StaticBannerAdapter.SellerCategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final StaticBannerAdapter.SellerCategoryViewHolder categoryViewHolder, int position) {
        final StaticBanners currentItem = categories.get(position);
        Glide.with(mContext).load(currentItem.getBanner_image())
                .into(categoryViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class SellerCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;


        public SellerCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.category_banner);
        }

    }
}
