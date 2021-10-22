package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import com.paramhans.ecommercesingle.Adapter.HomecategoryAdapter;
import com.paramhans.ecommercesingle.Adapter.ProductAdapter;
import com.paramhans.ecommercesingle.Adapter.SubCategoriesAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.GridSpacingItemDecoration;
import com.paramhans.ecommercesingle.Interfaces.CategoryItemClick;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.Models.HomeCategory;
import com.paramhans.ecommercesingle.Models.HomeSubCat;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity implements CategoryItemClick, ProductAddInterface {

    RecyclerView rv_product, rv_subcat, rv_cat;
    ArrayList<HomeCategory> homeCategories;
    LinearLayout container;
    HomecategoryAdapter homecategoryAdapter;

    SubCategoriesAdapter subCategoriesAdapter;
    ArrayList<HomeSubCat> homeSubCats;
    ArrayList<Products> products;
    ProductAdapter productAdapter;
    ImageView back;
    EditText et_search;
    ProgressBar progress_circular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        container = findViewById(R.id.container);
        rv_cat = findViewById(R.id.rv_cat);
        rv_subcat = findViewById(R.id.rv_subcat);
        rv_product = findViewById(R.id.rv_product);
        et_search = findViewById(R.id.et_search);
        back = findViewById(R.id.back);
        progress_circular = findViewById(R.id.progress_circular);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setSearch(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void setSearch(String keyword) {
        if (!CommonI.connectionAvailable(SearchActivity.this)) {
            CommonI.showSnackBar(SearchActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(SearchActivity.this, 3);
            rv_cat.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
            rv_cat.setItemAnimator(new DefaultItemAnimator());
            rv_cat.setLayoutManager(mLayoutManager2);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);

            rv_subcat.setLayoutManager(linearLayoutManager);

            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(SearchActivity.this);
            rv_product.setLayoutManager(linearLayoutManager1);

            homeCategories = new ArrayList<>();
            homeSubCats = new ArrayList<>();
            products = new ArrayList<>();

            progress_circular.setVisibility(View.VISIBLE);

            RequestQueue mRequestQueue = Volley.newRequestQueue(SearchActivity.this);

            Map<String, String> params = new HashMap<String, String>();
            params.put("keywords", keyword);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.search_products,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            progress_circular.setVisibility(View.INVISIBLE);
                            CommonI.showSnackBar(SearchActivity.this,
                                    container,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {

                            progress_circular.setVisibility(View.INVISIBLE);
                            JSONArray categoriesArray = response.getJSONObject("data").getJSONArray("cat_results");

                            homeSubCats.clear();
                            products.clear();
                            homeCategories.clear();

                            for (int i = 0; i < categoriesArray.length(); i++) {
                                // Get current json object
                                JSONObject categoriesArrayJSONObject = categoriesArray.getJSONObject(i);

                                String category_id = categoriesArrayJSONObject.getString(JsonVariables.cat_id);
                                String name = categoriesArrayJSONObject.getString(JsonVariables.cat_name);
                                String photo = categoriesArrayJSONObject.getString("cat_cat_image");

                                homeCategories.add(new HomeCategory(category_id, name, photo));
                            }

                            homecategoryAdapter = new HomecategoryAdapter(SearchActivity.this, homeCategories, SearchActivity.this);
                            rv_cat.setAdapter(homecategoryAdapter);

                            JSONArray subcategoriesArray = response.getJSONObject("data").getJSONArray("subcat_results");
                            for (int j = 0; j < subcategoriesArray.length(); j++) {
                                // Get current json object
                                JSONObject subcategoriesArrayJSONObject = subcategoriesArray.getJSONObject(j);
                                String subcat_id = subcategoriesArrayJSONObject.getString("subcat_id");
                                String subcat_name = subcategoriesArrayJSONObject.getString("subcat_name");
                                String subcat_cat_image = subcategoriesArrayJSONObject.getString("subcat_cat_image");
                                homeSubCats.add(new HomeSubCat(subcat_id, subcat_name, subcat_cat_image, ""));

                            }

                            subCategoriesAdapter = new SubCategoriesAdapter(SearchActivity.this, homeSubCats);
                            rv_subcat.setAdapter(subCategoriesAdapter);


                            JSONArray productArray = response.getJSONObject("data").getJSONArray("product_results");
                            for (int j = 0; j < productArray.length(); j++) {
                                // Get current json object
                                JSONObject subcategoriesArrayJSONObject = productArray.getJSONObject(j);
                                String prod_id = subcategoriesArrayJSONObject.getString("prod_id");
                                String prod_name = subcategoriesArrayJSONObject.getString("prod_name");
                                String prod_image = subcategoriesArrayJSONObject.getString("prod_image");
                                products.add(new Products(prod_id, prod_name, "", "", "", prod_image));

                            }

                            //productAdapter = new ProductAdapter(SearchActivity.this, products, SearchActivity.this);
                            //rv_product.setAdapter(productAdapter);

                        }


                    } catch (JSONException e) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        CommonI.showSnackBar(SearchActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        CommonI.showSnackBar(SearchActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        CommonI.showSnackBar(SearchActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        CommonI.showSnackBar(SearchActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        CommonI.showSnackBar(SearchActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        CommonI.showSnackBar(SearchActivity.this,
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
                v.clearFocus();
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void categoryClick(String id) {
        Intent intent = new Intent(SearchActivity.this, CategoryActivity.class);
        intent.putExtra("cat_id", id);
        startActivity(intent);
    }

    @Override
    public void productAdded(boolean b) {
    }
}
