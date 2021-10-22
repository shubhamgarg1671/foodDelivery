package com.paramhans.ecommercesingle.LocalDb;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.paramhans.ecommercesingle.Models.CartItem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreferenceCart
{
    public static final String PREFS_NAME = "SINGLEVENDOR_APP";
    public static final String FAVORITES = "CART";
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public SharedPreferenceCart() {
        super();
    }


    public void storeFavorites(Context context, List<CartItem> favorites) {


        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }




    public ArrayList<CartItem> loadFavorites(Context context) {
        SharedPreferences settings;
        List<CartItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            CartItem[] favoriteItems = gson.fromJson(jsonFavorites,CartItem[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<CartItem>(favorites);
        } else
            return null;

        return (ArrayList<CartItem>) favorites;
    }


    public void addFavorite(Context context, CartItem beanSampleList) {
        List<CartItem> favorites = loadFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<CartItem>();
        favorites.add(beanSampleList);
        storeFavorites(context, favorites);
    }

    public void removeFavorite(Context context, int beanSampleList) {
        ArrayList<CartItem> favorites = loadFavorites(context);

        if (favorites != null) {
            favorites.remove(beanSampleList);

            storeFavorites(context, favorites);
        }



    }

    public void checkRestaurant(Context context){

        ArrayList<CartItem> favorites =loadFavorites(context);


    }


    public void editFavorite(Context context, int beanSampllist, String textprice, String quanity){

        ArrayList<CartItem> favorites =loadFavorites(context);
        if(favorites!=null){


            favorites.get(beanSampllist).setDiscount_price(textprice);

            favorites.get(beanSampllist).setQty(quanity);
            storeFavorites(context, favorites);
        }

    }


    public void clearDate(Context context)
    {

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        // Clearing all data from Shared Preferences

        editor.remove(FAVORITES);
        editor.apply();
        editor.clear();
        editor.commit();

    }
}
