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

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Activities.SubCategoryActivity;
import com.paramhans.ecommercesingle.Models.HomeSubCat;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class HomeSubcategoryAdapter extends RecyclerView.Adapter<HomeSubcategoryAdapter.SellerCategoryViewHolder>
{
    private Context mContext;
    private ArrayList<HomeSubCat> categories;

    public HomeSubcategoryAdapter(Context mContext, ArrayList<HomeSubCat> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @NonNull
    @Override
    public HomeSubcategoryAdapter.SellerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_home_subcat, viewGroup, false);
        return new HomeSubcategoryAdapter.SellerCategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeSubcategoryAdapter.SellerCategoryViewHolder categoryViewHolder, int position) {
        final HomeSubCat currentItem = categories.get(position);

        categoryViewHolder.tv_name.setText(currentItem.getSubcat_name());
        Glide.with(mContext).load(currentItem.getSubcat_image())
                .into(categoryViewHolder.iv_cat);

        categoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SubCategoryActivity.class);
                intent.putExtra("subcat_id",currentItem.getSubcat_id());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class SellerCategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public ImageView iv_cat;

        public SellerCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_cat = itemView.findViewById(R.id.iv_cat);

        }

    }
}
