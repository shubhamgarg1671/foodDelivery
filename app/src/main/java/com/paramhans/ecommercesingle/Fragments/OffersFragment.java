package com.paramhans.ecommercesingle.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paramhans.ecommercesingle.Adapter.OfferAdapter;
import com.paramhans.ecommercesingle.Common.CommonI;
import com.paramhans.ecommercesingle.Common.JsonVariables;
import com.paramhans.ecommercesingle.Common.URLs;
import com.paramhans.ecommercesingle.CustomViews.ViewDialog;
import com.paramhans.ecommercesingle.Interfaces.Home;
import com.paramhans.ecommercesingle.Interfaces.PromocodeClick;
import com.paramhans.ecommercesingle.Models.PromoCode;
import com.paramhans.ecommercesingle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class OffersFragment extends Fragment implements PromocodeClick {

    RecyclerView recycle_coupon;
    OfferAdapter promocodeAdapter;
    RelativeLayout rel_main_coupon;
    private ArrayList<PromoCode> promoCodes;
    ViewDialog viewDialog;

    Home home;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_offers, container, false);
        recycle_coupon = v.findViewById(R.id.recycle_coupon);
        rel_main_coupon = v.findViewById(R.id.rel_main_coupon);
        viewDialog = new ViewDialog(getActivity());
        setPromos();
        return v;

    }


    private void setPromos() {
        if (!CommonI.connectionAvailable(getActivity())) {
            CommonI.showSnackBar(getActivity(),
                    rel_main_coupon,
                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                    getString(R.string.check_internet),
                    2000);
        } else {
            viewDialog.showDialog();
            RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getActivity());
            recycle_coupon.setLayoutManager(mLayoutManager3);
            promoCodes = new ArrayList<>();

            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URLs.allpromocode,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        viewDialog.hideDialog();
                        if (response.getInt(JsonVariables.status) == 0) {

                        } else {


                            JSONArray jsonArray = response.getJSONArray(JsonVariables.data);

                            promoCodes.clear();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                // Get current json object
                                JSONObject productsJSONObject = jsonArray.getJSONObject(i);

                                String promocode_id = productsJSONObject.getString("promocode_id");
                                String promocode_image = productsJSONObject.getString("promocode_image");
                                String promocode_name = productsJSONObject.getString("promocode_name");
                                String promocode_percenatge = productsJSONObject.getString("promocode_percenatge");
                                String max_amount = productsJSONObject.getString("max_amount");
                                String description = productsJSONObject.getString("description");

                                promoCodes.add(new PromoCode(promocode_id, promocode_image, promocode_name, promocode_percenatge, max_amount, description));


                            }
                            promocodeAdapter = new OfferAdapter(getActivity(), promoCodes, OffersFragment.this);
                            recycle_coupon.setAdapter(promocodeAdapter);
                        }

                    } catch (JSONException e) {
                        viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                rel_main_coupon,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                e.getMessage(),
                                2000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   if (error instanceof ServerError) {
                       viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                rel_main_coupon,
                                Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf"),
                                error.getMessage(),
                                2000);
                    } else if (error instanceof NetworkError) {
                       viewDialog.hideDialog();
                        CommonI.showSnackBar(getActivity(),
                                rel_main_coupon,
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
                    viewDialog.hideDialog();
                    Log.d(TAG, R.string.ERROR_IN_CONNECTING_VOLLEY + volleyError.getMessage());
                    return super.parseNetworkError(volleyError);
                }

            };

            mRequestQueue.add(objectRequest);
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        //home.home(true);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    home.home(true);
                    // call an interface for menu check
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void clickPromo(String id, String name) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            home = (Home) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        home = null;
    }
}
