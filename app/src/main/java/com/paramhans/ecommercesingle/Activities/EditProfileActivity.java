package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView iv_back;
    private TextView tv_error;
    private EditText et_name, et_email;
    private Button btn_submit;
    private RelativeLayout llLoginLayout;
    ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        inIt();
        viewDialog = new ViewDialog(this);
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

        User user = SharedPrefManager.getInstance(EditProfileActivity.this).getUser();
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
            User user = SharedPrefManager.getInstance(EditProfileActivity.this).getUser();
            String id = user.getId();
            RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
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

                            CommonI.showSnackBar(EditProfileActivity.this,
                                    llLoginLayout,
                                    Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                    response.getString(JsonVariables.msg),
                                    2000);

                        } else {

                            User user1 = SharedPrefManager.getInstance(EditProfileActivity.this).getUser();
                            String pin = user1.getUser_type();

                            User user = new User();
                            user.setId(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_id));
                            user.setEmail(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_email));
                            user.setMobile(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_mobile));
                            user.setName(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_name));
                            user.setUser_type(pin);

                            SharedPrefManager.getInstance(EditProfileActivity.this).userLogin(user);
                            if (response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_name).equals("")) {
                                Intent intent = new Intent(EditProfileActivity.this, RegistrationDetailsFillupActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                finish();

                                //getAddress(response.getJSONObject(JsonVariables.data).getString(JsonVariables.customer_id));

                            }


                            //Toast.makeText(RegistrationDetailsFillupActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(EditProfileActivity.this,
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
                        CommonI.showSnackBar(EditProfileActivity.this,
                                llLoginLayout,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(EditProfileActivity.this,
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
