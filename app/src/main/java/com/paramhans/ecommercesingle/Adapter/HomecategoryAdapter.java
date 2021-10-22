package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Interfaces.CategoryItemClick;
import com.paramhans.ecommercesingle.Models.HomeCategory;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class HomecategoryAdapter  extends RecyclerView.Adapter<HomecategoryAdapter.SellerCategoryViewHolder>
{
    private Context mContext;
    private ArrayList<HomeCategory> categories;
    private CategoryItemClick categoryItemClick;

    public HomecategoryAdapter(Context mContext, ArrayList<HomeCategory> categories,CategoryItemClick categoryItemClick) {
        this.mContext = mContext;
        this.categories = categories;
        this.categoryItemClick =categoryItemClick;
    }

    @NonNull
    @Override
    public HomecategoryAdapter.SellerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_card_categories, viewGroup, false);
        return new HomecategoryAdapter.SellerCategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomecategoryAdapter.SellerCategoryViewHolder categoryViewHolder, int position) {
        final HomeCategory currentItem = categories.get(position);
        Glide.with(mContext).load(currentItem.getCat_icon())
                .into(categoryViewHolder.iv_category);
        categoryViewHolder.tv_category_name.setText(currentItem.getCat_name());
        categoryViewHolder.lin_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryItemClick.categoryClick(currentItem.getCat_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class SellerCategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_category_name;
        public ImageView iv_category;
        public LinearLayout lin_category;

        public SellerCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            lin_category = itemView.findViewById(R.id.lin_category);
            iv_category = itemView.findViewById(R.id.iv_category);

        }

    }
}
