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
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.paramhans.ecommercesingle.Adapter.PaginationAdapter;
import com.paramhans.ecommercesingle.Adapter.SubCategoriesAdapter;
import com.paramhans.ecommercesingle.Adapter.ViewpagerCategoryBannerAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.GridSpacingItemDecoration;
import com.paramhans.ecommercesingle.CustomViews.PaginationScrollListener;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.CategoriesBanner;
import com.paramhans.ecommercesingle.Models.HomeSubCat;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class CategoryActivity extends AppCompatActivity implements ProductAddInterface {
    ViewpagerCategoryBannerAdapter viewpagerCategoryBannerAdapter;
    TabLayout sliderDotspanel;
    ViewPager viewPager;
    RelativeLayout container;
    ArrayList<CategoriesBanner> categoryBanners;
    Timer timer;
    ArrayList<HomeSubCat> homeSubCats;
    SubCategoriesAdapter subCategoriesAdapter;
    RecyclerView rv_subcat;
    TextView tv_category_name, tv_desc;
    public static LinearLayout lin_price;
    public static TextView tv_price;
    public static RelativeLayout rv_proceed;
    ImageView iv_back;
    ViewDialog viewDialog;
    RecyclerView rv_product;

    String cat_id;


    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES ;
    private int currentPage = PAGE_START;
    ProgressBar progressBar;
    PaginationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        viewPager = findViewById(R.id.viewpager_banner);
        container = findViewById(R.id.container);
        rv_subcat = findViewById(R.id.rv_subcategories);
        sliderDotspanel = findViewById(R.id.slider_dot_panel);
        tv_category_name = findViewById(R.id.tv_category_name);
        viewDialog = new ViewDialog(this);
        lin_price = findViewById(R.id.lin_price);
        tv_price = findViewById(R.id.tv_price);
        tv_desc = findViewById(R.id.tv_desc);
        rv_proceed = findViewById(R.id.rv_proceed);
        rv_product = findViewById(R.id.rv_product);
        iv_back = findViewById(R.id.iv_back);
        progressBar = findViewById(R.id.main_progress);
        Intent intent = getIntent();
        if (intent.hasExtra("cat_id")) {
            setDetails(intent.getStringExtra("cat_id"));
            cat_id = intent.getStringExtra("cat_id");
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<CartItem> cartItems = new ArrayList<>();
        SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
        cartItems = sharedPreferenceCart.loadFavorites(CategoryActivity.this);

        if (cartItems != null) {
            double sumPr = 0.00;
            for (int i = 0; i < cartItems.size(); i++) {
                String price = cartItems.get(i).getDiscount_price();
                double dp = Double.parseDouble(price);
                sumPr = sumPr + dp;
            }

            if (sumPr > 0.00) {
                rv_proceed.setVisibility(View.VISIBLE);
                lin_price.setVisibility(View.VISIBLE);
                tv_price.setText("Total Rs." + sumPr);
            } else {
                lin_price.setVisibility(View.GONE);
            }

        }

        rv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, CheckOutActivity.class));
            }
        });

        adapter = new PaginationAdapter(CategoryActivity.this,CategoryActivity.this);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(CategoryActivity.this,2);
        rv_product.setLayoutManager(mLayoutManager);

        rv_product.setItemAnimator(new DefaultItemAnimator());

        rv_product.setAdapter(adapter);

        rv_product.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setNextProducts();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setFirstProducts();
            }
        }, 1000);

    }

    private void setDetails(String cat_id) {
        if (!CommonI.connectionAvailable(CategoryActivity.this)) {
            CommonI.showSnackBar(CategoryActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            viewDialog.showDialog();
            categoryBanners = new ArrayList<>();
            homeSubCats = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(CategoryActivity.this, 3);
            rv_subcat.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
            rv_subcat.setItemAnimator(new DefaultItemAnimator());
            rv_subcat.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(CategoryActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.cat_id, cat_id);

            Log.d("kjfjdjsdfkj",new JSONObject(params).toString());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.category_banner,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            viewDialog.hideDialog();
                        } else {
                            viewDialog.hideDialog();
                            categoryBanners.clear();
                            homeSubCats.clear();

                            JSONObject data = response.getJSONObject(JsonVariables.data);
                            JSONArray jsonArray = data.getJSONArray("catslider_details");
                            JSONArray jsonArraySubcat = data.getJSONArray("subcatlist");
                            String category_name = data.getJSONObject("category_details").getString("cat_name");
                            String category_desc = data.getJSONObject("category_details").getString("cat_description");

                            tv_category_name.setText(category_name);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tv_desc.setText(Html.fromHtml(category_desc, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tv_desc.setText(Html.fromHtml(category_desc));
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String slider_id = object.getString(JsonVariables.slider_id);
                                String slider_slider_image = object.getString(JsonVariables.slider_slider_image);
                                categoryBanners.add(new CategoriesBanner(slider_id, slider_slider_image));

                            }

                            for (int j = 0; j < jsonArraySubcat.length(); j++) {
                                JSONObject object = jsonArraySubcat.getJSONObject(j);
                                String subcat_id = object.getString(JsonVariables.subcat_id);
                                String subcat_name = object.getString(JsonVariables.subcat_name);
                                String subcat_description = object.getString(JsonVariables.subcat_description);
                                String subcat_image = object.getString(JsonVariables.subcat_image);
                                homeSubCats.add(new HomeSubCat(subcat_id, subcat_name,subcat_image, subcat_description ));
                            }


                            rv_subcat.setAdapter(new SubCategoriesAdapter(CategoryActivity.this, homeSubCats));


                            viewPager.setAdapter(new ViewpagerCategoryBannerAdapter(categoryBanners, CategoryActivity.this));
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
                            sliderDotspanel.setupWithViewPager(viewPager, true);

                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(CategoryActivity.this,
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
                        CommonI.showSnackBar(CategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                         viewDialog.hideDialog();
                        CommonI.showSnackBar(CategoryActivity.this,
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

    private void setFirstProducts() {
        if (!CommonI.connectionAvailable(CategoryActivity.this)) {
            CommonI.showSnackBar(CategoryActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            RequestQueue mRequestQueue = Volley.newRequestQueue(CategoryActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.cat_id, cat_id);
            params.put("page",String.valueOf(currentPage));

            Log.d("lkidladklaklfir",new JSONObject(params).toString());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.catsubcatproduct,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);

                            JSONObject data = response.getJSONObject(JsonVariables.data);
                            JSONArray jsonArray = data.getJSONArray("product_list");
                            ArrayList<Products> products = new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject object = jsonArray.getJSONObject(j);
                                String pid = object.getString("pid");
                                String name = object.getString("catname");
                                String description = object.getString(JsonVariables.description);
                                String price = object.getString(JsonVariables.price);
                                String discount_price = object.getString("discount_price");
                                String image = object.getString(JsonVariables.image);
                                products.add(new Products(pid, name, description, price, discount_price, image));
                            }

                            TOTAL_PAGES = data.getInt("last_page");
                            adapter.addAll(products);

                            if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                            else isLastPage = true;

                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(CategoryActivity.this,
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
                        CommonI.showSnackBar(CategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(CategoryActivity.this,
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

    private void setNextProducts() {
        if (!CommonI.connectionAvailable(CategoryActivity.this)) {
            CommonI.showSnackBar(CategoryActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            RequestQueue mRequestQueue = Volley.newRequestQueue(CategoryActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.cat_id, cat_id);
            params.put("page",String.valueOf(currentPage));

            Log.d("lastsDGAG",new JSONObject(params).toString());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.catsubcatproduct,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            adapter.removeLoadingFooter();
                            isLoading = false;
                        } else {
                            adapter.removeLoadingFooter();
                            isLoading = false;

                            JSONObject data = response.getJSONObject(JsonVariables.data);
                            JSONArray jsonArray = data.getJSONArray("product_list");

                            ArrayList<Products> products = new ArrayList<>();

                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject object = jsonArray.getJSONObject(j);
                                String pid = object.getString("pid");
                                String name = object.getString("catname");
                                String description = object.getString(JsonVariables.description);
                                String price = object.getString(JsonVariables.price);
                                String discount_price = object.getString("discount_price");
                                String image = object.getString(JsonVariables.image);
                                products.add(new Products(pid, name, description, price, discount_price, image));
                            }

                            adapter.addAll(products);

                            if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                            else isLastPage = true;

                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(CategoryActivity.this,
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
                        CommonI.showSnackBar(CategoryActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(CategoryActivity.this,
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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void productAdded(boolean b) {
        if (b) {
            ArrayList<CartItem> favouritesBeanSampleList = new ArrayList<>();
            SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
            favouritesBeanSampleList = sharedPreferenceCart.loadFavorites(CategoryActivity.this);

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
