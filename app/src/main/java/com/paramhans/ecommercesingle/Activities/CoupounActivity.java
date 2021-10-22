package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paramhans.ecommercesingle.Adapter.PromocodeAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.Interfaces.PromocodeClick;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.PromoCode;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CoupounActivity extends AppCompatActivity implements PromocodeClick {

    RecyclerView recycle_coupon;
    PromocodeAdapter promocodeAdapter;
    RelativeLayout rel_main_coupon;
    ArrayList<PromoCode> promoCodes;
    double sumPr = 0.00;
    ImageView coupon_backicon;
    EditText code_edit;
    TextView verifycode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupoun);
        recycle_coupon = findViewById(R.id.recycle_coupon);
        rel_main_coupon = findViewById(R.id.rel_main_coupon);
        coupon_backicon = findViewById(R.id.coupon_backicon);
        code_edit = findViewById(R.id.code_edit);
        verifycode = findViewById(R.id.verifycode);
        ArrayList<CartItem> cartItems = new ArrayList<>();
        SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
        cartItems = sharedPreferenceCart.loadFavorites(CoupounActivity.this);

        if (cartItems != null) {

            for (int i = 0; i < cartItems.size(); i++) {
                String price = cartItems.get(i).getDiscount_price();
                double dp = Double.parseDouble(price);
                sumPr = sumPr + dp;
            }
            Log.d("ssuummmmprr",String.valueOf(sumPr));
        }
        coupon_backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code_edit.getText().toString().isEmpty())
                {
                    code_edit.setError("Required");
                    return;
                }
                else
                    {
                        //applyPromoCode(code_edit.getText().toString());
                    }
            }
        });

        setPromos();


    }

    private void setPromos() {
        if (!CommonI.connectionAvailable(CoupounActivity.this))
        {
            CommonI.showSnackBar(CoupounActivity.this,
                    rel_main_coupon,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        }
        else {

            RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(CoupounActivity.this);
            recycle_coupon.setLayoutManager(mLayoutManager3);
            promoCodes = new ArrayList<>();

            RequestQueue mRequestQueue = Volley.newRequestQueue(CoupounActivity.this);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.allpromocode,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {


                            JSONArray jsonArray = response.getJSONArray(JsonVariables.data);

                            promoCodes.clear();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                // Get current json object
                                JSONObject productsJSONObject = jsonArray.getJSONObject(i);

                                String promocode_id = productsJSONObject.getString("promocode_id");
                                String promocode_image = productsJSONObject.getString("promocode_image");
                                String promocode_name = productsJSONObject.getString("promocode_name");
                                String promocode_percenatge = productsJSONObject.getString("promocode_percenatge");
                                String max_amount = productsJSONObject.getString("max_amount");
                                String description = productsJSONObject.getString("description");

                                promoCodes.add(new PromoCode(promocode_id,promocode_image,promocode_name,promocode_percenatge,max_amount,description));


                            }
                            promocodeAdapter = new PromocodeAdapter(CoupounActivity.this, promoCodes, CoupounActivity.this);
                            recycle_coupon.setAdapter(promocodeAdapter);

                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {

                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);

                    } else if (error instanceof ParseError) {
                        Log.d("parse_error", error.getMessage());
                    }

                }
            }) {
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                    return super.parseNetworkError(volleyError);
                }

            };

            mRequestQueue.add(objectRequest);
        }
    }

    private void applyPromoCode(final String promocodeid, final String name)
    {
        if (!CommonI.connectionAvailable(CoupounActivity.this))
        {
            CommonI.showSnackBar(CoupounActivity.this,
                    rel_main_coupon,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        }
        else {
            User user = SharedPrefManager.getInstance(CoupounActivity.this).getUser();
            final String id = user.getId();
            RequestQueue mRequestQueue = Volley.newRequestQueue(CoupounActivity.this);

            Map<String, String> params = new HashMap<>();
            params.put("promocode_id",promocodeid );
            params.put("customer_id", id);
            params.put("order_amount",String.valueOf(sumPr));

            Log.d("paraaammmsss",params.toString());


            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.promocode_check,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            Toast.makeText(CoupounActivity.this, "OOPS!Promocode is invalid.", Toast.LENGTH_SHORT).show();
                        } else {

                            Log.d("resspppskdla",response.toString());


                            Toast.makeText(CoupounActivity.this, "Promocode Applied Successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CoupounActivity.this, CheckOutActivity.class);
                            intent.putExtra("promocode_id", response.getJSONObject("data").getString("promocode_id"));
                            intent.putExtra("promocode_amount", response.getJSONObject("data").getString("promocode_amount"));
                            intent.putExtra("order_amount_after", response.getJSONObject("data").getString("order_amount_after_promocode"));
                            intent.putExtra("promocode_name",name);
                            startActivity(intent);
                            finish();

                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {

                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(CoupounActivity.this,
                                rel_main_coupon,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);

                    } else if (error instanceof ParseError) {
                        Log.d("parse_error", error.getMessage());
                    }

                }
            }) {
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                    return super.parseNetworkError(volleyError);
                }

            };

            mRequestQueue.add(objectRequest);
        }
    }


    @Override
    public void clickPromo(String id,String name) {

        applyPromoCode(id,name);
    }
}
