package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class AboutUsActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_about_us;
    RelativeLayout container;
    ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        iv_back = findViewById(R.id.iv_back);
        tv_about_us = findViewById(R.id.tv_about_us);
        container = findViewById(R.id.container);
        viewDialog = new ViewDialog(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSellerDetails();
    }

    private void setSellerDetails() {
        if (!CommonI.connectionAvailable(AboutUsActivity.this)) {

        } else {

            viewDialog.showDialog();
            RequestQueue mRequestQueue = Volley.newRequestQueue(AboutUsActivity.this);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.about_us,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            viewDialog.hideDialog();
                            CommonI.showSnackBar(AboutUsActivity.this,
                                    container,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {
                            viewDialog.hideDialog();
                            String about_us = response.getJSONObject(JsonVariables.data).getString("about_us");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tv_about_us.setText(Html.fromHtml(about_us, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tv_about_us.setText(Html.fromHtml(about_us));
                            }

                        }


                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(AboutUsActivity.this,
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
                        CommonI.showSnackBar(AboutUsActivity.this,
                                container,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(AboutUsActivity.this,
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
