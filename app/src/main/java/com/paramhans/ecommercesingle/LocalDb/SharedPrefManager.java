package com.paramhans.ecommercesingle.LocalDb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.paramhans.ecommercesingle.Activities.LoginActivity;
import com.paramhans.ecommercesingle.Models.User;


public class SharedPrefManager
{
    public static final String SHARED_PREF_NAME = "single_vendor_shared_preference";
    public static final String LOCATION_SP ="location_sp";
    public static final String PHONE_NUM="PhoneNumber";
    public static final String DEFAULT_VALUE="empty";
    public static final String FULL_NAME_PREF="keyusername";
    public static final String LAT= "Latitude";
    public static final String LONG="Longitude";
    public static final String USER_ID_PREF="user_id";
    public static final String ADDRESS_TYPE ="Address_type";
    public static final String ADDRESS_ID = "address_id";
    public static final String ADDRESS = "address_total";
    public static final String USER_TYPE="user_type";
    public static final String EMAIL="email";
    public static final String PROFILE_PICTURE="profile_picture";
    public static final String FCM_TOKEN="fcm_token";
    public static final String DEVICE_ID = "device_id";
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_TYPE,user.getUser_type());
        editor.putString(FULL_NAME_PREF, user.getName());
        editor.putString(PHONE_NUM, user.getMobile());
        editor.putString(EMAIL,user.getEmail());
        editor.putString(USER_TYPE,user.getUser_type());
        editor.putString(USER_ID_PREF,user.getId());
        editor.putString(FCM_TOKEN,user.getFcm_token());
        editor.putString(DEVICE_ID,user.getDevice_id());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FULL_NAME_PREF, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(USER_ID_PREF,null),
                sharedPreferences.getString(FULL_NAME_PREF, null),
                sharedPreferences.getString(EMAIL, null),
                sharedPreferences.getString(PROFILE_PICTURE, null),
                sharedPreferences.getString(PHONE_NUM,null),
                sharedPreferences.getString(USER_TYPE,null),
                sharedPreferences.getString(DEVICE_ID,null),
                sharedPreferences.getString(FCM_TOKEN, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

}
