package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Models.StaticBanners;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class ViewPagerStaticBannerAdapter extends RecyclerView.Adapter<ViewPagerStaticBannerAdapter.SellerTypeBannerViewHolder>
{
    private Context mContext;
    private ArrayList<StaticBanners> sellerTypeBanners;
    private ViewPager2 viewPager2;

    public ViewPagerStaticBannerAdapter(Context mContext, ArrayList<StaticBanners> sellerTypeBanners,ViewPager2 viewPager2) {
        this.mContext = mContext;
        this.sellerTypeBanners = sellerTypeBanners;
        this.viewPager2 = viewPager2;

    }

    @NonNull
    @Override
    public ViewPagerStaticBannerAdapter.SellerTypeBannerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_category_banner_viewpager, viewGroup, false);
        return new ViewPagerStaticBannerAdapter.SellerTypeBannerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewPagerStaticBannerAdapter.SellerTypeBannerViewHolder sellerTypeViewHolder, int position) {
        final StaticBanners currentItem = sellerTypeBanners.get(position);

        Glide.with(mContext).load(currentItem.getBanner_image())
                .into(sellerTypeViewHolder.iv_shop);
        if(position == sellerTypeBanners.size()-2)
        {
            viewPager2.post(runnable);
        }


    }

    @Override
    public int getItemCount() {
        return sellerTypeBanners.size();
    }

    public class SellerTypeBannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_shop;
        public TextView tv_tag,tv_name,tv_address;

        public SellerTypeBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_shop = itemView.findViewById(R.id.category_banner);

        }

    }

    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sellerTypeBanners.addAll(sellerTypeBanners);
            notifyDataSetChanged();
        }
    };
}
