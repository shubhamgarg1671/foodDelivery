package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.paramhans.ecommercesingle.Activities.OrderDetailsActivity;
import com.paramhans.ecommercesingle.Models.Order;
import com.paramhans.ecommercesingle.Models.OrderOrder_items;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderListViewHolder>
{
    private Context mContext;
    private ArrayList<Order> orders;
    OrderItemAdapter orderItemAdapter ;

    public OrderAdapter(Context mContext, ArrayList<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_orders, viewGroup, false);
        return new OrderAdapter.OrderListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.OrderListViewHolder orderListViewHolder, int position) {
        final Order currentItem = orders.get(position);

        //categoryViewHolder.tv_date_time.setText(currentItem.getName());
        ArrayList<OrderOrder_items> cartArrayList = new ArrayList<>();

        orderListViewHolder.tv_date_time.setText(currentItem.getOrder_date());
        orderListViewHolder.tv_status.setText(currentItem.getOrder_status());
        orderListViewHolder.tv_total_amount.setText("Rs. "+currentItem.getOrder_amount());
        orderListViewHolder.tv_view_order_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                intent.putExtra("order_code",currentItem.getOrder_code());
                intent.putExtra("back","0");
                mContext.startActivity(intent);
            }
        });

        orderListViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                intent.putExtra("order_code",currentItem.getOrder_code());
                intent.putExtra("back","0");
                mContext.startActivity(intent);
            }
        });

        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(mContext);
        orderListViewHolder.rv_order_items.setLayoutManager(mLayoutManager3);
        cartArrayList = currentItem.getOrderOrder_items();

        orderItemAdapter = new OrderItemAdapter(mContext,cartArrayList);

        orderListViewHolder.rv_order_items.setAdapter(orderItemAdapter);


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_date_time,tv_status,tv_view_order_details,tv_total_amount;
        RecyclerView rv_order_items;
        LinearLayout container;

        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date_time = itemView.findViewById(R.id.tv_date_time);
            rv_order_items = itemView.findViewById(R.id.rv_order_items);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_view_order_details = itemView.findViewById(R.id.tv_view_order_details);
            tv_total_amount = itemView.findViewById(R.id.tv_total_amount);
            container = itemView.findViewById(R.id.container);

        }

    }
}
