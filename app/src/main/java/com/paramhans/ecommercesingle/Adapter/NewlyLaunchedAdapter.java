package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Activities.CategoryActivity;
import com.paramhans.ecommercesingle.Models.NewlyLaunched;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class NewlyLaunchedAdapter extends RecyclerView.Adapter<NewlyLaunchedAdapter.NewlyLaunchedViewHolder> {
    private Context mContext;
    private ArrayList<NewlyLaunched> categories;

    public NewlyLaunchedAdapter(Context mContext, ArrayList<NewlyLaunched> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @NonNull
    @Override
    public NewlyLaunchedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_newly_launched, viewGroup, false);
        return new NewlyLaunchedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewlyLaunchedViewHolder categoryViewHolder, int position) {
        final NewlyLaunched currentItem = categories.get(position);
        Glide.with(mContext).load(currentItem.getImage())
                .into(categoryViewHolder.iv_category);
        //categoryViewHolder.tv_category_name.setText(currentItem.getCat_name());
        categoryViewHolder.lin_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra("cat_id", currentItem.getCat_id());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class NewlyLaunchedViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_category_name;
        public ImageView iv_category;
        public LinearLayout lin_category;

        public NewlyLaunchedViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_name);
            lin_category = itemView.findViewById(R.id.lin_category);
            iv_category = itemView.findViewById(R.id.iv_cat);

        }

    }
}
