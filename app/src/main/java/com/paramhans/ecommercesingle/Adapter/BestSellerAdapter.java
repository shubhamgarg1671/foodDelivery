package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Activities.ProductDetailsActivity;
import com.paramhans.ecommercesingle.Fragments.HomeFragment;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;
import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ProductViewHolder>
{
    private Context mContext;
    private ArrayList<Products> products;

    ArrayList<CartItem> favouritesBeanSampleList;
    String qnttty;
    int indx, noit;
    double sum = 0.00;

    ProductAddInterface productAddInterface;
    SharedPreferenceCart sharedPreference = new SharedPreferenceCart();

    public BestSellerAdapter(Context mContext, ArrayList<Products> products, ProductAddInterface productAddInterface) {
        this.mContext = mContext;
        this.products = products;
        this.productAddInterface = productAddInterface;
    }

    @NonNull
    @Override
    public BestSellerAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_product, viewGroup, false);
        return new BestSellerAdapter.ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final BestSellerAdapter.ProductViewHolder productViewHolder, final int position) {
        final Products currentItem = products.get(position);
        productViewHolder.tv_product_name.setText(currentItem.getName());

        Glide.with(mContext).load(currentItem.getImage())
                .into(productViewHolder.iv_product);

        if (checkIfItemAvailable(currentItem.getPid())) {
            productViewHolder.add.setText("added");
        } else {
            productViewHolder.add.setText("add");
        }

        if (checkIfSameItem(currentItem.getPid())) {
            productViewHolder.plus_img.setVisibility(View.VISIBLE);
            productViewHolder.img_minus.setVisibility(View.VISIBLE);
            productViewHolder.order_size.setEnabled(false);
            productViewHolder.order_size.setText(qnttty);
        } else {

        }


        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("name",currentItem.getName());
                intent.putExtra("id",currentItem.getPid());
                intent.putExtra("price",currentItem.getPrice());
                intent.putExtra("discount_price",currentItem.getDiscount_price());
                intent.putExtra("image",currentItem.getImage());
                intent.putExtra("desc",currentItem.getDescription());
                mContext.startActivity(intent);
            }
        });

        productViewHolder.tv_price.setText("Rs." + currentItem.getDiscount_price());
        productViewHolder.tv_mrp.setText("Rs. " + currentItem.getPrice());
        productViewHolder.tv_mrp.setPaintFlags(productViewHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        productViewHolder.order_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (productViewHolder.order_size.getText().toString().equalsIgnoreCase("ADD")) {
//                    session = new SessionManager(context);
//                    session.setRestaurantMinAmt(ResturantActivity.minamount);
//                    session.setRestrurantAddress(ResturantActivity.resaddress);
//                    session.setRestrurantImage(ResturantActivity.resimage);
//                    session.setRestrurantName(ResturantActivity.restrurantName);
//                        session.setRestaurantMinAmt("100");
                    productViewHolder.item_qty_relative.setVisibility(View.VISIBLE);

                    String product_id = currentItem.getPid();
                    String product_name = currentItem.getName();
                    String price = currentItem.getPrice();
                    String special_price = currentItem.getDiscount_price();
                    String product_image = currentItem.getImage();
                    String description = currentItem.getDescription();
                    String qty = "1";
                    productAddInterface.productAdded(true);

                    favouritesBeanSampleList = sharedPreference.loadFavorites(mContext);


                    if (favouritesBeanSampleList == null) {

                        CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                        sharedPreference.addFavorite(mContext, cartitem);

                        productViewHolder.plus_img.setVisibility(View.VISIBLE);
                        productViewHolder.img_minus.setVisibility(View.VISIBLE);
                        productViewHolder.order_size.setEnabled(false);
                        productViewHolder.order_size.setText("1");
                        productAddInterface.productAdded(true);
                        Toast.makeText(mContext, "Item Added ", Toast.LENGTH_SHORT).show();
                        productViewHolder.add.setText("added");
                        CartAmt();
                    } else if (favouritesBeanSampleList.size() == 0) {

                        CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                        sharedPreference.addFavorite(mContext, cartitem);

                        productViewHolder.plus_img.setVisibility(View.VISIBLE);
                        productViewHolder.img_minus.setVisibility(View.VISIBLE);
                        productViewHolder.order_size.setEnabled(false);
                        productViewHolder.order_size.setText("1");
                        productAddInterface.productAdded(true);
                        Toast.makeText(mContext, "Item Added ", Toast.LENGTH_SHORT).show();
                        productViewHolder.add.setText("added");
                        CartAmt();
                    } else {

                        if (checkIfSameRestaurant("0")) {

                            CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                            sharedPreference.addFavorite(mContext, cartitem);

                            productViewHolder.plus_img.setVisibility(View.VISIBLE);
                            productViewHolder.img_minus.setVisibility(View.VISIBLE);
                            productViewHolder.order_size.setEnabled(false);
                            productViewHolder.order_size.setText("1");
                            productAddInterface.productAdded(true);
                            Toast.makeText(mContext, "Item Added ", Toast.LENGTH_SHORT).show();
                            productViewHolder.add.setText("added");
                            CartAmt();
                        } else {
                            sharedPreference.clearDate(mContext);
                            CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                            sharedPreference.addFavorite(mContext, cartitem);
                            productAddInterface.productAdded(true);
                            productViewHolder.plus_img.setVisibility(View.VISIBLE);
                            productViewHolder.img_minus.setVisibility(View.VISIBLE);
                            productViewHolder.order_size.setEnabled(false);
                            productViewHolder.order_size.setText("1");

                            Toast.makeText(mContext, "Item Added ", Toast.LENGTH_SHORT).show();
                            productViewHolder.add.setText("added");
                            CartAmt();
                        }
                    }


                } else {


                }


                //ItemCounter();
            }
        });

        productViewHolder.plus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productViewHolder.order_size.getText().toString().equalsIgnoreCase("ADD")) {

                    productAddInterface.productAdded(true);

                    //ItemCounter();
                } else if(productViewHolder.order_size.getText().toString().equalsIgnoreCase("20")) {

                    productAddInterface.productAdded(true);
                    Toast.makeText(mContext, "Maximum ordering limit", Toast.LENGTH_SHORT).show();

                }else {

                    productAddInterface.productAdded(true);
                    getindex(currentItem.getPid());

                    int plusValue = Integer.parseInt(productViewHolder.order_size.getText().toString()) + 1;


                    productViewHolder.order_size.setText(String.valueOf(plusValue));

                    double t = Double.parseDouble(currentItem.getDiscount_price())*plusValue;

                    sharedPreference.editFavorite(mContext, indx, String.valueOf(t), String.valueOf(plusValue));

                    CartAmt();
                    //ItemCounter();

                }
            }
        });

        productViewHolder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productViewHolder.order_size.getText().toString().equalsIgnoreCase("ADD")) {
                    productAddInterface.productAdded(true);
                } else if (productViewHolder.order_size.getText().toString().equalsIgnoreCase("1")) {

                    productAddInterface.productAdded(true);
                    getindex(currentItem.getPid());

                    sharedPreference.removeFavorite(mContext, indx);

                    productViewHolder.plus_img.setVisibility(View.INVISIBLE);
                    productViewHolder.img_minus.setVisibility(View.INVISIBLE);
                    productViewHolder.order_size.setEnabled(true);
                    productViewHolder.order_size.setText("ADD");

                    CartAmt();

                    // ItemCounter();
                }  else  {
                    productAddInterface.productAdded(true);
                    getindex(currentItem.getPid());

                    int minusValue = Integer.parseInt(productViewHolder.order_size.getText().toString()) - 1;

                    productViewHolder.order_size.setText(String.valueOf(minusValue));

                    double m = Double.parseDouble(currentItem.getDiscount_price()) * minusValue;

                    sharedPreference.editFavorite(mContext, indx, String.valueOf(m), String.valueOf(minusValue));


                    CartAmt();

                    // ItemCounter();
                    Log.d("njjvcnmm", String.valueOf(m));


                }

            }
        });


    }

    public boolean checkIfItemAvailable(String checkProduct) {
        boolean check = false;
        List<CartItem> cartItems = sharedPreference.loadFavorites(mContext);
        if (cartItems != null) {
            for (CartItem product : cartItems) {

                if (product.getPid().equals(checkProduct)) {

                    check = true;
                    break;

                }

            }
        }
        return check;
    }

    public boolean checkIfSameItem(String checkProduct) {
        boolean check = false;
        List<CartItem> cartItems = sharedPreference.loadFavorites(mContext);
        if (cartItems != null) {
            for (CartItem product : cartItems) {

                if (product.getPid().equals(checkProduct)) {

                    qnttty = product.getQty();


                    check = true;
                    break;

                }

            }
        }
        return check;

    }

    public boolean getindex(String checkProduct) {
        boolean check = false;
        List<CartItem> favorites = sharedPreference.loadFavorites(mContext);
        if (favorites != null) {
            for (CartItem product : favorites) {

                if (product.getPid().equals(checkProduct)) {
                    indx = favorites.indexOf(product);
                    check = true;
                    break;

                }

            }
        }
        return check;


    }

    public boolean checkIfSameRestaurant(String checkProduct) {
        boolean check = false;
        List<CartItem> favorites = sharedPreference.loadFavorites(mContext);
        if (favorites != null) {
            for (CartItem product : favorites) {

                if (product.getShop_id().equals(checkProduct)) {

                    check = true;
                    break;

                }

            }
        }
        return check;


    }

    public void CartAmt() {
        favouritesBeanSampleList = sharedPreference.loadFavorites(mContext);
        sum = 0.00;
        noit = 0;

        if (favouritesBeanSampleList != null) {

            for (int j = 0; j < favouritesBeanSampleList.size(); j++) {


                CartItem cartIt = favouritesBeanSampleList.get(j);

                sum = sum + Double.parseDouble(cartIt.getDiscount_price());

                noit = noit + Integer.parseInt(cartIt.getQty());

            }

            Log.d("adkv", String.valueOf(sum) + " ------- noit" + noit);

            if(sum>0.00)
            {
                HomeFragment.tv_cart.setEnabled(true);
            }
            else
            {
                HomeFragment.tv_cart.setEnabled(false);
            }
        }


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView order_size, add, tv_product_name, tv_mrp, tv_price, tv_special_price, tv_quantity, tv_single_attribute;
        public ImageView iv_product, plus_img, img_minus;
        RelativeLayout item_qty_relative;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_qty_relative = itemView.findViewById(R.id.item_qty_relative);
            order_size = itemView.findViewById(R.id.order_size);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_mrp = itemView.findViewById(R.id.tv_mrp);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_special_price = itemView.findViewById(R.id.tv_special_price);
            add = itemView.findViewById(R.id.add);
            iv_product = itemView.findViewById(R.id.iv_product);
            plus_img = itemView.findViewById(R.id.plus_img);
            img_minus = itemView.findViewById(R.id.img_minus);
        }

    }
}
