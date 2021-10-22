package com.paramhans.ecommercesingle.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
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

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    ImageView addrs_backicon;
    RelativeLayout relative;
    TextView fragHome_pickUpLocation;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    double lat, lon;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static Handler handler = new Handler();
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private Location currentLocation = null;
    Geocoder geocoder;
    List<Address> addresses;
    String  intid;
    String lt, ln;
    String postalCode = "0";
    LinearLayout lin_no_service;
    String addressst;
    TextView tv_address,tv_current_loaction;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        lin_no_service = findViewById(R.id.lin_no_service);
        tv_address = findViewById(R.id.tv_address);
        tv_current_loaction = findViewById(R.id.tv_current_loaction);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }

        buildGoogleApiClient();

        relative = (RelativeLayout) findViewById(R.id.relative);
        addrs_backicon = (ImageView) findViewById(R.id.addrs_backicon);
        intid = getIntent().getStringExtra("intentid");

        fragHome_pickUpLocation = (TextView)findViewById(R.id.addresslocation) ;
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



        addrs_backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert manager != null;
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

//                    mGoogleApiClient = new GoogleApiClient.Builder(LocationSearchActivity.this)
//                            .addApi(LocationServices.API)
//                            .addConnectionCallbacks(LocationSearchActivity.this)
//                            .addOnConnectionFailedListener(LocationSearchActivity.this).build();
//                    mGoogleApiClient.connect();
                    buildGoogleApiClient();
                    //Toast.makeText(LocationSearchActivity.this, "disabled", Toast.LENGTH_SHORT).show();
                    //buildAlertMessageNoGps();
                }
                else
                {
                    buildGoogleApiClient();
                    runCurrentLocationCode();
                }

            }
        });

        tv_current_loaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert manager != null;
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    mGoogleApiClient = new GoogleApiClient.Builder(LocationSearchActivity.this)
//                            .addApi(LocationServices.API)
//                            .addConnectionCallbacks(LocationSearchActivity.this)
//                            .addOnConnectionFailedListener(LocationSearchActivity.this).build();
//                    mGoogleApiClient.connect();
                    buildGoogleApiClient();
                    //Toast.makeText(LocationSearchActivity.this, "disabled", Toast.LENGTH_SHORT).show();
                    //buildAlertMessageNoGps();
                }
                else
                {
                    buildGoogleApiClient();
                    runCurrentLocationCode();
                }

            }
        });

    }

    public void runCurrentLocationCode()
    {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
//            getAddress();
            buildGoogleApiClient();
//            sendLocation();

//            lt = String.valueOf(lat);
//            ln = String.valueOf(lon);

            Log.d("cordinates", String.valueOf(lat));


            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();


                    lt = String.valueOf(lat);
                    ln = String.valueOf(lon);

                    tv_address.setText(address);

                    //Log.d("plllllacceececc", postalCode);
                    if (postalCode.equals("")) {
                        lin_no_service.setVisibility(View.VISIBLE);
                    } else {
                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                        i.putExtra("intentid",intid);
                        i.putExtra("address", addressst);
                        i.putExtra("lat", lt);
                        i.putExtra("lon", ln);
                        startActivity(i);
                    }
//                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                    i.putExtra("intentid",intid);
//                    i.putExtra("address", address);
//                    i.putExtra("lat", lt);
//                    i.putExtra("lon", ln);
//                    startActivity(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {

            Toast.makeText(this, "OOPs We couldn't find the locality where you are located currently.Please enter the locality manually.", Toast.LENGTH_LONG).show();

        }
    }



    public void runLocationcodes() {
        getLocation();

        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
//            getAddress();
            buildGoogleApiClient();
//            sendLocation();

            Log.d("cordinates", String.valueOf(lat));

            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() > 0) {
                    addressst = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();


                    lt = String.valueOf(lat);
                    ln = String.valueOf(lon);
                    tv_address.setText(addressst);
                    Log.d("plllllacceececc", postalCode);
                    if (postalCode.equals("")) {
                        lin_no_service.setVisibility(View.VISIBLE);
                    } else {
                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                        i.putExtra("intentid",intid);
                        i.putExtra("address", addressst);
                        i.putExtra("lat", lt);
                        i.putExtra("lon", ln);
                        startActivity(i);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {



        }
    }

    private void getLocation() {


        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
//                        tbt1.setChecked(true);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            getLocation();
//        if(isOnline()) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                return;
            }

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                currentLocation = mLastLocation;


//                changeMap(mLastLocation);
//            Log.d(TAG, "ON connected");

            } else

                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);


            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);


//        }else{
//
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }


    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
//                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        //Toast.makeText(this, ""+requestCode, Toast.LENGTH_SHORT).show();

        if (requestCode==2000) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    // All required changes were successfully made
                    buildGoogleApiClient();
                    dialog = new ProgressDialog(LocationActivity.this);
                    dialog.setMessage("Searching.....");
                    dialog.show();
                    new CountDownTimer(4000, 1000) { //Set Timer for 5 seconds
                        int time=30;
                        public void onTick(long millisUntilFinished)
                        {

                        }
                        @Override
                        public void onFinish() {
                            if(dialog !=null)
                            {
                                dialog.dismiss();
                            }
                            runCurrentLocationCode();
                        }
                    }.start();
                    //Toast.makeText(LocationSearchActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, "OOPs We couldn't find the locality where you are located currently.Please enter the locality manually.", Toast.LENGTH_LONG).show();
                    break;
                }
                default: {
                    break;
                }
            }
        }
        else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == AutocompleteActivity.RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(intent);
                fragHome_pickUpLocation.setText(place.getName() + ",\n" +
                        place.getAddress());


//                mMap.clear();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                Log.d("plllaaaceee", place.getId());

                Log.d("piklat", String.valueOf(lat));

                lt = String.valueOf(lat);
                ln = String.valueOf(lon);
                returnPincode(place.getId());


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

    private void returnPincode(String place_id) {
        RequestQueue queue = Volley.newRequestQueue(LocationActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/place/details/json?key=" + getString(R.string.api_key) + "&placeid=" + place_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray addressComponents = response.getJSONObject("result").getJSONArray("address_components");
                    for (int i = 0; i < addressComponents.length(); i++) {
                        JSONArray typesArray = addressComponents.getJSONObject(i).getJSONArray("types");
                        for (int j = 0; j < typesArray.length(); j++) {
                            if (typesArray.get(j).toString().equalsIgnoreCase("postal_code")) {
                                postalCode = addressComponents.getJSONObject(i).getString("long_name");
                            }
                        }
                    }

                    if (postalCode.equals("0")) {
                        lin_no_service.setVisibility(View.VISIBLE);
                    } else {
                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                        i.putExtra("intentid",intid);
                        i.putExtra("address", addressst);
                        i.putExtra("lat", lt);
                        i.putExtra("lon", ln);
                        startActivity(i);
                    }


                    //Log.d("postttalllcode", postalCode);

                } catch (JSONException e) {
                    Toast.makeText(LocationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LocationActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }
}
