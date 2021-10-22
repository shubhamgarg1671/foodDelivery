package com.paramhans.ecommercesingle.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    double pickLat, pickLong;
    ImageView loc;
    TextView addresslocation, tv_home, tv_work;
    Geocoder geocoder;
    List<Address> addresses;
    String ltt, lnn, city, state, country, intid;
    Button addressproceed;
    ImageView pin;
    RelativeLayout rel_main_login;
    EditText locality;
    String type = "Home";
    String postalCodecheck = "0";
    ViewDialog viewDialog;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rel_main_login = findViewById(R.id.rel_maps);
        locality = findViewById(R.id.locality);
        viewDialog = new ViewDialog(this);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        loc = (ImageView) findViewById(R.id.myLocationCustomButton);
        addresslocation = (TextView) findViewById(R.id.addresslocation);
        addressproceed = (Button) findViewById(R.id.addressproceed);
        tv_home = findViewById(R.id.tv_home);
        tv_work = findViewById(R.id.tv_work);
        pin = findViewById(R.id.pick_marker);
        intid = getIntent().getStringExtra("intentid");

        Glide.with(MapsActivity.this).load(R.drawable.map_pin).into(pin);

        ltt = getIntent().getStringExtra("lat");
        lnn = getIntent().getStringExtra("lon");
        pickLat = Double.parseDouble(ltt);
        pickLong = Double.parseDouble(lnn);

        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_home.setBackground(getResources().getDrawable(R.drawable.btn_simple_bg));
                tv_work.setBackground(getResources().getDrawable(R.drawable.type_bg));
                tv_work.setTextColor(getResources().getColor(R.color.black));
                type = "Home";
            }
        });

        tv_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_work.setBackground(getResources().getDrawable(R.drawable.btn_simple_bg));
                tv_home.setBackground(getResources().getDrawable(R.drawable.type_bg));
                tv_work.setTextColor(getResources().getColor(R.color.black));
                type = "Work";
            }
        });

        try {
            addresses = geocoder.getFromLocation(pickLat, pickLong, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCodecheck = addresses.get(0).getPostalCode();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        addresslocation.setText(getIntent().getStringExtra("address"));
//
        addressproceed = (Button) findViewById(R.id.addressproceed);
        addressproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddress();
            }
        });
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickLat, pickLong), 16.0f));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
                pickLat = location.getLatitude();
                pickLong = location.getLongitude();

//                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        postalCodecheck = addresses.get(0).getPostalCode();


                        addresslocation.setText(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                Log.d("Driver", "Driver");


//            endMarker = mMap.addMarker(new MarkerOptions().position(latLong).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.green_pin)));
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15));
                // Zoom in, animating the camera.
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong)
                        .zoom(17)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d("ltlnposition", String.valueOf(mMap.getCameraPosition().target.latitude) + ", " + String.valueOf(mMap.getCameraPosition().target.longitude));


                CameraAction();


            }
        });


    }




    private void dialogNotAvailable() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_not_available, null);
        dialog = new AlertDialog.Builder(this).create();
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setView(view);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;
        dialog.show();

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CameraAction() {

        pickLat = mMap.getCameraPosition().target.latitude;
        pickLong = mMap.getCameraPosition().target.longitude;

        Log.d("pickupln", String.valueOf(pickLat) + ", " + String.valueOf(pickLong));

//                        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(pickLat, pickLong, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                postalCodecheck = addresses.get(0).getPostalCode();

                addresslocation.setText(address);

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("dfgvcds", String.valueOf(e));
        }

    }


    private void saveAddress() {
        if (!CommonI.connectionAvailable(this)) {
            CommonI.showSnackBar(MapsActivity.this,
                    rel_main_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    3000);
        } else {
            User user = SharedPrefManager.getInstance(MapsActivity.this).getUser();
            String id = user.getId();

            RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

            Map<String, String> params = new HashMap<String, String>();

            params.put("address_line", addresslocation.getText().toString() + "," + locality.getText().toString());
            params.put("city", city);
            params.put("state", state);
            params.put("country", type);
            params.put("pincode", postalCodecheck);
            params.put("latitude", ltt);
            params.put("longitude", lnn);
            params.put("customer_id", id);

            Log.d("sjdkakjsdk", new JSONObject(params).toString());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.addAddress,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {

                    try {
                        if (response.getString("status").equals("0")) {

                            CommonI.showSnackBar(MapsActivity.this,
                                    rel_main_login,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"), response.getString("msg"),
                                    2000);
                        } else {
                            String success = response.getString("status");

                            if (success.equalsIgnoreCase("1")) {
                                String address_id = response.getString("address_id");
                                SharedPreferences pref = getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
                                SharedPreferences.Editor edt = pref.edit();
                                edt.putString(SharedPrefManager.LAT, ltt);
                                edt.putString(SharedPrefManager.LONG, lnn);
                                edt.putString(SharedPrefManager.ADDRESS_ID, address_id);
                                edt.putString(SharedPrefManager.ADDRESS, addresslocation.getText().toString() + "," + locality.getText().toString() + "-[" + type + "]");

                                //Toast.makeText(context, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                                edt.apply();

                                User user1 = SharedPrefManager.getInstance(MapsActivity.this).getUser();

                                User user = new User();

                                user.setId(user1.getId());
                                user.setEmail(user1.getEmail());
                                user.setMobile(user1.getMobile());
                                user.setName(user1.getName());
                                user.setUser_type(postalCodecheck);
                                SharedPrefManager.getInstance(MapsActivity.this).userLogin(user);

                                setDefaultAddress(address_id);

                            }

                        }


                    } catch (JSONException e) {
                        CommonI.showSnackBar(MapsActivity.this,
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
                        //processing(false, "");

                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {

                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {

                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        //processing(false, "");

                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        //processing(false, "");

                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);

                    } else if (error instanceof ParseError) {
                        //processing(false, "");

                        Toast.makeText(MapsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("parse_error", error.getMessage());
                    }

                }
            }) {
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    //processing(false, "");

                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                    return super.parseNetworkError(volleyError);
                }

            };

            requestQueue.add(objectRequest);
        }
    }


    private void setDefaultAddress(final String address_id) {
        if (!CommonI.connectionAvailable(MapsActivity.this)) {
            CommonI.showSnackBar(MapsActivity.this,
                    rel_main_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            viewDialog.showDialog();
            User user = SharedPrefManager.getInstance(MapsActivity.this).getUser();
            String user_id = user.getId();

            RequestQueue mRequestQueue = Volley.newRequestQueue(MapsActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, user_id);
            params.put(JsonVariables.address_id, address_id);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.set_default_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
                            viewDialog.hideDialog();
                            CommonI.showSnackBar(MapsActivity.this,
                                    rel_main_login,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                        } else {
                            viewDialog.hideDialog();
                            if (intid.equalsIgnoreCase("cart")) {
                                Intent intent = new Intent(MapsActivity.this, AddressActivity.class);
                                intent.putExtra("intentid", intid);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(MapsActivity.this, HomeActivity.class);
                                intent.putExtra("profile", "1");
                                startActivity(intent);
                                finish();
                            }

                            Toast.makeText(MapsActivity.this, "Address saved", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
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
                        CommonI.showSnackBar(MapsActivity.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(MapsActivity.this,
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
                    viewDialog.hideDialog();

                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                    return super.parseNetworkError(volleyError);
                }

            };

            mRequestQueue.add(objectRequest);

        }
    }

}
