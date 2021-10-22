package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paramhans.ecommercesingle.Models.RateCard;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class RateCardAdapter extends RecyclerView.Adapter<RateCardAdapter.RateCardViewHolder>
{
    private Context mContext;
    private ArrayList<RateCard> rateCards;

    public RateCardAdapter(Context mContext, ArrayList<RateCard> rateCards) {
        this.mContext = mContext;
        this.rateCards = rateCards;
    }

    @NonNull
    @Override
    public RateCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_rate_card, viewGroup, false);
        return new RateCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RateCardViewHolder viewHolder, final int position) {
        final RateCard currentItem = rateCards.get(position);

        viewHolder.tv_charge.setText("Rs."+currentItem.getRatechat_price());
        viewHolder.tv_desc.setText(currentItem.getRatechat_name());


    }

    @Override
    public int getItemCount() {
        return rateCards.size();
    }


    public class RateCardViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_desc, tv_charge;

        public RateCardViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_charge = itemView.findViewById(R.id.tv_charge);

        }

    }
}
