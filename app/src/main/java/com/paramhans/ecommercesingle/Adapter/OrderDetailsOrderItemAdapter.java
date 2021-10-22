package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paramhans.ecommercesingle.Models.OrderOrder_items;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class OrderDetailsOrderItemAdapter extends RecyclerView.Adapter<OrderDetailsOrderItemAdapter.OrderItemsViewHolder>
{
    private Context mContext;
    private ArrayList<OrderOrder_items> carts;


    public OrderDetailsOrderItemAdapter(Context mContext, ArrayList<OrderOrder_items> carts) {
        this.mContext = mContext;
        this.carts = carts;
    }

    @NonNull
    @Override
    public OrderDetailsOrderItemAdapter.OrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_order_details, viewGroup, false);
        return new OrderDetailsOrderItemAdapter.OrderItemsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailsOrderItemAdapter.OrderItemsViewHolder orderItemsViewHolder, final int position) {
        final OrderOrder_items currentItem = carts.get(position);


        orderItemsViewHolder.tv_product_name.setText(" X " +" "+ currentItem.getProduct_name());


        String spPrice = currentItem.getTotal_price();
        final double dSpPrice = Double.parseDouble(spPrice);
        int quantity = Integer.parseInt(currentItem.getQty());

        orderItemsViewHolder.tv_qty.setText(currentItem.getQty());

        orderItemsViewHolder.tv_price.setText("Rs."+spPrice);

        if(!currentItem.getService_provider_name().isEmpty())
        {
            orderItemsViewHolder.call.setVisibility(View.VISIBLE);
            orderItemsViewHolder.mobile.setVisibility(View.VISIBLE);
            orderItemsViewHolder.name.setVisibility(View.VISIBLE);
            orderItemsViewHolder.name.setText(currentItem.getService_provider_name());
        }
        else
            {
                orderItemsViewHolder.name.setText(" ");
                //Will let you know soon.. :)
            }

    }

    @Override
    public int getItemCount() {
        return carts.size();
    }


    public class OrderItemsViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_product_name, tv_price, tv_qty,mobile,name;
        ImageView call;

        public OrderItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tv_name_with_attribute);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            mobile = itemView.findViewById(R.id.mobile);
            name = itemView.findViewById(R.id.name);
            call = itemView.findViewById(R.id.call);
        }

    }
}
