package com.paramhans.ecommercesingle.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    EditText et_mobile_number;
    Button btn_login;
    TextView tv_error;
    RelativeLayout rel_login;
    ImageView iv_back;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_mobile_number = findViewById(R.id.et_mobile_number);
        btn_login = findViewById(R.id.btn_login);
        tv_error = findViewById(R.id.tv_error);
        rel_login = findViewById(R.id.rel_login);
        iv_back = findViewById(R.id.iv_back);
        viewDialog = new ViewDialog(this);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateMobileNumber()) {
                    return;
                } else {
                    sendOTP();
                }
            }
        });

        et_mobile_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 9) {
                    btn_login.setBackground(getResources().getDrawable(R.drawable.btn_bg));
                    tv_error.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendOTP() {
        if (!CommonI.connectionAvailable(LoginActivity.this)) {
            CommonI.showSnackBar(LoginActivity.this,
                    rel_login,
                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {

            viewDialog.showDialog();
            Map<String, String> params = new HashMap<>();
            params.put(JsonVariables.mobile_no, et_mobile_number.getText().toString());

            RequestQueue mRequestQueue = Volley.newRequestQueue(LoginActivity.this);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.send_otp,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("shubham", "onResponse() called with: response = [" + response + "]");
                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {
                            Toast.makeText(LoginActivity.this, "" + response.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            if (intent.hasExtra("id")) {
                                Intent intent1 = new Intent(LoginActivity.this, EnterOTPActivity.class);
                                intent1.putExtra("mobile", et_mobile_number.getText().toString());
                                intent1.putExtra("id", "manage");
                                startActivity(intent1);
                                finish();
                            } else {
                                Intent intent1 = new Intent(LoginActivity.this, EnterOTPActivity.class);
                                intent1.putExtra("mobile", et_mobile_number.getText().toString());
                                startActivity(intent1);
                                finish();
                            }


                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(LoginActivity.this,
                                rel_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Shubham", "onErrorResponse() called with: error = [" + error + "]");
                    if (error instanceof ServerError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(LoginActivity.this,
                                rel_login,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(LoginActivity.this,
                                rel_login,
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

    private boolean validateMobileNumber() {
        String mobileInput = et_mobile_number.getText().toString();
        if (mobileInput.isEmpty()) {
            tv_error.setText("Required");
            return false;
        } else if (mobileInput.length() < 10) {
            tv_error.setText("Put valid mobile number");
            return false;
        } else {
            tv_error.setText("");
            return true;
        }

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
