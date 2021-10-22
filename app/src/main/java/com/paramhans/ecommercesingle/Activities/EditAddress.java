package com.paramhans.ecommercesingle.Activities;

import android.Manifest;
import android.content.Intent;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class EditAddress  extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    Geocoder geocoder;
    List<Address> addresses;
    TextView fragHome_pickUpLocation;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    double pickLat, pickLong;
    EditText fulladdress, edt_city, edt_state, edt_country, edt_pin;
    String city, state, country, postcode, ltt, lnn, add_id, f_address, value;
    GoogleApiClient mGoogleApiClient;
    ImageView loc;
    Button addressproceed;
    RelativeLayout rel_main_login;
    EditText locality;
    ImageView pin,back_btn;
    TextView tv_home, tv_work;
    String type ="Home";
    ViewDialog viewDialog;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        rel_main_login = findViewById(R.id.rel_edit_address);
        locality = findViewById(R.id.locality);
        back_btn = findViewById(R.id.back_btn);
        viewDialog = new ViewDialog(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addressproceed = (Button) findViewById(R.id.addressproceed);
        loc = (ImageView) findViewById(R.id.myLocationCustomButton);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key), Locale.US);
        }

        f_address = getIntent().getStringExtra("f_address");
        add_id = getIntent().getStringExtra("add_id");
        ltt = getIntent().getStringExtra("lat");
        lnn = getIntent().getStringExtra("lon");
        city = getIntent().getStringExtra("city");
        state = getIntent().getStringExtra("state");
        country = getIntent().getStringExtra("country");
        postcode = getIntent().getStringExtra("pin");
        pickLat = Double.parseDouble(ltt);
        pickLong = Double.parseDouble(lnn);

        try {
            addresses = geocoder.getFromLocation(pickLat, pickLong, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postcode = addresses.get(0).getPostalCode();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_state = (EditText) findViewById(R.id.edt_state);
        edt_country = (EditText) findViewById(R.id.edt_country);
        edt_pin = (EditText) findViewById(R.id.edt_postcode);
        fulladdress = (EditText) findViewById(R.id.fulladdress);

        pin = findViewById(R.id.pick_marker);

        Glide.with(EditAddress.this).load(R.drawable.map_pin).into(pin);
        fulladdress.setText(f_address);
        edt_city.setText(city);
        edt_state.setText(state);
        edt_country.setText(country);
        edt_pin.setText(postcode);

        fragHome_pickUpLocation = (TextView) findViewById(R.id.addresslocation);
        fragHome_pickUpLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent autocompleteIntent =
                            new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, getPlaceFields())
                                    .build(getApplicationContext());
                    startActivityForResult(autocompleteIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (Exception e) {
                    // TODO: Handle the error.
                    Log.d("pwsdfv", String.valueOf(e));
                }

            }
        });

        tv_home = findViewById(R.id.tv_home);
        tv_work = findViewById(R.id.tv_work);

        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_home.setBackground(getResources().getDrawable(R.drawable.type_bg));
                tv_work.setBackground(getResources().getDrawable(R.drawable.btn_simple_bg));
                tv_work.setTextColor(getResources().getColor(R.color.gray));
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



        addressproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               checkPin();




            }
        });
    }

    private void checkAvailability(final String pincode) {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            viewDialog.showDialog();
            RequestQueue requestQueue = Volley.newRequestQueue(EditAddress.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.pincode, pincode);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.check_pincode,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        if (response.getInt(JsonVariables.status) == 1) {

                            viewDialog.hideDialog();
                        } else {
                            // lin_no_service.setVisibility(View.VISIBLE);
                            viewDialog.hideDialog();
                            dialogNotAvailable();
                            //Toast.makeText(EditAddress.this, "Not available in this location", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(EditAddress.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        viewDialog.hideDialog();
                        Toast.makeText(EditAddress.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        Toast.makeText(EditAddress.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

            requestQueue.add(objectRequest);
        }

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

    private void saveAddress() {
        if (!CommonI.connectionAvailable(this)) {
            CommonI.showSnackBar(EditAddress.this,
                    rel_main_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    3000);
        } else {
            User user = SharedPrefManager.getInstance(EditAddress.this).getUser();
            String id = user.getId();
            RequestQueue requestQueue = Volley.newRequestQueue(EditAddress.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put("address_id", add_id);
            params.put("address_line", f_address+","+locality.getText().toString());
            params.put("city", city);
            params.put("state", state);
            params.put("country", type);
            params.put("pincode", postcode);
            params.put("latitude", ltt);
            params.put("longitude", lnn);
            params.put("user_id", id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.editaddress,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {

                    try {
                        if (response.getString("status").equals("0")) {

                            CommonI.showSnackBar(EditAddress.this,
                                    rel_main_login,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"), response.getString("msg"),
                                    2000);
                        } else {


                            String success = response.getString("status");

                            if (success.equalsIgnoreCase("1")) {


                                Toast.makeText(EditAddress.this, "Address Updated Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }


                        }


                    } catch (JSONException e) {
                        CommonI.showSnackBar(EditAddress.this,
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

                        CommonI.showSnackBar(EditAddress.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        // processing(false, "");
                        CommonI.showSnackBar(EditAddress.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {

                        CommonI.showSnackBar(EditAddress.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        //processing(false, "");

                        CommonI.showSnackBar(EditAddress.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        //processing(false, "");

                        CommonI.showSnackBar(EditAddress.this,
                                rel_main_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);

                    } else if (error instanceof ParseError) {
                        //processing(false, "");

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
                        postcode = addresses.get(0).getPostalCode();
//            Log.d("sonuaddress". address);
                        checkAvailability(postcode);
                        fragHome_pickUpLocation.setText(address);
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


    private void checkPin() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(EditAddress.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.pincode, postcode);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.check_pincode,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 1) {

                            f_address = fulladdress.getText().toString();
                            city = edt_city.getText().toString();
                            state = edt_state.getText().toString();
                            country = edt_country.getText().toString();
                            postcode = edt_pin.getText().toString();

                            ltt = String.valueOf(pickLat);
                            lnn = String.valueOf(pickLong);

                            saveAddress();


                        } else {

                            // lin_no_service.setVisibility(View.VISIBLE);

                            dialogNotAvailable();
                            Toast.makeText(EditAddress.this, "We are not available here.Please change your location.", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(EditAddress.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   if (error instanceof ServerError) {
                        Toast.makeText(EditAddress.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(EditAddress.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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

            requestQueue.add(objectRequest);
        }
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
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postcode = addresses.get(0).getPostalCode();
                checkAvailability(postcode);

                fragHome_pickUpLocation.setText(address);


            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("dfgvcds", String.valueOf(e));
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent intent) {
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {


            if (resultCode == AutocompleteActivity.RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(intent);
                fragHome_pickUpLocation.setText(place.getName() + ",\n" +
                        place.getAddress());

//                mMap.clear();
                pickLat = place.getLatLng().latitude;
                pickLong = place.getLatLng().longitude;

                Log.d("piklat", String.valueOf(pickLat));

                String lt = String.valueOf(pickLat);
                String ln = String.valueOf(pickLong);

                Log.d("piklatlong", pickLat + "' " + pickLong);

                LatLng sydney = new LatLng(pickLat, pickLong);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                // Zoom in, animating the camera.
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney)
                        .zoom(17)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//                red.setVisibility(View.GONE);
//                green.setVisibility(View.VISIBLE);

                try {
                    addresses = geocoder.getFromLocation(pickLat, pickLong, 1);
                    if (addresses != null && addresses.size() > 0) {
                        f_address = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        postcode = addresses.get(0).getPostalCode();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(intent);
                Log.d("ptfki", status.getStatusMessage());
                Toast.makeText(getApplicationContext(), "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }


        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    static List<Place.Field> getPlaceFields(Place.Field... placeFieldsToOmit) {
        // Arrays.asList is immutable, create a mutable list to allow removing fields
        List<Place.Field> placeFields = new ArrayList<>(Arrays.asList(Place.Field.values()));
        placeFields.removeAll(Arrays.asList(placeFieldsToOmit));

        return placeFields;
    }
}
