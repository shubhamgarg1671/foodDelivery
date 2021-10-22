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

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paramhans.ecommercesingle.Adapter.RateCardAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.Models.RateCard;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RatecardActivity extends AppCompatActivity {
    ImageView iv_back;
    RecyclerView rv_ratecard;
    private ArrayList<RateCard> rateCards;
    LinearLayout container;
    String subcat_id;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratecard);
        iv_back = findViewById(R.id.iv_back);
        rv_ratecard = findViewById(R.id.rv_ratecard);
        container = findViewById(R.id.container);
        viewDialog = new ViewDialog(this);
        Intent intent = getIntent();
        subcat_id = intent.getStringExtra("subcat_id");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setDetails();
    }

    private void setDetails() {
        if (!CommonI.connectionAvailable(RatecardActivity.this)) {
            CommonI.showSnackBar(RatecardActivity.this,
                    container,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            viewDialog.showDialog();
            rateCards = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(RatecardActivity.this);
            rv_ratecard.setLayoutManager(mLayoutManager2);
            RequestQueue mRequestQueue = Volley.newRequestQueue(RatecardActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.subcat_id, subcat_id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.ratecards,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {


                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);


                                String description = object.getString("ratechat_name");
                                String price = object.getString("ratechat_price");

                                rateCards.add(new RateCard(description, price));

                            }

                            rv_ratecard.setAdapter(new RateCardAdapter(RatecardActivity.this, rateCards));


                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(RatecardActivity.this,
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
                        CommonI.showSnackBar(RatecardActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(RatecardActivity.this,
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
}
