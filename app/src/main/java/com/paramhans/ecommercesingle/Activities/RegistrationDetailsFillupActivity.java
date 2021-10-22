package com.paramhans.ecommercesingle.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
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

import static android.content.ContentValues.TAG;

public class RegistrationDetailsFillupActivity extends AppCompatActivity {

    private ImageView iv_back, iv_man_pc;
    private TextView tv_error;
    private String mobile_number;
    private EditText et_name, et_email;
    private Button btn_submit;
    private RelativeLayout llLoginLayout;
    private TextView tv_skip, header, msg;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details_fillup);
        inIt();
        viewDialog = new ViewDialog(this);

        Intent intent = getIntent();

        if (intent.hasExtra("register")) {
            tv_skip.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
        } else {
            tv_skip.setVisibility(View.GONE);
            iv_man_pc.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            btn_submit.setText("UPDATE NOW");

        }


        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getDefaultAddress();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateName()) {
                    return;
                } else {
                    register();
                }


            }
        });

        User user = SharedPrefManager.getInstance(RegistrationDetailsFillupActivity.this).getUser();
        et_name.setText(user.getName());
        et_email.setText(user.getEmail());


    }

    private void inIt() {
        iv_back = findViewById(R.id.iv_back);
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        tv_error = findViewById(R.id.tv_error);
        btn_submit = findViewById(R.id.btn_submit);
        llLoginLayout = findViewById(R.id.lin_container);
        tv_skip = findViewById(R.id.tv_skip);
        iv_man_pc = findViewById(R.id.iv_man_pc);
        header = findViewById(R.id.header);
        msg = findViewById(R.id.msg);
    }

    private boolean validateName() {
        String name = et_name.getText().toString();
        if (name.isEmpty()) {
            tv_error.setText("Required");
            return false;
        } else {
            tv_error.setText("");
            return true;
        }

    }

    private void register() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            viewDialog.showDialog();
            User user = SharedPrefManager.getInstance(RegistrationDetailsFillupActivity.this).getUser();
            String id = user.getId();
            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationDetailsFillupActivity.this);
            String customer_name = et_name.getText().toString();
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, id);
            params.put(JsonVariables.customer_name, customer_name);
            params.put(JsonVariables.customer_email, et_email.getText().toString());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.customer_register,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {

                            CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
                                    llLoginLayout,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);


                        } else {


                            User user = new User();
                            user.setId(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_id));
                            user.setEmail(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_email));
                            user.setMobile(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_mobile));
                            user.setName(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_name));

                            SharedPrefManager.getInstance(RegistrationDetailsFillupActivity.this).userLogin(user);

                            if (response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_name).equals("")) {
                                Intent intent = new Intent(RegistrationDetailsFillupActivity.this, RegistrationDetailsFillupActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //finish();

                                getDefaultAddress();
                                //getAddress(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_id));

                            }


                            //Toast.makeText(RegistrationDetailsFillupActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
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
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
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


    private void getDefaultAddress() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            viewDialog.showDialog();
            User user = SharedPrefManager.getInstance(RegistrationDetailsFillupActivity.this).getUser();
            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationDetailsFillupActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, user.getId());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.get_default_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                            viewDialog.hideDialog();
                            Intent intent1 = new Intent(RegistrationDetailsFillupActivity.this, LocationSearchActivity.class);
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
                            User user1 = SharedPrefManager.getInstance(RegistrationDetailsFillupActivity.this).getUser();

                            User user = new User();

                            user.setId(user1.getId());
                            user.setEmail(user1.getEmail());
                            user.setMobile(user1.getMobile());
                            user.setName(user1.getName());

                            user.setUser_type(response.getJSONObject("data").getString("pin"));
                            SharedPrefManager.getInstance(RegistrationDetailsFillupActivity.this).userLogin(user);
                            startActivity(new Intent(RegistrationDetailsFillupActivity.this, HomeActivity.class));
                            finish();


                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        Intent intent1 = new Intent(RegistrationDetailsFillupActivity.this, LocationSearchActivity.class);
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
                        Toast.makeText(RegistrationDetailsFillupActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        Toast.makeText(RegistrationDetailsFillupActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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
            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationDetailsFillupActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, id);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.get_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                            startActivity(new Intent(RegistrationDetailsFillupActivity.this, AddressActivity.class));
                            finish();

                        } else {

                            JSONArray jsonArray = response.getJSONArray(JsonVariables.data);
                            if (jsonArray.length() == 0) {
                                startActivity(new Intent(RegistrationDetailsFillupActivity.this, AddressActivity.class));
                                finish();
                            } else {
//                                startActivity(new Intent(RegistrationDetailsFillupActivity.this, DeliveryActivity.class));
//                                finish();
                            }


                        }

                    } catch (JSONException e) {
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
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
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);

                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(RegistrationDetailsFillupActivity.this,
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
