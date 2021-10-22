package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.SellerCategoryViewHolder>
{
    private Context mContext;
    private ArrayList<CartItem> cartItems;


    public CartAdapter(Context mContext, ArrayList<CartItem> cartItems) {
        this.mContext = mContext;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartAdapter.SellerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_cart, viewGroup, false);
        return new CartAdapter.SellerCategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.SellerCategoryViewHolder categoryViewHolder, int position) {
        final CartItem currentItem = cartItems.get(position);
        categoryViewHolder.tv_name.setText(currentItem.getName()+"["+currentItem.getQty()+"]");
        categoryViewHolder.tv_price.setText("Rs."+currentItem.getDiscount_price());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class SellerCategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name,tv_price;

        public SellerCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_product_name);
            tv_price = itemView.findViewById(R.id.tv_price);

        }
    }
}
