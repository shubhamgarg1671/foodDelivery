package com.paramhans.ecommercesingle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

public class SplashActivity extends AppCompatActivity {
    MaterialCardView mat_iv_logo;
    Animation animFadeIn, animdown;
    TextView tv_tag;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    boolean isPermitted = false;
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

        checkRunTimePermission();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11111) {
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];
                isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isPermitted) {

                        SharedPreferences pref = getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
                        SharedPreferences.Editor edt = pref.edit();

                        String latitude = pref.getString(SharedPrefManager.LAT, "");
                        String longitude = pref.getString(SharedPrefManager.LONG, "");




                            User user = SharedPrefManager.getInstance(SplashActivity.this).getUser();
                            String id = user.getId();
                            if (id == null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }, 500);
                            } else {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    }, 500);


                            }


                        //Toast.makeText(this, "Ok permitted", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    if (SharedPrefManager.getInstance(SplashActivity.this).isLoggedIn()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                finish();
                            }
                        }, 1500);
                    }
                    else
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }, 1500);
                    }
                    //Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
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

    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        } else {

        }
    }
    AlertDialog.Builder dialog;

    private void alertView() {
        dialog = new AlertDialog.Builder(SplashActivity.this);

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

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_permission_title)
                        .setMessage(R.string.dialog_permission_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SplashActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
