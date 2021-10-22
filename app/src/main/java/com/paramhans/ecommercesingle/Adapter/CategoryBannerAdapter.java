package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Activities.CategoryActivity;
import com.paramhans.ecommercesingle.Models.CategoryBanner;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class CategoryBannerAdapter extends RecyclerView.Adapter<CategoryBannerAdapter.SellerTypeBannerViewHolder>
{
    private Context mContext;
    private ArrayList<CategoryBanner> sellerTypeBanners;
    private ViewPager2 viewPager2;

    public CategoryBannerAdapter(Context mContext, ArrayList<CategoryBanner> sellerTypeBanners,ViewPager2 viewPager2) {
        this.mContext = mContext;
        this.sellerTypeBanners = sellerTypeBanners;
        this.viewPager2 = viewPager2;

    }

    @NonNull
    @Override
    public CategoryBannerAdapter.SellerTypeBannerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_category_banner_viewpager, viewGroup, false);
        return new CategoryBannerAdapter.SellerTypeBannerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryBannerAdapter.SellerTypeBannerViewHolder sellerTypeViewHolder, int position) {
        final CategoryBanner currentItem = sellerTypeBanners.get(position);

        Glide.with(mContext).load(currentItem.getImage())
                .into(sellerTypeViewHolder.iv_shop);
        if(position == sellerTypeBanners.size()-2)
        {
            viewPager2.post(runnable);
        }

        sellerTypeViewHolder.iv_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra("cat_id",currentItem.getCat_id());
                mContext.startActivity(intent);
            }
        });

//
//        sellerTypeViewHolder.lin_shop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(mContext, ShopsActivity.class);
////                intent.putExtra(Extras.seller_type_id,currentItem.getSeller_type_id());
////                intent.putExtra(Extras.seller_type_icon,currentItem.getIcon());
////                intent.putExtra(Extras.seller_type_banner,currentItem.getBanner_image());
////                intent.putExtra(Extras.seller_type_name,currentItem.getSellet_category());
////                mContext.startActivity(intent);
//            }
//        });

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
