package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paramhans.ecommercesingle.Models.HomeCatSubCat;
import com.paramhans.ecommercesingle.Models.HomeSubCat;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class HomeCatNameAdapter extends RecyclerView.Adapter<HomeCatNameAdapter.SellerCategoryViewHolder>
{
    private Context mContext;
    private ArrayList<HomeCatSubCat> categories;
    HomeSubcategoryAdapter homeSubcategoryAdapter;

    public HomeCatNameAdapter(Context mContext, ArrayList<HomeCatSubCat> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @NonNull
    @Override
    public HomeCatNameAdapter.SellerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_cat_subcat_home, viewGroup, false);
        return new HomeCatNameAdapter.SellerCategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeCatNameAdapter.SellerCategoryViewHolder categoryViewHolder, int position) {
        final HomeCatSubCat currentItem = categories.get(position);

        categoryViewHolder.tv_category_name.setText(currentItem.getCat_name());
        ArrayList<HomeSubCat> subCatArrayList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager3 = new GridLayoutManager(mContext,2);
        categoryViewHolder.rv_subcat.setLayoutManager(mLayoutManager3);
        subCatArrayList = currentItem.getHomeSubCats();

        homeSubcategoryAdapter = new HomeSubcategoryAdapter(mContext,subCatArrayList);

        categoryViewHolder.rv_subcat.setAdapter(homeSubcategoryAdapter);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class SellerCategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_category_name;
        public RecyclerView rv_subcat;

        public SellerCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            rv_subcat = itemView.findViewById(R.id.rv_subcat);

        }

    }
}
