package com.paramhans.ecommercesingle.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class EnterOTPActivity extends AppCompatActivity {

    private ImageView iv_back;
    private TextView numEntered, tv_resend_otp,tv_timer;
    private String mobile_number;
    private EditText otp_view_pin;
    private Button btn_submit_otp;
    String newToken;
    private LinearLayout llLoginLayout;
    ViewDialog viewDialog;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_o_t_p);
        FirebaseApp.initializeApp(EnterOTPActivity.this);
        inIt();
        viewDialog = new ViewDialog(this);
        tv_timer = findViewById(R.id.tv_timer);

        Intent intent = getIntent();
        mobile_number = intent.getStringExtra("mobile");

        numEntered.setText("(+91) - " + mobile_number);

        otp_view_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp_view_pin.getText().toString().length() == 4) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(otp_view_pin.getWindowToken(), 0);
                    verify_otp();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnterOTPActivity.this, LoginActivity.class));
                finish();
            }
        });

        btn_submit_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateOTP()) {
                    return;
                } else {
                    verify_otp();
                }


            }
        });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("32wfegh1", "getInstanceId failed", task.getException());
                            return;
                        }
                        newToken = task.getResult().getToken();


                    }
                });

        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP();
            }
        });

        new CountDownTimer(30000, 1000) { //Set Timer for 5 seconds
            int time=30;
            public void onTick(long millisUntilFinished)
            {
                tv_resend_otp.setVisibility(View.GONE);
                tv_timer.setText("0:"+checkDigit(time));
                time--;
            }

            @Override
            public void onFinish() {
                tv_timer.setVisibility(View.GONE);
                tv_resend_otp.setVisibility(View.VISIBLE);
            }
        }.start();

    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    private void sendOTP() {
        if (!CommonI.connectionAvailable(EnterOTPActivity.this)) {
            CommonI.showSnackBar(EnterOTPActivity.this,
                    llLoginLayout,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            viewDialog.showDialog();
            Map<String, String> params = new HashMap<>();
            params.put(JsonVariables.mobile_no, mobile_number);

            RequestQueue mRequestQueue = Volley.newRequestQueue(EnterOTPActivity.this);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.send_otp,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {
                            Toast.makeText(EnterOTPActivity.this, "" + response.getString("msg"), Toast.LENGTH_SHORT).show();
                        } else {
                            new CountDownTimer(30000, 1000) { //Set Timer for 5 seconds
                                int time=30;
                                public void onTick(long millisUntilFinished)
                                {
                                    tv_resend_otp.setVisibility(View.GONE);
                                    tv_timer.setVisibility(View.VISIBLE);
                                    tv_timer.setText("0:"+checkDigit(time));
                                    time--;
                                }
                                @Override
                                public void onFinish() {
                                    tv_timer.setVisibility(View.GONE);
                                    tv_resend_otp.setVisibility(View.VISIBLE);
                                }
                            }.start();
                            Toast.makeText(EnterOTPActivity.this, "" + response.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
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
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
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

    private void verify_otp() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            viewDialog.showDialog();
            RequestQueue requestQueue = Volley.newRequestQueue(EnterOTPActivity.this);
            String otp = otp_view_pin.getText().toString();
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.mobile_no, mobile_number);
            params.put(JsonVariables.otp, otp);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.otp_verification,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {

                            CommonI.showSnackBar(EnterOTPActivity.this,
                                    llLoginLayout,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);
                            otp_view_pin.getText().clear();


                        } else {

                            User user = new User();

                            user.setId(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_id));
                            user.setEmail(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_email));
                            user.setMobile(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_mobile));
                            user.setName(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_name));

                            SharedPrefManager.getInstance(EnterOTPActivity.this).userLogin(user);

                            updateFcmToken(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_id), response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_name));


                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
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
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
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

            requestQueue.add(objectRequest);
        }

    }

    private void updateFcmToken(String id, final String name) {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(EnterOTPActivity.this);
            String otp = otp_view_pin.getText().toString();
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, id);
            params.put("fcm_token", newToken);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.customer_fcmtoken,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                            CommonI.showSnackBar(EnterOTPActivity.this,
                                    llLoginLayout,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);

                        } else {

                                Intent intent = new Intent(EnterOTPActivity.this, HomeActivity.class);
                                intent.putExtra("register", "1");
                                startActivity(intent);
                                finish();


                                //getDefaultAddress();


                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Poppins-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
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

            requestQueue.add(objectRequest);
        }
    }

    private void getDefaultAddress() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            viewDialog.showDialog();
            User user = SharedPrefManager.getInstance(EnterOTPActivity.this).getUser();
            RequestQueue requestQueue = Volley.newRequestQueue(EnterOTPActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, user.getId());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.get_default_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                            viewDialog.hideDialog();
                            Intent intent1 = new Intent(EnterOTPActivity.this, LocationSearchActivity.class);
                            intent1.putExtra("add", "1");
                            startActivity(intent1);
                            finish();
                        } else {
                            viewDialog.hideDialog();

                            SharedPreferences pref = getSharedPreferences(SharedPrefManager.LOCATION_SP, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edt = pref.edit();
                            edt.putString(SharedPrefManager.LAT, response.getJSONObject("data").getString("latitude"));
                            edt.putString(SharedPrefManager.LONG, response.getJSONObject("data").getString("longitude"));
                            edt.apply();
                            User user1 = SharedPrefManager.getInstance(EnterOTPActivity.this).getUser();

                            User user = new User();

                            user.setId(user1.getId());
                            user.setEmail(user1.getEmail());
                            user.setMobile(user1.getMobile());
                            user.setName(user1.getName());

                            user.setUser_type(response.getJSONObject("data").getString("pin"));
                            SharedPrefManager.getInstance(EnterOTPActivity.this).userLogin(user);
                            startActivity(new Intent(EnterOTPActivity.this, HomeActivity.class));
                            finish();


                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        Intent intent1 = new Intent(EnterOTPActivity.this, LocationSearchActivity.class);
                        intent1.putExtra("add", "1");
                        startActivity(intent1);
                        finish();
                        // Toast.makeText(LocationSearchActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        viewDialog.hideDialog();
                        Toast.makeText(EnterOTPActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        Toast.makeText(EnterOTPActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void getAddress(String id) {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(EnterOTPActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.get_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {


                        } else {

                            JSONArray jsonArray = response.getJSONArray(JsonVariables.data);
                            if (jsonArray.length() == 0) {
//                                startActivity(new Intent(EnterOTPActivity.this, AddressActivity.class));
//                                finish();
                            } else {
//                                startActivity(new Intent(EnterOTPActivity.this, DeliveryActivity.class));
//                                finish();
                            }


                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);

                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(EnterOTPActivity.this,
                                llLoginLayout,
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

            requestQueue.add(objectRequest);
        }
    }

    private boolean validateOTP() {
        String otp = otp_view_pin.getText().toString();
        if (otp.isEmpty()) {
            CommonI.showSnackBar(EnterOTPActivity.this,
                    llLoginLayout,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    "Please Provide OTP sent to your your phone number.",
                    2000);


            return false;
        } else if (otp.length() < 4) {

            CommonI.showSnackBar(EnterOTPActivity.this,
                    llLoginLayout,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    "Please Provide OTP sent to your your phone number.",
                    2000);
            return false;
        } else {
            return true;
        }


    }

    private void inIt() {
        iv_back = findViewById(R.id.iv_back);
        numEntered = findViewById(R.id.numEntered);
        otp_view_pin = findViewById(R.id.otp_view_pin);
        btn_submit_otp = findViewById(R.id.btn_submit_otp);
        tv_resend_otp = findViewById(R.id.tv_resend_otp);
        llLoginLayout = findViewById(R.id.lin_main_enter_otp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EnterOTPActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
                v.clearFocus();
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
