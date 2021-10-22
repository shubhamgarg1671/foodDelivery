package com.paramhans.ecommercesingle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import net.alexandroid.gps.GpsStatusDetector;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GpsStatusDetector.GpsStatusDetectorCallBack {
    MaterialCardView mat_iv_logo;
    Animation animFadeIn, animdown;
    TextView tv_tag;
    private GpsStatusDetector mGpsStatusDetector;
    private Context context;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private boolean isPermitted;
    boolean check_gps = false;
    private LocationManager locationManager;
    private static final String TAG = MainActivity.class.getSimpleName();

    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private ProgressDialog progressDialog;
    ConstraintLayout rel_main_login;

    String forceUpdate, latestVersion, currentVersion, features;
    Dialog forceUpdateDialog, softUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mat_iv_logo = findViewById(R.id.mat_iv_logo);
        tv_tag = findViewById(R.id.tv_tag);
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
        animdown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
        mat_iv_logo.startAnimation(animFadeIn);
        tv_tag.startAnimation(animdown);
        rel_main_login = findViewById(R.id.container);


        init();
        //progressBar = findViewById(R.id.progressBar_load);
        mGpsStatusDetector = new GpsStatusDetector(this);

        context = getApplicationContext();
        activity = this;

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("32wfegh1", "getInstanceId failed", task.getException());
                            return;
                        }

                        String newToken = task.getResult().getToken();

                        FirebaseMessaging.getInstance().subscribeToTopic("OLLOF");
                        Log.d("fdghghjghj", newToken);

                    }
                });

        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        currentVersion = packageInfo.versionName;
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkRunTimePermission();
    }


    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            //Toast.makeText(context, ""+mCurrentLocation.getLongitude()+mCurrentLocation.getLatitude(), Toast.LENGTH_SHORT).show();
            SharedPreferences pref = getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();

            String latitude = pref.getString(SharedPrefManager.LAT, "");
            String longitude = pref.getString(SharedPrefManager.LONG, "");
            if (latitude == "") {
                edt.putString(SharedPrefManager.LAT, String.valueOf(mCurrentLocation.getLatitude()));
                edt.putString(SharedPrefManager.LONG, String.valueOf(mCurrentLocation.getLongitude()));
                edt.apply();

                User user = SharedPrefManager.getInstance(MainActivity.this).getUser();
                String id = user.getId();
                if (id == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, 500);
                } else {
                    String pincode = user.getUser_type();
                    if (pincode == null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, LocationSearchActivity.class));
                                finish();
                            }
                        }, 500);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        }, 500);
                    }

                }

            } else {
                User user = SharedPrefManager.getInstance(MainActivity.this).getUser();
                String id = user.getId();
                if (id == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, 500);
                } else {
                    String pincode = user.getUser_type();
                    if (pincode == null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, LocationSearchActivity.class));
                                finish();
                            }
                        }, 500);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        }, 500);
                    }

                }

            }

            //Toast.makeText(context, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();


            mFusedLocationClient
                    .removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });

        }

    }

    private void startLocationUpdates() {

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });

    }


    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mGpsStatusDetector.checkGpsStatus();
            requestPermissions(permissionArrays, 11111);
        } else {
            mGpsStatusDetector.checkGpsStatus();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11111) {
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];
                isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isGpsEnabled(MainActivity.this) && isPermitted) {

                        startLocationUpdates();

                    }
                } else {

                    if (isGpsEnabled(MainActivity.this)) {
                        startLocationUpdates();
                    }

                }


                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permission);
                    }
                    if (!showRationale) {

                        new AlertDialog.Builder(this).
                                setTitle(R.string.dialog_permission_title)
                                .setMessage("Permission are required, grant the permission from settings")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();

                    } else {
                        alertView();
                    }
                }
            }
        }
    }

    AlertDialog.Builder dialog;

    private void alertView() {
        dialog = new AlertDialog.Builder(MainActivity.this);

        dialog.setTitle(getString(R.string.location_permission_denied))
                .setInverseBackgroundForced(true)
                .setMessage(getString(R.string.on_permission_denied))
                .setNegativeButton(getString(R.string.im_sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                        finish();
                    }
                })
                .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                        checkRunTimePermission();

                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGpsSwitchStateReceiver);

    }

    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                //MyLog.d("Is gps enabled: " + isGpsEnabled(MainActivity.this));
                if (!isGpsEnabled(MainActivity.this)) {
                    mGpsStatusDetector.checkGpsStatus();
                }
            }
        }
    };

    private boolean isGpsEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGpsStatusDetector.checkOnActivityResult(requestCode, resultCode);
    }

    @Override
    public void onGpsSettingStatus(boolean enabled) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (enabled && isPermitted) {

                check_gps = true;

                startLocationUpdates();
            }
        } else {
            if (enabled) {
                check_gps = true;
                startLocationUpdates();
            }
        }


    }

    @Override
    public void onGpsAlertCanceledByUser() {
        Snackbar.make(findViewById(android.R.id.content), R.string.we_need_permission, 300000)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mGpsStatusDetector.checkGpsStatus();
                    }
                }).setActionTextColor(Color.RED).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

//    private void checkUpdate() {
//        if (!CommonI.connectionAvailable(this)) {
//            CommonI.showSnackBar(SplashScreen.this,
//                    rel_main_login,
//                    Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                    getString(R.string.check_internet),
//                    3000);
//        } else {
//
//
//            RequestQueue requestQueue = Volley.newRequestQueue(SplashScreen.this);
//
//            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_URL + "forceupdate",
//                    null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(final JSONObject response) {
//
//                    try {
//                        if (response.getString("status").equals("0")) {
//                            CommonI.showSnackBar(SplashScreen.this,
//                                    rel_main_login,
//                                    Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"), response.getString("msg"),
//                                    2000);
//                        } else {
//
//                            JSONObject data = response.getJSONObject("data");
//                            latestVersion = data.getString("version");
//                            features = data.getString("feature");
//                            int is_force_update = data.getInt("is_force_update");
//
//                            forceUpdate = String.valueOf(is_force_update);
//
//                            update();
//
//
//                        }
//
//
//                    } catch (JSONException e) {
//                        CommonI.showSnackBar(SplashScreen.this,
//                                rel_main_login,
//                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                                e.getMessage(),
//                                2000);
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    if (error instanceof NoConnectionError) {
//                        //processing(false, "");
//                        CommonI.showSnackBar(SplashScreen.this,
//                                rel_main_login,
//                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                                error.getMessage(),
//                                2000);
//                    } else if (error instanceof TimeoutError) {
//                        // processing(false, "");
//                        CommonI.showSnackBar(SplashScreen.this,
//                                rel_main_login,
//                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                                error.getMessage(),
//                                2000);
//                    } else if (error instanceof AuthFailureError) {
//                        CommonI.showSnackBar(SplashScreen.this,
//                                rel_main_login,
//                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                                error.getMessage(),
//                                2000);
//                    } else if (error instanceof ServerError) {
//                        //processing(false, "");
//                        CommonI.showSnackBar(SplashScreen.this,
//                                rel_main_login,
//                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                                error.getMessage(),
//                                2000);
//                    } else if (error instanceof NetworkError) {
//                        //processing(false, "");
//                        CommonI.showSnackBar(SplashScreen.this,
//                                rel_main_login,
//                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
//                                error.getMessage(),
//                                2000);
//
//                    } else if (error instanceof ParseError) {
//                        //processing(false, "");
//                        Log.d("parse_error", error.getMessage());
//                    }
//
//                }
//            }) {
//                @Override
//                protected VolleyError parseNetworkError(VolleyError volleyError) {
//                    //processing(false, "");
//                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
//                    return super.parseNetworkError(volleyError);
//                }
//
//            };
//
//            requestQueue.add(objectRequest);
//        }
//    }

//    private void update() {
//        if (currentVersion.equals(latestVersion)) {
//            startActivity(new Intent(SplashScreen.this, FoodNav.class));
//            finish();
//        } else {
//            if (forceUpdate.equals("1")) {
//                setForceUpdateDialog();
//            } else if (forceUpdate.equals("0")) {
//                SharedPreferences pref = getSharedPreferences(SessionManager.PREFS_NAME_LOC, Context.MODE_PRIVATE);
//                String remind_later = pref.getString(SessionManager.REMIND_ME_LATER, "");
//
//
//                if (remind_later.equals("")) {
//                    setSoftUpdateDialog();
//                } else if (remind_later.equals(latestVersion)) {
//                    startActivity(new Intent(SplashScreen.this, FoodNav.class));
//                    finish();
//                } else {
//
//                    setSoftUpdateDialog();
//                }
//
//            }
//        }
//    }
//
//    private void setSoftUpdateDialog() {
//        softUpdateDialog = new Dialog(SplashScreen.this);
//        softUpdateDialog.setContentView(R.layout.soft_update_dialog);
//        Window window = softUpdateDialog.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//
//        TextView sure = softUpdateDialog.findViewById(R.id.tv_sure);
//        TextView cancel = softUpdateDialog.findViewById(R.id.tv_cancel);
//        TextView textView = softUpdateDialog.findViewById(R.id.tv_features);
//
//        textView.setText(features);
//
//        sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                softUpdateDialog.dismiss();
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en_US")));
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                softUpdateDialog.dismiss();
//
//                SharedPreferences pref = getSharedPreferences(SessionManager.PREFS_NAME_LOC, Context.MODE_PRIVATE);
//                SharedPreferences.Editor edt = pref.edit();
//                edt.putString(SessionManager.REMIND_ME_LATER, latestVersion);
//                edt.apply();
//
//                startActivity(new Intent(SplashScreen.this, FoodNav.class));
//                finish();
//
//            }
//        });
//
//        softUpdateDialog.setCancelable(false);
//        softUpdateDialog.show();
//    }
//
//    private void setForceUpdateDialog() {
//        forceUpdateDialog = new Dialog(SplashScreen.this);
//        forceUpdateDialog.setContentView(R.layout.force_update_dialog);
//        Window window = forceUpdateDialog.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//
//        TextView sure = forceUpdateDialog.findViewById(R.id.tv_sure);
//        TextView cancel = forceUpdateDialog.findViewById(R.id.tv_cancel);
//
//        TextView textView = forceUpdateDialog.findViewById(R.id.tv_features);
//
//        textView.setText(features);
//
//        sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                forceUpdateDialog.dismiss();
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en_US")));
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                forceUpdateDialog.dismiss();
//                finish();
//            }
//        });
//
//        forceUpdateDialog.setCancelable(false);
//        forceUpdateDialog.show();
//    }
}
