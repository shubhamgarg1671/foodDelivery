package com.paramhans.ecommercesingle.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.paramhans.ecommercesingle.Adapter.OrderAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.Interfaces.BookServiceClicked;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.Order;
import com.paramhans.ecommercesingle.Models.OrderOrder_items;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HistoryBookingFragment extends Fragment
{
    RecyclerView rv_ongoing_bookings;
    LinearLayout lin_no_booking;
    RelativeLayout home;
    private ArrayList<Order> orders;
    OrderAdapter orderAdapter;
    Button btn_book_service;
    BookServiceClicked bookServiceClicked;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        lin_no_booking = v.findViewById(R.id.lin_no_booking);
        rv_ongoing_bookings = v.findViewById(R.id.rv_ongoing_bookings);
        home = v.findViewById(R.id.rel_booking_history);
        btn_book_service = v.findViewById(R.id.btn_book_service);
        setBookings();


        btn_book_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bookServiceClicked.home(1);
            }
        });

        return v;
    }
    private void setBookings() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    home,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getActivity());
            rv_ongoing_bookings.setLayoutManager(mLayoutManager3);
            orders = new ArrayList<>();
            User user = SharedPrefManager.getInstance(getActivity()).getUser();
            String id = user.getId();
            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

            Map<String, String> params = new HashMap<String, String>();
            params.put("customer_id", id);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URLs.customer_history_orderlist,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getInt(JsonVariables.status) == 0) {
//                            CommonI.showSnackBar(getActivity(),
//                                    home,
//                                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
//                                    response.getString(JsonVariables.msg),
//                                    2000);
                            lin_no_booking.setVisibility(View.VISIBLE);
                        } else {
                            JSONArray categoriesArray = response.getJSONArray(JsonVariables.data);

                            lin_no_booking.setVisibility(View.GONE);
                            for (int i = 0; i < categoriesArray.length(); i++) {
                                JSONObject categoriesArrayJSONObject = categoriesArray.getJSONObject(i);

                                String id = categoriesArrayJSONObject.getString("id");
                                String order_code = categoriesArrayJSONObject.getString("order_code");
                                String address_id = categoriesArrayJSONObject.getString("address_id");
                                String order_amount = categoriesArrayJSONObject.getString("order_amount");
                                String order_date = categoriesArrayJSONObject.getString("order_date");
                                String order_status = categoriesArrayJSONObject.getString("order_status");


                                JSONArray jsonArray = categoriesArrayJSONObject.getJSONArray("order_items");
                                ArrayList<OrderOrder_items> orderOrder_items = new ArrayList<>();

                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject catJSONObject = jsonArray.getJSONObject(j);
                                    String idq = catJSONObject.getString("id");
                                    String qty = catJSONObject.getString("qty");
                                    String price = catJSONObject.getString("price");
                                    String total_price = catJSONObject.getString("total_price");

                                    String product_id = catJSONObject.getString("product_id");
                                    String product_name = catJSONObject.getString("product_name");
                                    String product_main_img = catJSONObject.getString("product_main_img");
                                    String category_id = catJSONObject.getString("category_id");
                                    String category_name = catJSONObject.getString("category_name");
                                    String subcategory_id = catJSONObject.getString("subcategory_id");
                                    String subcategory_name = catJSONObject.getString("subcategory_name");
                                    String service_provider_id = catJSONObject.getString("service_provider_id");
                                    String service_provider_name = catJSONObject.getString("service_provider_name");

                                    orderOrder_items.add(new OrderOrder_items(idq, qty, price, total_price, product_id, product_name, product_main_img,
                                            category_id, category_name, subcategory_id, subcategory_name, service_provider_id, service_provider_name));

                                }


                                orders.add(new Order(id, order_code, address_id, order_amount, order_date, order_status, orderOrder_items));
                            }
                            orderAdapter = new OrderAdapter(getActivity(), orders);
                            rv_ongoing_bookings.setAdapter(orderAdapter);
                        }


                    } catch (JSONException e) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof TimeoutError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof AuthFailureError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof ServerError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                        CommonI.showSnackBar(getActivity(),
                                home,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
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

            mRequestQueue.add(objectRequest);

        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bookServiceClicked = (BookServiceClicked) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bookServiceClicked = null;
    }
}
