package com.paramhans.ecommercesingle.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paramhans.ecommercesingle.Adapter.CartAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.LocalDb.SharedPreferenceCart;
import com.paramhans.ecommercesingle.Models.CartItem;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CheckOutActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    TextView tv_subtotal, tv_coupon_code, tv_coupon_code_amount, tv_total;
    String order_type = "0";
    ImageView iv_back;
    ArrayList<CartItem> favouritesBeanSampleList;
    RelativeLayout rel_delivery;
    String razorpay_orderid;
    TextView tv_apply_coupon, coupon_applied;
    double sumPr = 0.00;
    String promocode_id = " ", promocode_amount = "0", order_amount_after_promocode, promocode_name;
    SharedPreferenceCart sharedPreferenceCart;
    AlertDialog dialog;
    String address_id;
    RelativeLayout rv_proceed, rel_address;
    RecyclerView rv_cart;
    TextView tv_service_address, tv_header, tv_proceed, tv_change;
    String payment_method = "0";
    AlertDialog dialogOrder;
    ViewDialog viewDialog;
    RadioGroup radio_group;
    RadioButton employee, employer;
    TextView tv_delivery_charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        tv_apply_coupon = findViewById(R.id.tv_apply_coupon);
        rv_proceed = findViewById(R.id.rv_proceed);
        rel_delivery = findViewById(R.id.rel_delivery);
        rv_cart = findViewById(R.id.rv_cart);
        tv_total = findViewById(R.id.tv_total);
        tv_delivery_charge = findViewById(R.id.tv_delivery_charge);
        tv_subtotal = findViewById(R.id.tv_subtotal);
        tv_coupon_code = findViewById(R.id.tv_coupon_code);
        tv_coupon_code_amount = findViewById(R.id.tv_coupon_code_amount);
        iv_back = findViewById(R.id.iv_back);
        tv_apply_coupon = findViewById(R.id.tv_apply_coupon);
        coupon_applied = findViewById(R.id.coupon_applied);
        tv_service_address = findViewById(R.id.tv_service_address);
        tv_header = findViewById(R.id.tv_header);
        tv_proceed = findViewById(R.id.tv_proceed);
        tv_change = findViewById(R.id.tv_change);
        rel_address = findViewById(R.id.rel_address);
        viewDialog = new ViewDialog(this);
        radio_group = findViewById(R.id.radio_group);
        employee = findViewById(R.id.employee);
        employer = findViewById(R.id.employer);
        employee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    order_type = "1";
                }
            }
        });

        employer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    order_type = "2";
                }
            }
        });
        sharedPreferenceCart = new SharedPreferenceCart();
        favouritesBeanSampleList = sharedPreferenceCart.loadFavorites(CheckOutActivity.this);

        if (favouritesBeanSampleList != null) {

            for (int i = 0; i < favouritesBeanSampleList.size(); i++) {
                String price = favouritesBeanSampleList.get(i).getDiscount_price();
                double dp = Double.parseDouble(price);
                sumPr = sumPr + dp;
            }
            Log.d("ssuummmmprr", String.valueOf(sumPr));
            tv_subtotal.setText("Rs." + sumPr);

            tv_coupon_code_amount.setText("Not Applied");
            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(CheckOutActivity.this);
            rv_cart.setLayoutManager(mLayoutManager1);
            rv_cart.setAdapter(new CartAdapter(CheckOutActivity.this, favouritesBeanSampleList));

        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDefaultAddress();

        getDeliveryCharge();


        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(CheckOutActivity.this, AddressActivity.class);
                view.putExtra("intentid", "cart");
                startActivity(view);
            }
        });


        final Intent intent = getIntent();
        if (intent.hasExtra("promocode_id")) {
            promocode_id = intent.getStringExtra("promocode_id");
            promocode_amount = intent.getStringExtra("promocode_amount");
            order_amount_after_promocode = intent.getStringExtra("order_amount_after");
            promocode_name = intent.getStringExtra("promocode_name");
            tv_subtotal.setText("Rs." + sumPr);
            coupon_applied.setText(promocode_name + ":" + "Rs." + promocode_amount + "off on your order amount.");
            coupon_applied.setTextColor(getResources().getColor(R.color.green_light));
            tv_coupon_code.setText("Coupon(" + promocode_name + ")");
            tv_coupon_code_amount.setText(" - Rs." + promocode_amount);
            tv_total.setText("Rs." + order_amount_after_promocode);
            dialogCouponApplied();
        }

        tv_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckOutActivity.this, CoupounActivity.class));
                finish();
            }
        });
        rv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (address_id.equals("0")) {
                    Intent view = new Intent(CheckOutActivity.this, AddressActivity.class);
                    view.putExtra("intentid", "cart");
                    startActivity(view);
                } else if (order_type.equals("0")) {

                    Toast.makeText(CheckOutActivity.this, "Please select your payment method.", Toast.LENGTH_SHORT).show();

                } else {
                    if (order_type.equals("1")) {
                        checkoutCahOnDelivery();
                    } else {
                        checkoutOnlineDelivery();
                    }


                }


            }
        });

    }

    private void getDefaultAddress() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            viewDialog.showDialog();
            User user = SharedPrefManager.getInstance(CheckOutActivity.this).getUser();
            RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put(JsonVariables.customer_id, user.getId());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.get_default_address,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {

                            viewDialog.hideDialog();
                            rel_address.setVisibility(View.GONE);
                            address_id = "0";
                        } else {
                            viewDialog.hideDialog();
                            rel_address.setVisibility(View.VISIBLE);
                            address_id = response.getJSONObject("data").getString("id");
                            tv_service_address.setText(response.getJSONObject("data").getString("address_line"));
                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        Toast.makeText(CheckOutActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        viewDialog.hideDialog();
                        Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        viewDialog.hideDialog();
                        Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void getDeliveryCharge() {
        if (!CommonI.connectionAvailable(this)) {

        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.get_deliverycharge,
                   null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {


                        } else {
                            tv_delivery_charge.setText("Rs. "+response.getJSONObject("data").getString("delivery_amount"));

                            tv_total.setText("Rs." + (sumPr+Double.parseDouble(response.getJSONObject("data").getString("delivery_amount"))));

                        }

                    } catch (JSONException e) {

                        Toast.makeText(CheckOutActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {

                        Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {

                        Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void dialogOrderplacedSuccessfully(final String order_code) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_order_placed_successfully, null);
        dialog = new AlertDialog.Builder(this).create();
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(CheckOutActivity.this, OrderDetailsActivity.class);
                intent.putExtra("order_code", order_code);
                intent.putExtra("back", "1");
                startActivity(intent);
                finish();

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;
        dialog.show();

    }

    private void dialogCouponApplied() {
        View view = LayoutInflater.from(this).inflate(R.layout.coupon_applied_dialog, null);
        dialog = new AlertDialog.Builder(this).create();
        TextView tvOk = view.findViewById(R.id.ok);
        TextView tv_coupn_desc = view.findViewById(R.id.tv_coupn_desc);
        TextView header = view.findViewById(R.id.header);

        header.setText("Coupon Applied");
        tv_coupn_desc.setText("Your coupon is successfully applied.");
        tvOk.setText("YAY!");

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;
        dialog.show();
    }

    private void checkoutCahOnDelivery() {
        viewDialog.showDialog();
        User user = SharedPrefManager.getInstance(CheckOutActivity.this).getUser();
        String id = user.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        HashMap input = new HashMap();

        //customer_id,customer_note,cart_hash,delivery date need to send and check
        input.put("customer_id", id);
        input.put("address_id", address_id);
        input.put("promocode_id", promocode_id);
        input.put("promocode_amount", promocode_amount);


        List<HashMap> line_items = new ArrayList<>();

        for (int i = 0; i < favouritesBeanSampleList.size(); i++) {
            CartItem orderLineItemModel = favouritesBeanSampleList.get(i);
            HashMap hashMap = new HashMap();

            hashMap.put("product_id", orderLineItemModel.getPid());
            hashMap.put("qty", orderLineItemModel.getQty());
            line_items.add(hashMap);
        }

        input.put("order_items", line_items);

        Log.d("JSONARRAY", String.valueOf(new JSONObject(input)));
        Log.d("JSONOBJECT", String.valueOf(new JSONObject(input)));


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.place_order,
                new JSONObject(input), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {
                    if (response.getString("status").equals("0")) {

                        viewDialog.hideDialog();
                        CommonI.showSnackBar(CheckOutActivity.this,
                                rel_delivery,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"), response.getString("msg"),
                                2000);
                    } else {
                        viewDialog.hideDialog();
                        String success = response.getString("status");
                        String order_code = response.getJSONObject("data").getString("order_id");
                        SharedPreferenceCart sharedPreferenceCart1;
                        sharedPreferenceCart1 = new SharedPreferenceCart();
                        sharedPreferenceCart1.clearDate(CheckOutActivity.this);
                        dialogOrderplacedSuccessfully(order_code);

                    }


                } catch (JSONException e) {
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            e.getMessage(),
                            2000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    //processing(false, "");
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            error.getMessage(),
                            2000);
                } else if (error instanceof NetworkError) {
                    //processing(false, "");
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            error.getMessage(),
                            2000);

                } else if (error instanceof ParseError) {
                    //processing(false, "");

                    Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("parse_error", error.getMessage());
                }

            }
        }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                //processing(false, "");
                viewDialog.hideDialog();
                Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }

        };

        requestQueue.add(objectRequest);
    }

    private void checkoutOnlineDelivery() {
        viewDialog.showDialog();
        User user = SharedPrefManager.getInstance(CheckOutActivity.this).getUser();
        String id = user.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        HashMap input = new HashMap();

        //customer_id,customer_note,cart_hash,delivery date need to send and check
        input.put("customer_id", id);
        input.put("address_id", address_id);
        input.put("promocode_id", promocode_id);
        input.put("promocode_amount", promocode_amount);


        List<HashMap> line_items = new ArrayList<>();

        for (int i = 0; i < favouritesBeanSampleList.size(); i++) {
            CartItem orderLineItemModel = favouritesBeanSampleList.get(i);
            HashMap hashMap = new HashMap();

            hashMap.put("product_id", orderLineItemModel.getPid());
            hashMap.put("qty", orderLineItemModel.getQty());
            line_items.add(hashMap);
        }

        input.put("order_items", line_items);

        Log.d("JSONARRAY", String.valueOf(new JSONObject(input)));
        Log.d("JSONOBJECT", String.valueOf(new JSONObject(input)));


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.create_razorpay_order,
                new JSONObject(input), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {
                    if (response.getString("status").equals("0")) {

                        viewDialog.hideDialog();
                        CommonI.showSnackBar(CheckOutActivity.this,
                                rel_delivery,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"), response.getString("msg"),
                                2000);
                    } else {
                        viewDialog.hideDialog();
                        razorpay_orderid = response.getJSONObject(JsonVariables.data).getString("razorpay_orderid");
                        String order_amount = response.getJSONObject(JsonVariables.data).getString("razorpay_amount");
                        startPayment(razorpay_orderid, order_amount);

                    }


                } catch (JSONException e) {
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            e.getMessage(),
                            2000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    //processing(false, "");
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            error.getMessage(),
                            2000);
                } else if (error instanceof NetworkError) {
                    //processing(false, "");
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            error.getMessage(),
                            2000);

                } else if (error instanceof ParseError) {
                    //processing(false, "");

                    Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("parse_error", error.getMessage());
                }

            }
        }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                //processing(false, "");
                viewDialog.hideDialog();
                Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }

        };

        requestQueue.add(objectRequest);
    }

    private void saveOrder(String payment_id) {
        viewDialog.showDialog();
        User user = SharedPrefManager.getInstance(CheckOutActivity.this).getUser();
        String id = user.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        HashMap input = new HashMap();

        //customer_id,customer_note,cart_hash,delivery date need to send and check
        input.put("customer_id", id);
        input.put("address_id", address_id);
        input.put("promocode_id", promocode_id);
        input.put("promocode_amount", promocode_amount);
        input.put("razorpay_orderid", razorpay_orderid);
        input.put("razorpay_paymentid", payment_id);


        List<HashMap> line_items = new ArrayList<>();

        for (int i = 0; i < favouritesBeanSampleList.size(); i++) {
            CartItem orderLineItemModel = favouritesBeanSampleList.get(i);
            HashMap hashMap = new HashMap();

            hashMap.put("product_id", orderLineItemModel.getPid());
            hashMap.put("qty", orderLineItemModel.getQty());
            line_items.add(hashMap);
        }

        input.put("order_items", line_items);

        Log.d("JSONARRAY", String.valueOf(new JSONObject(input)));
        Log.d("JSONOBJECT", String.valueOf(new JSONObject(input)));


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.online_orderpalced,
                new JSONObject(input), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {
                    if (response.getString("status").equals("0")) {

                        viewDialog.hideDialog();
                        CommonI.showSnackBar(CheckOutActivity.this,
                                rel_delivery,
                                Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"), response.getString("msg"),
                                2000);
                    } else {
                        viewDialog.hideDialog();
                        SharedPreferenceCart sharedPreferenceCart1;
                        sharedPreferenceCart1 = new SharedPreferenceCart();
                        sharedPreferenceCart1.clearDate(CheckOutActivity.this);
                        String order_code = response.getJSONObject("data").getString("order_id");
                        dialogOrderplacedSuccessfully(order_code);

                    }


                } catch (JSONException e) {
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            e.getMessage(),
                            2000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    //processing(false, "");
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            error.getMessage(),
                            2000);
                } else if (error instanceof NetworkError) {
                    //processing(false, "");
                    viewDialog.hideDialog();
                    CommonI.showSnackBar(CheckOutActivity.this,
                            rel_delivery,
                            Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"),
                            error.getMessage(),
                            2000);

                } else if (error instanceof ParseError) {
                    //processing(false, "");

                    Toast.makeText(CheckOutActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("parse_error", error.getMessage());
                }

            }
        }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                //processing(false, "");
                viewDialog.hideDialog();
                Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }

        };

        requestQueue.add(objectRequest);
    }

    public void startPayment(String order_id, String amount) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.mipmap.ic_launcher);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Single Vendor");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "");
            options.put("order_id", order_id);
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            User user = SharedPrefManager.getInstance(CheckOutActivity.this).getUser();
            String mobile = user.getMobile();
            String email = user.getEmail();
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", mobile);

            options.put("prefill", preFill);
            Log.d("oopppttionns", options.toString());
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("RazorError", "Error in starting Razorpay Checkout", e);
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CheckOutActivity.this);
        builder1.setMessage("Payment failed.Please retry.");
        builder1.setPositiveButton(
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData data) {
        String payment_id = data.getPaymentId();
        String signature = data.getSignature();
        String orderId = data.getOrderId();
        Log.d("RazorPAymentSuccess", payment_id);
        saveOrder(payment_id);

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d("paayyyymmmentttterrooor", s + "......" + paymentData.toString());
        showExitDialog();
    }
}
