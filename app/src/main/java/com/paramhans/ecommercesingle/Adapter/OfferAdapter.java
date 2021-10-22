package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Interfaces.PromocodeClick;
import com.paramhans.ecommercesingle.Models.PromoCode;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.AddressViewHolder>
{
    private Context mContext;
    private ArrayList<PromoCode> promoCodes;
    PromocodeClick promocodeClick;

    public OfferAdapter(Context mContext, ArrayList<PromoCode> promoCodes, PromocodeClick promocodeClick) {
        this.mContext = mContext;
        this.promoCodes = promoCodes;
        this.promocodeClick = promocodeClick;
    }

    @NonNull
    @Override
    public OfferAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_promocode, viewGroup, false);
        return new OfferAdapter.AddressViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfferAdapter.AddressViewHolder viewHolder, final int position) {
        final PromoCode currentItem = promoCodes.get(position);

        viewHolder.tv_name.setText(currentItem.getPromocode_name());
        viewHolder.tv_description.setText(currentItem.getDescription());
        viewHolder.tv_max_amount.setText("Note:You can get off upto Rs."+currentItem.getMax_amount());
        Glide.with(mContext).load(currentItem.getPromocode_image())
                .into(viewHolder.iv_promo);
        viewHolder.tv_apply.setVisibility(View.GONE);
        viewHolder.tv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promocodeClick.clickPromo(currentItem.getPromocode_id(),currentItem.getPromocode_name());
            }
        });



    }

    @Override
    public int getItemCount() {
        return promoCodes.size();
    }


    public class AddressViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name, tv_apply, tv_description, tv_max_amount;
        public ImageView iv_promo;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_apply = itemView.findViewById(R.id.tv_apply);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_max_amount = itemView.findViewById(R.id.tv_max_amount);
            iv_promo = itemView.findViewById(R.id.iv_promo);
        }

    }
}
