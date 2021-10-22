package com.paramhans.ecommercesingle.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.paramhans.ecommercesingle.Adapter.AddressAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.Interfaces.AddressItemClick;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.AdressModel;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddressActivity extends AppCompatActivity implements AddressItemClick {

    ImageView addrs_backicon;
    Button add_address, add_proceed;

    String addressId, intid;
    RecyclerView rv_address;
    String value, address, addresses;
    RelativeLayout rel_main_login;
    TextView no_address,saved_add;
    ArrayList<AdressModel> adressModels;
    AddressAdapter addressAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        rv_address = (RecyclerView) findViewById(R.id.recycle_address);
        add_address = (Button) findViewById(R.id.add_address);
        add_proceed = (Button) findViewById(R.id.add_proceed);
        rel_main_login = findViewById(R.id.rel_main_address);
        no_address = findViewById(R.id.no_address);
        saved_add = findViewById(R.id.saved_add);

        if(getIntent().hasExtra("intentid"))
        {
            intid = getIntent().getStringExtra("intentid");

            if (getIntent().getStringExtra("intentid").equalsIgnoreCase("manage")) {
                add_proceed.setVisibility(View.GONE);
            } else if (getIntent().getStringExtra("intentid").equalsIgnoreCase("cart")) {
                add_proceed.setVisibility(View.VISIBLE);
            }

        }
        else
            {

            }

        addrs_backicon = (ImageView) findViewById(R.id.addrs_backicon);

        setAddresses();



        add_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startActivity(new Intent(AddressActivity.this,CheckOutActivity.class));

            }

        });


        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this, LocationActivity.class);
                intent.putExtra("intentid", intid);
                startActivity(intent);

            }
        });

        addrs_backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAddresses();
    }

    private void setAddresses() {
        if (!CommonI.connectionAvailable(AddressActivity.this)) {
            CommonI.showSnackBar(AddressActivity.this,
                    rel_main_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        }
        else {

            RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(AddressActivity.this);
            rv_address.setLayoutManager(mLayoutManager3);
            adressModels = new ArrayList<>();
            User user = SharedPrefManager.getInstance(AddressActivity.this).getUser();
            String user_id = user.getId();

            RequestQueue mRequestQueue = Volley.newRequestQueue(AddressActivity.this);
            Map<String, String> params = new HashMap<String, String>();

            params.put(JsonVariables.customer_id, user_id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.get_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        adressModels.clear();
                        if (response.getInt(JsonVariables.status) == 0) {
                            no_address.setVisibility(View.VISIBLE);
                            add_proceed.setVisibility(View.GONE);

//                            Intent intent = new Intent(AddressActivity.this, LocationActivity.class);
//                            intent.putExtra("intentid", intid);
//                            startActivity(intent);

                        } else {


                            JSONArray jsonArray = response.getJSONArray(JsonVariables.data);
                            no_address.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                // Get current json object
                                JSONObject productsJSONObject = jsonArray.getJSONObject(i);

                                String address_id = productsJSONObject.getString(JsonVariables.address_id);
                                String address_1 = productsJSONObject.getString(JsonVariables.address_line);
                                //String address_2 = productsJSONObject.getString(JsonVariables.address_2);
                                String city = productsJSONObject.getString(JsonVariables.city);
                                String state = productsJSONObject.getString(JsonVariables.state);
                                String country = productsJSONObject.getString(JsonVariables.country);

                                String pincode = productsJSONObject.getString(JsonVariables.pincode);
                                String latitude = productsJSONObject.getString(JsonVariables.latitude);
                                String longitude = productsJSONObject.getString(JsonVariables.longitude);
                                String is_default = productsJSONObject.getString(JsonVariables.is_default);

                                if (is_default.equals("1")) {
                                    addressId = address_id;
                                }

                                adressModels.add(new AdressModel(address_1, "", city, state, country, pincode, latitude, longitude, is_default, address_id));
                            }

                        }

                        addressAdapter = new AddressAdapter(AddressActivity.this, adressModels, AddressActivity.this);
                        rv_address.setAdapter(addressAdapter);

                    } catch (JSONException e) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {

                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
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

    private void setDefaultAddress(final String address_id) {
        if (!CommonI.connectionAvailable(AddressActivity.this)) {
            CommonI.showSnackBar(AddressActivity.this,
                    rel_main_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            User user = SharedPrefManager.getInstance(AddressActivity.this).getUser();
            String user_id = user.getId();

            RequestQueue mRequestQueue = Volley.newRequestQueue(AddressActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, user_id);
            params.put(JsonVariables.address_id, address_id);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.set_default_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            CommonI.showSnackBar(AddressActivity.this,
                                    rel_main_login,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {

                            //Toast.makeText(AdressActivity.this, ""+address_id, Toast.LENGTH_SHORT).show();
                            setAddresses();

                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {

                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {

                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {


                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {

                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {

                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
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
    private void deleteAddress(String address_id) {

        if (!CommonI.connectionAvailable(AddressActivity.this)) {
            CommonI.showSnackBar(AddressActivity.this,
                    rel_main_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            User user = SharedPrefManager.getInstance(AddressActivity.this).getUser();
            String user_id = user.getId();

            RequestQueue mRequestQueue = Volley.newRequestQueue(AddressActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, user_id);
            params.put(JsonVariables.address_id, address_id);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.deleteaddress,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            CommonI.showSnackBar(AddressActivity.this,
                                    rel_main_login,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {
                            setAddresses();
                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(AddressActivity.this,
                                rel_main_login,
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
    public void remove(AdressModel adressModel) {
        deleteAddress(adressModel.getAddress_id());
    }

    @Override
    public void edit(AdressModel adressModel) {
        String address_id = adressModel.getAddress_id();
        String faddress = adressModel.getAddress_1();
        String lat =adressModel.getLatitude();
        String lon = adressModel.getLongitude();
        String city = adressModel.getCity();
        String state = adressModel.getState();
        String country = adressModel.getCountry();
        String postcode = adressModel.getPincode();
        Intent i = new Intent(AddressActivity.this, EditAddress.class);
        i.putExtra("f_address", faddress);
        i.putExtra("add_id", address_id);
        i.putExtra("lat", lat);
        i.putExtra("lon", lon);
        i.putExtra("city", city);
        i.putExtra("state", state);
        i.putExtra("country", country);
        i.putExtra("postcode", postcode);
        startActivity(i);
    }

    @Override
    public void defaultClick(AdressModel adressModel) {
        setDefaultAddress(adressModel.getAddress_id());
    }
}
