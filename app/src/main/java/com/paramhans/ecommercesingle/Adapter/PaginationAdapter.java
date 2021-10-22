package com.paramhans.ecommercesingle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
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
import com.paramhans.ecommercesingle.Activities.CategoryActivity;
import com.paramhans.ecommercesingle.Activities.ProductDetailsActivity;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.R;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Products> movies;
    private Context context;

    ArrayList<CartItem> favouritesBeanSampleList;
    String qnttty;
    int indx, noit;
    double sum = 0.00;

    ProductAddInterface productAddInterface;
    SharedPreferenceCart sharedPreference = new SharedPreferenceCart();

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context,ProductAddInterface productAddInterface) {
        this.context = context;
        this.productAddInterface = productAddInterface;
        movies = new ArrayList<>();
    }

    public boolean checkIfItemAvailable(String checkProduct) {
        boolean check = false;
        List<CartItem> cartItems = sharedPreference.loadFavorites(context);
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
        List<CartItem> cartItems = sharedPreference.loadFavorites(context);
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
        List<CartItem> favorites = sharedPreference.loadFavorites(context);
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
        List<CartItem> favorites = sharedPreference.loadFavorites(context);
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
        favouritesBeanSampleList = sharedPreference.loadFavorites(context);
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
                CategoryActivity.tv_price.setText("Total Rs."+String.valueOf(sum));
                CategoryActivity.rv_proceed.setVisibility(View.VISIBLE);
            }
            else
            {
                CategoryActivity.rv_proceed.setVisibility(View.GONE);
            }
        }


    }

    public List<Products> getMovies() {
        return movies;
    }

    public void setMovies(List<Products> movies) {
        this.movies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_product, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Products movie = movies.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH productViewHolder = (MovieVH) holder;
                final Products currentItem = movies.get(position);
                productViewHolder.tv_product_name.setText(currentItem.getName());

                Glide.with(context).load(currentItem.getImage())
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


                productViewHolder.tv_price.setText("Rs." + currentItem.getDiscount_price());
                productViewHolder.tv_mrp.setText("Rs. " + currentItem.getPrice());
                productViewHolder.tv_mrp.setPaintFlags(productViewHolder.tv_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                productViewHolder.order_size.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (productViewHolder.order_size.getText().toString().equalsIgnoreCase("ADD")) {
                            productViewHolder.item_qty_relative.setVisibility(View.VISIBLE);

                            String product_id = currentItem.getPid();
                            String product_name = currentItem.getName();
                            String price = currentItem.getPrice();
                            String special_price = currentItem.getDiscount_price();
                            String product_image = currentItem.getImage();
                            String description = currentItem.getDescription();
                            String qty = "1";
                            productAddInterface.productAdded(true);

                            favouritesBeanSampleList = sharedPreference.loadFavorites(context);


                            if (favouritesBeanSampleList == null) {

                                CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                                sharedPreference.addFavorite(context, cartitem);

                                productViewHolder.plus_img.setVisibility(View.VISIBLE);
                                productViewHolder.img_minus.setVisibility(View.VISIBLE);
                                productViewHolder.order_size.setEnabled(false);
                                productViewHolder.order_size.setText("1");
                                productAddInterface.productAdded(true);
                                Toast.makeText(context, "Item Added ", Toast.LENGTH_SHORT).show();
                                productViewHolder.add.setText("added");
                                CartAmt();
                            } else if (favouritesBeanSampleList.size() == 0) {

                                CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                                sharedPreference.addFavorite(context, cartitem);

                                productViewHolder.plus_img.setVisibility(View.VISIBLE);
                                productViewHolder.img_minus.setVisibility(View.VISIBLE);
                                productViewHolder.order_size.setEnabled(false);
                                productViewHolder.order_size.setText("1");
                                productAddInterface.productAdded(true);
                                Toast.makeText(context, "Item Added ", Toast.LENGTH_SHORT).show();
                                productViewHolder.add.setText("added");
                                CartAmt();
                            } else {

                                if (checkIfSameRestaurant("0")) {

                                    CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                                    sharedPreference.addFavorite(context, cartitem);

                                    productViewHolder.plus_img.setVisibility(View.VISIBLE);
                                    productViewHolder.img_minus.setVisibility(View.VISIBLE);
                                    productViewHolder.order_size.setEnabled(false);
                                    productViewHolder.order_size.setText("1");
                                    productAddInterface.productAdded(true);
                                    Toast.makeText(context, "Item Added ", Toast.LENGTH_SHORT).show();
                                    productViewHolder.add.setText("added");
                                    CartAmt();
                                } else {
                                    sharedPreference.clearDate(context);
                                    CartItem cartitem = new CartItem(product_id, product_name, description, price, special_price, product_image, qty,"0");
                                    sharedPreference.addFavorite(context, cartitem);
                                    productAddInterface.productAdded(true);
                                    productViewHolder.plus_img.setVisibility(View.VISIBLE);
                                    productViewHolder.img_minus.setVisibility(View.VISIBLE);
                                    productViewHolder.order_size.setEnabled(false);
                                    productViewHolder.order_size.setText("1");

                                    Toast.makeText(context, "Item Added ", Toast.LENGTH_SHORT).show();
                                    productViewHolder.add.setText("added");
                                    CartAmt();
                                }
                            }


                        } else {


                        }

                    }
                });

                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProductDetailsActivity.class);
                        intent.putExtra("name",currentItem.getName());
                        intent.putExtra("id",currentItem.getPid());
                        intent.putExtra("price",currentItem.getPrice());
                        intent.putExtra("discount_price",currentItem.getDiscount_price());
                        intent.putExtra("image",currentItem.getImage());
                        intent.putExtra("desc",currentItem.getDescription());
                        context.startActivity(intent);
                    }
                });

                productViewHolder.plus_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (productViewHolder.order_size.getText().toString().equalsIgnoreCase("ADD")) {
                            productAddInterface.productAdded(true);
                        } else if(productViewHolder.order_size.getText().toString().equalsIgnoreCase("20")) {

                            productAddInterface.productAdded(true);
                            Toast.makeText(context, "Maximum ordering limit", Toast.LENGTH_SHORT).show();

                        }else {

                            productAddInterface.productAdded(true);
                            getindex(currentItem.getPid());

                            int plusValue = Integer.parseInt(productViewHolder.order_size.getText().toString()) + 1;


                            productViewHolder.order_size.setText(String.valueOf(plusValue));

                            double t = Double.parseDouble(currentItem.getDiscount_price())*plusValue;

                            sharedPreference.editFavorite(context, indx, String.valueOf(t), String.valueOf(plusValue));

                            CartAmt();


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

                            sharedPreference.removeFavorite(context, indx);

                            productViewHolder.plus_img.setVisibility(View.INVISIBLE);
                            productViewHolder.img_minus.setVisibility(View.INVISIBLE);
                            productViewHolder.order_size.setEnabled(true);
                            productViewHolder.order_size.setText("ADD");

                            CartAmt();

                        }  else  {
                            productAddInterface.productAdded(true);
                            getindex(currentItem.getPid());

                            int minusValue = Integer.parseInt(productViewHolder.order_size.getText().toString()) - 1;

                            productViewHolder.order_size.setText(String.valueOf(minusValue));

                            double m = Double.parseDouble(currentItem.getDiscount_price()) * minusValue;

                            sharedPreference.editFavorite(context, indx, String.valueOf(m), String.valueOf(minusValue));


                            CartAmt();

                            Log.d("njjvcnmm", String.valueOf(m));


                        }

                    }
                });

                break;
            case LOADING:

                break;
        }

    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == movies.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Products mc) {
        movies.add(mc);
        notifyItemInserted(movies.size() - 1);
    }

    public void addAll(List<Products> mcList) {
        for (Products mc : mcList) {
            add(mc);
        }
    }

    public void remove(Products city) {
        int position = movies.indexOf(city);
        if (position > -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Products());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movies.size() - 1;
        Products item = getItem(position);

        if (item != null) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Products getItem(int position) {
        return movies.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder {
        public TextView order_size, add, tv_product_name, tv_mrp, tv_price, tv_special_price, tv_quantity, tv_single_attribute;
        public ImageView iv_product, plus_img, img_minus;
        RelativeLayout item_qty_relative;

        public MovieVH(View view) {
            super(view);

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


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
