package com.paramhans.ecommercesingle.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

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
import com.paramhans.ecommercesingle.Activities.CategoryActivity;
import com.paramhans.ecommercesingle.Activities.CheckOutActivity;
import com.paramhans.ecommercesingle.Activities.LocationSearchActivity;
import com.paramhans.ecommercesingle.Activities.SearchActivity;
import com.paramhans.ecommercesingle.Activities.SubCategoryActivity;
import com.paramhans.ecommercesingle.Adapter.BestSellerAdapter;
import com.paramhans.ecommercesingle.Adapter.CategoryBannerAdapter;
import com.paramhans.ecommercesingle.Adapter.HomeCatNameAdapter;
import com.paramhans.ecommercesingle.Adapter.HomecategoryAdapter;
import com.paramhans.ecommercesingle.Adapter.NewlyLaunchedAdapter;
import com.paramhans.ecommercesingle.Adapter.ProductAdapter;
import com.paramhans.ecommercesingle.Adapter.RecomendedproductAdapter;
import com.paramhans.ecommercesingle.Adapter.ViewPagerStaticBannerAdapter;
import com.paramhans.ecommercesingle.Adapter.ViewpagerCategoryBannerAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.GridSpacingItemDecoration;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.Interfaces.CategoryItemClick;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.CategoriesBanner;
import com.paramhans.ecommercesingle.Models.CategoryBanner;
import com.paramhans.ecommercesingle.Models.HomeCatSubCat;
import com.paramhans.ecommercesingle.Models.HomeCategory;
import com.paramhans.ecommercesingle.Models.HomeSubCat;
import com.paramhans.ecommercesingle.Models.NewlyLaunched;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.Models.StaticBanners;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment implements CategoryItemClick, ProductAddInterface {

    private RecyclerView rv_categories, rv_newly_launched,rv_recomended,rv_bestseller;
    private ArrayList<HomeCategory> homeCategories;
    private ArrayList<NewlyLaunched> newlyLauncheds;
    NewlyLaunchedAdapter newlyLaunchedAdapter;
    CoordinatorLayout home;
    HomecategoryAdapter homecategoryAdapter;
    ViewPager2 viewpager_category_banner;
    private ArrayList<CategoryBanner> categoryBanners;
    private Handler sliderhandler = new Handler();
    private Handler sliderhandler2 = new Handler();
    HomeCatNameAdapter homeCatNameAdapter;
    RecyclerView rv_subcategory;
    ArrayList<HomeCatSubCat> homeCatSubCats;
    LinearLayout lin_search;
    ViewDialog viewDialog;
    TextView tv_loaction;
    LinearLayout lin_no_service, lin_content;
    ViewPager2 viewpager_static_banner;
    private ArrayList<StaticBanners> staticBanners;
    public static TextView tv_cart;

    SharedPreferenceCart sharedPreferenceCart;

    ArrayList<Products> recomendedProducts,bestsellerProducts;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        home = v.findViewById(R.id.home);
        rv_categories = v.findViewById(R.id.rv_categories);
        viewpager_category_banner = v.findViewById(R.id.viewpager_category_banner);
        rv_newly_launched = v.findViewById(R.id.rv_newly_launched);
        rv_subcategory = v.findViewById(R.id.rv_subcategory);
        lin_search = v.findViewById(R.id.lin_search);
        tv_loaction = v.findViewById(R.id.tv_loaction);
        lin_content = v.findViewById(R.id.lin_content);
        lin_no_service = v.findViewById(R.id.lin_no_service);
        viewpager_static_banner = v.findViewById(R.id.viewpager_static_banner);
        tv_cart = v.findViewById(R.id.tv_cart);
        rv_recomended = v.findViewById(R.id.rv_recomended);
        rv_bestseller = v.findViewById(R.id.rv_bestseller);
        viewDialog = new ViewDialog(getActivity());
        lin_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        SharedPreferences pref = getActivity().getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
        String latitude = pref.getString(SharedPrefManager.LAT, "");
        String longitude = pref.getString(SharedPrefManager.LONG, "");

        ArrayList<CartItem> cartItems = new ArrayList<>();
        SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
        cartItems = sharedPreferenceCart.loadFavorites(getActivity());

        if (cartItems != null) {
            double sumPr = 0.00;
            for (int i = 0; i < cartItems.size(); i++) {
                String price = cartItems.get(i).getDiscount_price();
                double dp = Double.parseDouble(price);
                sumPr = sumPr + dp;
            }

            if (sumPr > 0.00) {

                tv_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), CheckOutActivity.class));
                    }
                });

            } else {
                tv_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "No products in your cart.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }



        tv_loaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationSearchActivity.class);
                intent.putExtra("intentid", "manage");
                intent.putExtra("add","1");
                startActivity(intent);
            }
        });

        setCategories();
        newlyLaunched();
        //setCatSubCategories();
        staticbanners();

        setRecomendedlist();
        setBestSellerlist();

        return v;
    }


    private void setRecomendedlist() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {


            recomendedProducts = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
            rv_recomended.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.recomended_products,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {

                            recomendedProducts.clear();

                            JSONArray subcatProducts = response.getJSONArray("data");




                            for (int j = 0; j < subcatProducts.length(); j++) {
                                JSONObject object = subcatProducts.getJSONObject(j);
                                String pid = object.getString("pid");
                                String name = object.getString(JsonVariables.name);
                                String description = object.getString(JsonVariables.description);
                                String price = object.getString(JsonVariables.price);
                                String discount_price = object.getString("discount_price");
                                String image = object.getString(JsonVariables.image);
                                recomendedProducts.add(new Products(pid, name, description, price, discount_price, image));

                            }

                            rv_recomended.setAdapter(new RecomendedproductAdapter(getActivity(), recomendedProducts, HomeFragment.this));


                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private void setBestSellerlist() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {


            bestsellerProducts = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getActivity(), 2);
            rv_bestseller.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
            rv_bestseller.setItemAnimator(new DefaultItemAnimator());
            rv_bestseller.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.bestseller_products,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {

                            bestsellerProducts.clear();

                            JSONArray subcatProducts = response.getJSONArray("data");


                            for (int j = 0; j < subcatProducts.length(); j++) {
                                JSONObject object = subcatProducts.getJSONObject(j);
                                String pid = object.getString("pid");
                                String name = object.getString(JsonVariables.name);
                                String description = object.getString(JsonVariables.description);
                                String price = object.getString(JsonVariables.price);
                                String discount_price = object.getString("discount_price");
                                String image = object.getString(JsonVariables.image);
                                bestsellerProducts.add(new Products(pid, name, description, price, discount_price, image));

                            }

                            rv_bestseller.setAdapter(new BestSellerAdapter(getActivity(), bestsellerProducts, HomeFragment.this));


                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private void setLocationName(double latitude, double longitude) {
        viewDialog.showDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + getString(R.string.api_key), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("status").equals("OK")) {
                        //error_message
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                "Something Went Wrong in fetching location.",
                                2000);

                    } else {
                        viewDialog.hideDialog();
                        JSONArray Results = response.getJSONArray("results");
                        JSONObject zero = Results.getJSONObject(0);
                        String testAddress = zero.getString("formatted_address");


                    }

                } catch (JSONException e) {
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(getActivity(),
                            home,
                            Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                            e.getMessage(),
                            2000);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                viewDialog.hideDialog();
                CommonI.showSnackBar(getActivity(),
                        home,
                        Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                        error.getMessage(),
                        2000);
            }
        });

        queue.add(jsonObjectRequest);

    }

    private void setCatSubCategories() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getActivity());
            rv_subcategory.setLayoutManager(mLayoutManager3);
            homeCatSubCats = new ArrayList<>();
            User user = SharedPrefManager.getInstance(getActivity()).getUser();
            String pincode = user.getUser_type();
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

            Map<String, String> params = new HashMap<String, String>();

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.pincode_catsubcat,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            CommonI.showSnackBar(getActivity(),
                                    home,
                                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {
                            JSONArray categoriesArray = response.getJSONArray(JsonVariables.data);

                            for (int i = 0; i < categoriesArray.length(); i++) {
                                // Get current json object
                                JSONObject categoriesArrayJSONObject = categoriesArray.getJSONObject(i);

                                String category_id = categoriesArrayJSONObject.getString(JsonVariables.cat_id);
                                String cat_name = categoriesArrayJSONObject.getString(JsonVariables.cat_name);
                                String cat_icon = categoriesArrayJSONObject.getString(JsonVariables.cat_icon);

                                JSONArray jsonArray = categoriesArrayJSONObject.getJSONArray("cat_subcat");
                                ArrayList<HomeSubCat> homeSubCats = new ArrayList<>();

                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject catJSONObject = jsonArray.getJSONObject(j);
                                    String subcat_id = catJSONObject.getString(JsonVariables.subcat_id);
                                    String subcat_name = catJSONObject.getString(JsonVariables.subcat_name);
                                    String subcat_image = catJSONObject.getString(JsonVariables.subcat_image);
                                    String subcat_description = catJSONObject.getString(JsonVariables.subcat_description);
                                    homeSubCats.add(new HomeSubCat(subcat_id, subcat_name, subcat_image, subcat_description));

                                }


                                homeCatSubCats.add(new HomeCatSubCat(category_id, cat_name, cat_icon, homeSubCats));
                            }
                            homeCatNameAdapter = new HomeCatNameAdapter(getActivity(), homeCatSubCats);
                            rv_subcategory.setAdapter(homeCatNameAdapter);
                        }


                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private void newlyLaunched() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getActivity(),2);
            rv_newly_launched.setLayoutManager(mLayoutManager1);
            newlyLauncheds = new ArrayList<>();

            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());


            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.newlylaunched,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            CommonI.showSnackBar(getActivity(),
                                    home,
                                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {
                            JSONArray categoriesArray = response.getJSONArray(JsonVariables.data);

                            for (int i = 0; i < categoriesArray.length(); i++) {
                                // Get current json object
                                JSONObject categoriesArrayJSONObject = categoriesArray.getJSONObject(i);

                                String category_id = categoriesArrayJSONObject.getString(JsonVariables.cat_id);
                                //String name = categoriesArrayJSONObject.getString(JsonVariables.cat_name);
                                String homepage_banner_id = categoriesArrayJSONObject.getString(JsonVariables.homepage_banner_id);
                                String cat_slider = categoriesArrayJSONObject.getString(JsonVariables.cat_slider);

                                newlyLauncheds.add(new NewlyLaunched(category_id, homepage_banner_id, cat_slider, "AC Service"));
                            }
                            newlyLaunchedAdapter = new NewlyLaunchedAdapter(getActivity(), newlyLauncheds);
                            rv_newly_launched.setAdapter(newlyLaunchedAdapter);
                        }


                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private void staticbanners() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            staticBanners = new ArrayList<>();
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.staticbanners,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            CommonI.showSnackBar(getActivity(),
                                    home,
                                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {



                            JSONArray data = response.getJSONArray(JsonVariables.data);

                            Log.d("rreessssponsseee",response.toString());

                            if(data.length()==0)
                            {
                                viewpager_static_banner.setVisibility(View.GONE);
                            }
                            else
                            {
                                for (int i = 0; i < data.length(); i++) {
                                    // Get current json object
                                    JSONObject categoriesArrayJSONObject = data.getJSONObject(i);

                                    String id = categoriesArrayJSONObject.getString("id");
                                    String banner_image = categoriesArrayJSONObject.getString("banner_image");

                                    staticBanners.add(new StaticBanners(id,banner_image));
                                }

                                viewpager_static_banner.setAdapter(new ViewPagerStaticBannerAdapter(getActivity(), staticBanners, viewpager_static_banner));
                                viewpager_static_banner.setClipToPadding(false);
                                viewpager_static_banner.setClipChildren(false);
                                viewpager_static_banner.setOffscreenPageLimit(3);
                                viewpager_static_banner.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

                                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                                    @Override
                                    public void transformPage(@NonNull View page, float position) {
                                        float r = 1 - Math.abs(position);
                                        page.setScaleY(0.85f + r * 0.15f);
                                    }
                                });

                                viewpager_static_banner.setPageTransformer(compositePageTransformer);
                                viewpager_static_banner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                    @Override
                                    public void onPageSelected(int position) {
                                        super.onPageSelected(position);
                                        sliderhandler2.removeCallbacks(sliderRunnable2);
                                        sliderhandler2.postDelayed(sliderRunnable2, 4000);
                                    }
                                });
                            }

                        }


                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private void setCategoriesBanner() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            categoryBanners = new ArrayList<>();
            User user = SharedPrefManager.getInstance(getActivity()).getUser();
            String pincode = user.getUser_type();
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.pincode, pincode);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.homepagebanner,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                            Toast.makeText(getActivity(), ""+response.getString("msg"), Toast.LENGTH_SHORT).show();
                        } else {

                            categoryBanners.clear();

                            JSONArray data = response.getJSONArray(JsonVariables.data);

                            Log.d("rreessssponsseee",response.toString());

                            if(data.length()==0)
                            {
                                viewpager_category_banner.setVisibility(View.GONE);
                            }
                            else
                                {
                                    for (int x = 0; x < data.length(); x++) {
                                        JSONObject jsonObject = data.getJSONObject(x);
                                        String cat_id = jsonObject.getString(JsonVariables.cat_id);
                                        String homepage_banner_id = jsonObject.getString(JsonVariables.homepage_banner_id);
                                        String cat_slider = jsonObject.getString(JsonVariables.cat_slider);
                                        categoryBanners.add(new CategoryBanner(cat_id, homepage_banner_id, cat_slider));
                                    }

                                    viewpager_category_banner.setAdapter(new CategoryBannerAdapter(getActivity(), categoryBanners, viewpager_category_banner));
                                    viewpager_category_banner.setClipToPadding(false);
                                    viewpager_category_banner.setClipChildren(false);
                                    viewpager_category_banner.setOffscreenPageLimit(3);
                                    viewpager_category_banner.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

                                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                                    compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                                        @Override
                                        public void transformPage(@NonNull View page, float position) {
                                            float r = 1 - Math.abs(position);
                                            page.setScaleY(0.85f + r * 0.15f);
                                        }
                                    });

                                    viewpager_category_banner.setPageTransformer(compositePageTransformer);
                                    viewpager_category_banner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                        @Override
                                        public void onPageSelected(int position) {
                                            super.onPageSelected(position);
                                            sliderhandler.removeCallbacks(sliderRunnable);
                                            sliderhandler.postDelayed(sliderRunnable, 4000);
                                        }
                                    });
                                }




                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                     if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private void setCategories() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            viewDialog.showDialog();
            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getActivity(), 3);
            rv_categories.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
            rv_categories.setItemAnimator(new DefaultItemAnimator());
            rv_categories.setLayoutManager(mLayoutManager2);
            homeCategories = new ArrayList<>();
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.category_list,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {
                            CommonI.showSnackBar(getActivity(),
                                    home,
                                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {
                            JSONArray categoriesArray = response.getJSONArray(JsonVariables.data);

                            for (int i = 0; i < categoriesArray.length(); i++) {
                                // Get current json object
                                JSONObject categoriesArrayJSONObject = categoriesArray.getJSONObject(i);

                                String category_id = categoriesArrayJSONObject.getString(JsonVariables.cat_id);
                                String name = categoriesArrayJSONObject.getString(JsonVariables.cat_name);
                                String photo = categoriesArrayJSONObject.getString(JsonVariables.cat_image);

                                homeCategories.add(new HomeCategory(category_id, name, photo));
                            }
                            homecategoryAdapter = new HomecategoryAdapter(getActivity(), homeCategories, HomeFragment.this);
                            rv_categories.setAdapter(homecategoryAdapter);

                            setCategoriesBanner();
                        }


                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewpager_category_banner.setCurrentItem(viewpager_category_banner.getCurrentItem() + 1);
        }
    };
    private Runnable sliderRunnable2 = new Runnable() {
        @Override
        public void run() {
            viewpager_static_banner.setCurrentItem(viewpager_static_banner.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderhandler.removeCallbacks(sliderRunnable);
        sliderhandler2.removeCallbacks(sliderRunnable2);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderhandler.postDelayed(sliderRunnable, 2000);
        sliderhandler2.postDelayed(sliderRunnable2, 2000);
        ArrayList<CartItem> cartItems = new ArrayList<>();
        SharedPreferenceCart sharedPreferenceCart = new SharedPreferenceCart();
        cartItems = sharedPreferenceCart.loadFavorites(getActivity());

        if (cartItems != null) {
            double sumPr = 0.00;
            for (int i = 0; i < cartItems.size(); i++) {
                String price = cartItems.get(i).getDiscount_price();
                double dp = Double.parseDouble(price);
                sumPr = sumPr + dp;
            }

            if (sumPr > 0.00) {

                tv_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), CheckOutActivity.class));
                    }
                });

            } else {
                tv_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "No products in your cart.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    showExitDialog();
                    return true;
                }

                return false;
            }
        });
    }

    private void showExitDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Do you want to exit from this app.");
        builder1.setTitle("Exit");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        getActivity().finishAffinity();
//                        getActivity().finish();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    @Override
    public void categoryClick(String id) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("cat_id", id);
        getActivity().startActivity(intent);
    }

    @Override
    public void productAdded(boolean b) {

    }
}
