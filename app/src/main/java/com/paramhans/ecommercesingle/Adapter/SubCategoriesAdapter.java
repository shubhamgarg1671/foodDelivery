package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Activities.SubCategoryActivity;
import com.paramhans.ecommercesingle.Models.HomeSubCat;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SellerCategoryViewHolder>
{
    private Context mContext;
    private ArrayList<HomeSubCat> categories;

    public SubCategoriesAdapter(Context mContext, ArrayList<HomeSubCat> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @NonNull
    @Override
    public SubCategoriesAdapter.SellerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_card_categories, viewGroup, false);
        return new SubCategoriesAdapter.SellerCategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoriesAdapter.SellerCategoryViewHolder categoryViewHolder, int position) {
        final HomeSubCat currentItem = categories.get(position);
        Glide.with(mContext).load(currentItem.getSubcat_image())
                .into(categoryViewHolder.iv_category);
        //Toast.makeText(mContext, ""+currentItem.getSubcat_image(), Toast.LENGTH_SHORT).show();
        categoryViewHolder.tv_category_name.setText(currentItem.getSubcat_name());
        categoryViewHolder.lin_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
