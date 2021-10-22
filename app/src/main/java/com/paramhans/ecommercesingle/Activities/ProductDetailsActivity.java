package com.paramhans.ecommercesingle.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.paramhans.ecommercesingle.Adapter.RelatedProductAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.GridSpacingItemDecoration;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.Interfaces.ProductAddInterface;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.Products;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProductDetailsActivity extends AppCompatActivity implements ProductAddInterface {

    String name, id, price, discount_price, image, desc;
    ImageView iv_product, iv_back, iv_reduce, iv_add;
    TextView tv_product_name, tv_product_price, tv_product_discount_price, tv_product_desc, tv_quantity, tv_go_to_cart,
            tv_add_to_cart, tv_related_products;
    SharedPreferenceCart sharedPreference = new SharedPreferenceCart();
    String qnttty;
    LinearLayout lin_add_cart;
    int indx;
    RelativeLayout container;
    ViewDialog viewDialog;
    ArrayList<Products> products;
    RecyclerView rv_related_products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        viewDialog = new ViewDialog(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");
        price = intent.getStringExtra("price");
        discount_price = intent.getStringExtra("discount_price");
        image = intent.getStringExtra("image");
        desc = intent.getStringExtra("desc");

        iv_back = findViewById(R.id.iv_back);
        iv_product = findViewById(R.id.iv_product);
        tv_product_name = findViewById(R.id.tv_product_name);
        tv_product_price = findViewById(R.id.tv_product_price);
        tv_product_discount_price = findViewById(R.id.tv_product_discount_price);
        tv_product_desc = findViewById(R.id.tv_product_desc);
        tv_related_products = findViewById(R.id.tv_related_products);
        container = findViewById(R.id.container);
        rv_related_products = findViewById(R.id.rv_related_products);

        tv_go_to_cart = findViewById(R.id.tv_go_to_cart);
        iv_product = findViewById(R.id.iv_product);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_add_to_cart = findViewById(R.id.tv_add_to_cart);
        iv_add = findViewById(R.id.iv_add);
        iv_reduce = findViewById(R.id.iv_reduce);
        lin_add_cart = findViewById(R.id.lin_add_cart);


        Glide.with(ProductDetailsActivity.this).load(image).into(iv_product);

        tv_product_name.setText(name);
        tv_product_price.setText("MRP Rs." + price);
        tv_product_discount_price.setText("Rs. " + discount_price);
        tv_product_price.setPaintFlags(tv_product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_product_desc.setText(Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_product_desc.setText(Html.fromHtml(desc));
        }

        if (checkIfSameItem(id)) {
            tv_add_to_cart.setVisibility(View.GONE);
            lin_add_cart.setVisibility(View.VISIBLE);
            tv_quantity.setText(String.valueOf(qnttty));

        } else {
            tv_add_to_cart.setVisibility(View.VISIBLE);
        }


        tv_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartitem = new CartItem(id, name, desc, price, discount_price, image, "1", "0");
                sharedPreference.addFavorite(ProductDetailsActivity.this, cartitem);
                tv_quantity.setText("1");
                lin_add_cart.setVisibility(View.VISIBLE);
                tv_add_to_cart.setVisibility(View.GONE);
            }
        });

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getindex(id);
                //Toast.makeText(ProductDetailsActivity.this, "" + indx, Toast.LENGTH_SHORT).show();
                int plusValue = Integer.parseInt(tv_quantity.getText().toString()) + 1;
                double t = Double.parseDouble(discount_price) * plusValue;
                sharedPreference.editFavorite(ProductDetailsActivity.this, indx, String.valueOf(t), String.valueOf(plusValue));
                tv_quantity.setText(String.valueOf(plusValue));
            }
        });
        iv_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getindex(id);
                if (Integer.parseInt(tv_quantity.getText().toString()) == 1) {
                    sharedPreference.removeFavorite(ProductDetailsActivity.this, indx);
                    tv_add_to_cart.setVisibility(View.VISIBLE);
                    lin_add_cart.setVisibility(View.GONE);
                } else {
                    int minusValue = Integer.parseInt(tv_quantity.getText().toString()) - 1;
                    tv_quantity.setText(String.valueOf(minusValue));
                    double m = Double.parseDouble(discount_price) * minusValue;
                    sharedPreference.editFavorite(ProductDetailsActivity.this, indx, String.valueOf(m), String.valueOf(minusValue));
                }

            }
        });
        tv_go_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this, CheckOutActivity.class));
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setDetails();

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void setDetails() {
        if (!CommonI.connectionAvailable(ProductDetailsActivity.this)) {
            CommonI.showSnackBar(ProductDetailsActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            viewDialog.showDialog();

            products = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(ProductDetailsActivity.this, 2);
            rv_related_products.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
            rv_related_products.setItemAnimator(new DefaultItemAnimator());
            rv_related_products.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(ProductDetailsActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put("product_id", id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.relative_product,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            viewDialog.hideDialog();
                            tv_related_products.setVisibility(View.GONE);
                        } else {
                            viewDialog.hideDialog();
                            products.clear();

                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() == 0) {
                                tv_related_products.setVisibility(View.GONE);
                            } else {
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject object = jsonArray.getJSONObject(j);
                                    String pid = object.getString("pid");
                                    String name = object.getString(JsonVariables.name);
                                    String description = object.getString(JsonVariables.description);
                                    String price = object.getString(JsonVariables.price);
                                    String discount_price = object.getString("discount_price");
                                    String image = object.getString(JsonVariables.image);
                                    products.add(new Products(pid, name, description, price, discount_price, image));

                                }

                                rv_related_products.setAdapter(new RelatedProductAdapter(ProductDetailsActivity.this, products, ProductDetailsActivity.this));
                            }

                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(ProductDetailsActivity.this,
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
                        CommonI.showSnackBar(ProductDetailsActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(ProductDetailsActivity.this,
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

    public boolean checkIfSameItem(String checkProduct) {
        boolean check = false;
        List<CartItem> cartItems = sharedPreference.loadFavorites(ProductDetailsActivity.this);
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
        List<CartItem> favorites = sharedPreference.loadFavorites(ProductDetailsActivity.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void productAdded(boolean b) {

    }
}
