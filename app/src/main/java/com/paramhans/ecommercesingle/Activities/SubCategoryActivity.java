package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.paramhans.ecommercesingle.Adapter.ProductAdapter;
import com.paramhans.ecommercesingle.Adapter.ViewpagerCategoryBannerAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.GridSpacingItemDecoration;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.CategoriesBanner;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class SubCategoryActivity extends AppCompatActivity implements ProductAddInterface {
    ViewpagerCategoryBannerAdapter viewpagerCategoryBannerAdapter;
    TabLayout sliderDotspanel;
    ViewPager viewPager;
    RelativeLayout container;
    ArrayList<CategoriesBanner> categoryBanners;
    Timer timer;
    ArrayList<Products> products;
    ProductAdapter productAdapter;
    RecyclerView rv_subcat;
    TextView tv_category_name, tv_desc;
    public static LinearLayout lin_price;
    public static TextView tv_price;
    public static RelativeLayout rv_proceed;
    ImageView iv_back;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        viewPager = findViewById(R.id.viewpager_banner);
        container = findViewById(R.id.container);
        rv_subcat = findViewById(R.id.rv_products);
        sliderDotspanel = findViewById(R.id.slider_dot_panel);
        tv_category_name = findViewById(R.id.tv_category_name);
        iv_back = findViewById(R.id.iv_back);
        lin_price = findViewById(R.id.lin_price);
        tv_desc = findViewById(R.id.tv_desc);
        tv_price = findViewById(R.id.tv_price);
        viewDialog = new ViewDialog(this);
        rv_proceed = findViewById(R.id.rv_proceed);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Intent intent = getIntent();
        if (intent.hasExtra("subcat_id")) {
            setDetails(intent.getStringExtra("subcat_id"));
        }


        rv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubCategoryActivity.this, CheckOutActivity.class));
            }
        });

        ArrayList<CartItem> cartItems = new ArrayList<>();
        SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
        cartItems = sharedPreferenceCart.loadFavorites(SubCategoryActivity.this);

        if (cartItems != null) {
            double sumPr = 0.00;
            for (int i = 0; i < cartItems.size(); i++) {
                String price = cartItems.get(i).getDiscount_price();
                double dp = Double.parseDouble(price);
                sumPr = sumPr + dp;
            }

            if (sumPr > 0.00) {
                rv_proceed.setVisibility(View.VISIBLE);
                tv_price.setText("Total Rs." + sumPr);
            } else {
                rv_proceed.setVisibility(View.GONE);
            }
        }
        //setratecard(intent.getStringExtra("subcat_id"));

    }


    private void setratecard(String subcat_id) {
        if (!CommonI.connectionAvailable(SubCategoryActivity.this)) {
            CommonI.showSnackBar(SubCategoryActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            RequestQueue mRequestQueue = Volley.newRequestQueue(SubCategoryActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.subcat_id, subcat_id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.ratecards,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {


                            JSONArray jsonArray = response.getJSONArray("data");




                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(SubCategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        CommonI.showSnackBar(SubCategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(SubCategoryActivity.this,
                                container,
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

    private void setDetails(String cat_id) {
        if (!CommonI.connectionAvailable(SubCategoryActivity.this)) {
            CommonI.showSnackBar(SubCategoryActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            viewDialog.showDialog();

            categoryBanners = new ArrayList<>();
            products = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(SubCategoryActivity.this, 2);
            rv_subcat.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
            rv_subcat.setItemAnimator(new DefaultItemAnimator());
            rv_subcat.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(SubCategoryActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.subcat_id, cat_id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.subcat_details,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            viewDialog.hideDialog();
                        } else {
                            viewDialog.hideDialog();
                            categoryBanners.clear();
                            products.clear();

                            JSONObject data = response.getJSONObject(JsonVariables.data);
                            JSONArray jsonArray = data.getJSONArray("subcatslider_details");
                            JSONArray subcatProducts = data.getJSONArray("subcat_products");
                            String subcat_name = data.getJSONObject("subcat_details").getString("subcat_name");
                            String subcat_description = data.getJSONObject("subcat_details").getString("subcat_description");

                            tv_category_name.setText(subcat_name);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tv_desc.setText(Html.fromHtml(subcat_description, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tv_desc.setText(Html.fromHtml(subcat_description));
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String slider_id = object.getString(JsonVariables.slider_id);
                                String slider_slider_image = object.getString(JsonVariables.slider_slider_image);
                                categoryBanners.add(new CategoriesBanner(slider_id, slider_slider_image));

                            }

                            for (int j = 0; j < subcatProducts.length(); j++) {
                                JSONObject object = subcatProducts.getJSONObject(j);
                                String pid = object.getString("pid");
                                String name = object.getString(JsonVariables.name);
                                String description = object.getString(JsonVariables.description);
                                String price = object.getString(JsonVariables.price);
                                String discount_price = object.getString("discount_price");
                                String image = object.getString(JsonVariables.image);
                                products.add(new Products(pid, name, description, price, discount_price, image));

                            }

                            rv_subcat.setAdapter(new ProductAdapter(SubCategoryActivity.this, products, SubCategoryActivity.this));


                            viewPager.setAdapter(new ViewpagerCategoryBannerAdapter(categoryBanners, SubCategoryActivity.this));
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new SubCategoryActivity.SliderTimer(), 4000, 6000);
                            sliderDotspanel.setupWithViewPager(viewPager, true);

                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(SubCategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(SubCategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(SubCategoryActivity.this,
                                container,
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
                    viewDialog.hideDialog();
                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                    return super.parseNetworkError(volleyError);
                }

            };

            mRequestQueue.add(objectRequest);

        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void productAdded(boolean b) {

        if (b) {
            ArrayList<CartItem> favouritesBeanSampleList = new ArrayList<>();
            SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
            favouritesBeanSampleList = sharedPreferenceCart.loadFavorites(SubCategoryActivity.this);

            try {
                if (favouritesBeanSampleList != null) {
                    for (int j = 0; j < favouritesBeanSampleList.size(); j++) {


                        CartItem cartIt = favouritesBeanSampleList.get(j);

                        //sum = sum + Double.parseDouble(cartIt.getItemprice());
//                shippingsum = shippingsum + Double.parseDouble(cartIt.getShipingcost());
                    }

                }
            } catch (Exception e) {

            }
        }

    }

    class SliderTimer extends TimerTask {


        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < categoryBanners.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }

                }
            });


        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
