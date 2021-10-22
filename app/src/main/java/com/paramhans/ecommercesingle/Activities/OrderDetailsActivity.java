package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.paramhans.ecommercesingle.Adapter.OrderDetailsOrderItemAdapter;
import com.paramhans.ecommercesingle.Adapter.OrderItemAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.Models.OrderOrder_items;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView tv_order_code, tv_order_amount, tv_order_date, tv_order_status, tv_address;
    RecyclerView rv_order_items;
    OrderItemAdapter orderItemAdapter;
    LinearLayout container;
    private ArrayList<OrderOrder_items> orderOrder_items;
    ImageView iv_back;
    String back ="0";
    TextView tv_delivery_charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        tv_address = findViewById(R.id.tv_address);
        tv_order_code = findViewById(R.id.tv_order_code);
        tv_order_amount = findViewById(R.id.tv_order_amount);
        tv_order_date = findViewById(R.id.tv_order_date);
        tv_order_status = findViewById(R.id.tv_order_status);
        rv_order_items = findViewById(R.id.rv_order_items);
        container = findViewById(R.id.container);
        iv_back = findViewById(R.id.iv_back);
        tv_delivery_charge = findViewById(R.id.tv_delivery_charge);
        Intent intent = getIntent();
        String order_code = intent.getStringExtra("order_code");
        back = intent.getStringExtra("back");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(back.equalsIgnoreCase("1"))
                {
                    startActivity(new Intent(OrderDetailsActivity.this,HomeActivity.class));
                    finish();
                }
                else
                {
                    finish();
                }

            }
        });



        setOrderDetails(order_code);
    }

    private void setOrderDetails(String order_code) {
        if (!CommonI.connectionAvailable(OrderDetailsActivity.this)) {
            CommonI.showSnackBar(OrderDetailsActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            orderOrder_items = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(OrderDetailsActivity.this);
            rv_order_items.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(OrderDetailsActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put("order_code", order_code);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.order_details,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {


                            Log.d("reeeeesssponnse",response.toString());


                            tv_address.setText(response.getJSONObject("data").getJSONObject("address").getString("address_line")+","+response.getJSONObject("data").getJSONObject("address").getString("address_city")+","+response.getJSONObject("data").getJSONObject("address").getString("address_state")+"-"+response.getJSONObject("data").getJSONObject("address").getString("address_pin")+"\n"+response.getJSONObject("data").getJSONObject("address").getString("address_country"));

                            tv_delivery_charge.setText("Rs."+response.getJSONObject("data").getString("delivery_amount"));
                            tv_order_amount.setText("Rs."+response.getJSONObject("data").getString("order_amount"));

                            tv_order_code.setText(response.getJSONObject("data").getString("order_code"));

                            tv_order_date.setText(response.getJSONObject("data").getString("order_date"));
                            tv_order_status.setText(response.getJSONObject("data").getString("order_status"));

                            JSONArray jsonArray = response.getJSONObject("data").getJSONArray("order_items");

                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject catJSONObject = jsonArray.getJSONObject(j);
                                String idq = catJSONObject.getString("id");
                                String qty = catJSONObject.getString("qty");
                                String price = catJSONObject.getString("price");
                                String total_price = catJSONObject.getString("total_price");

                                String product_id = catJSONObject.getString("product_id");
                                String product_name = catJSONObject.getString("product_name");
                                String product_main_img = catJSONObject.getString("product_main_img");
                                String category_id = catJSONObject.getString("category_id");
                                String category_name = catJSONObject.getString("category_name");
                                String subcategory_id = catJSONObject.getString("subcategory_id");
                                String subcategory_name = catJSONObject.getString("subcategory_name");
                                String service_provider_id = catJSONObject.getString("service_provider_id");
                                String service_provider_name = catJSONObject.getString("service_provider_name");

                                orderOrder_items.add(new OrderOrder_items(idq, qty, price, total_price, product_id, product_name, product_main_img,
                                        category_id, category_name, subcategory_id, subcategory_name, service_provider_id, service_provider_name));
                            }

                            rv_order_items.setAdapter(new OrderDetailsOrderItemAdapter(OrderDetailsActivity.this, orderOrder_items));


                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(OrderDetailsActivity.this,
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
                        CommonI.showSnackBar(OrderDetailsActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(OrderDetailsActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(OrderDetailsActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(OrderDetailsActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(OrderDetailsActivity.this,
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(back.equalsIgnoreCase("1"))
        {
            startActivity(new Intent(OrderDetailsActivity.this,HomeActivity.class));
            finish();
        }
        else
        {
            finish();
        }
    }
}
