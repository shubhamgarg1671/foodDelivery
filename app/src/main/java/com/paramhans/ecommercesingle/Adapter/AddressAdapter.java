package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.paramhans.ecommercesingle.Interfaces.AddressItemClick;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.AdressModel;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private Context mContext;
    private ArrayList<AdressModel> adressModels;
    AddressItemClick addressItemClick;

    public AddressAdapter(Context mContext, ArrayList<AdressModel> adressModels, AddressItemClick addressItemClick) {
        this.mContext = mContext;
        this.adressModels = adressModels;
        this.addressItemClick = addressItemClick;
    }

    @NonNull
    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_address, viewGroup, false);
        return new AddressAdapter.AddressViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddressAdapter.AddressViewHolder addressViewHolder, final int position) {
        final AdressModel currentItem = adressModels.get(position);

        User user = SharedPrefManager.getInstance(mContext).getUser();
        String name = user.getName();
        String phone = user.getMobile();
        addressViewHolder.tv_name.setText(name);
        addressViewHolder.tv_phone.setText(phone);
        addressViewHolder.tv_address_1.setText(currentItem.getAddress_1());
        addressViewHolder.tv_city.setText(currentItem.getCity());
        addressViewHolder.tv_state.setText(currentItem.getState());
        addressViewHolder.tv_pin.setText(currentItem.getPincode());
        addressViewHolder.type.setText(currentItem.getCountry());
        addressViewHolder.tv_address_2.setText(currentItem.getAddress_2());
        if (currentItem.getIs_default().equals("1")) {
            addressViewHolder.radio_default_address.setChecked(true);
            SharedPreferences pref = mContext.getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString(SharedPrefManager.LAT, currentItem.getLatitude());
            edt.putString(SharedPrefManager.LONG, currentItem.getLongitude());
            edt.putString(SharedPrefManager.ADDRESS, currentItem.getAddress_1()+","+currentItem.getAddress_2()+","
                    +currentItem.getCity()+"-["+currentItem.getCountry()+"]");
            edt.apply();

            User user1 = SharedPrefManager.getInstance(mContext).getUser();

            User user2 = new User();

            user2.setId(user1.getId());
            user2.setEmail(user1.getEmail());
            user2.setMobile(user1.getMobile());
            user2.setName(user1.getName());
            user2.setUser_type(currentItem.getPincode());
            SharedPrefManager.getInstance(mContext).userLogin(user2);

        }

        addressViewHolder.radio_default_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //addressViewHolder.radio_default_address.setChecked(false);
                    addressItemClick.defaultClick(currentItem);

                } else {
                    addressViewHolder.radio_default_address.setChecked(true);
                    addressItemClick.defaultClick(currentItem);
                    SharedPreferences pref = mContext.getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putString(SharedPrefManager.LAT, currentItem.getLatitude());
                    edt.putString(SharedPrefManager.LONG, currentItem.getLongitude());
                    edt.putString(SharedPrefManager.ADDRESS_ID, currentItem.getAddress_id());
                    edt.putString(SharedPrefManager.ADDRESS, currentItem.getAddress_1()+","+currentItem.getAddress_2()+","
                    +currentItem.getCity()+"-["+currentItem.getCountry()+"]");
                    edt.apply();

                    User user1 = SharedPrefManager.getInstance(mContext).getUser();

                    User user2 = new User();

                    user2.setId(user1.getId());
                    user2.setEmail(user1.getEmail());
                    user2.setMobile(user1.getMobile());
                    user2.setName(user1.getName());
                    user2.setUser_type(currentItem.getPincode());
                    SharedPrefManager.getInstance(mContext).userLogin(user2);
                }
            }
        });

        addressViewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addressItemClick.remove(currentItem);

            }
        });

        addressViewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressItemClick.edit(currentItem);
            }
        });


    }

    @Override
    public int getItemCount() {
        return adressModels.size();
    }


    public class AddressViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name, tv_address_1, tv_city, tv_pin, tv_state, tv_phone, type, tv_address_2;

        public Button btn_remove, btn_edit;
        public CheckBox radio_default_address;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address_1 = itemView.findViewById(R.id.tv_address_1);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_pin = itemView.findViewById(R.id.tv_pin);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            type = itemView.findViewById(R.id.type);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            radio_default_address = itemView.findViewById(R.id.radio_default_address);
            tv_address_2 = itemView.findViewById(R.id.tv_address_2);
        }

    }

}
