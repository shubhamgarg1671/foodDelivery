package com.paramhans.ecommercesingle.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.paramhans.ecommercesingle.Activities.AboutUsActivity;
import com.paramhans.ecommercesingle.Activities.AddressActivity;
import com.paramhans.ecommercesingle.Activities.EditProfileActivity;
import com.paramhans.ecommercesingle.Activities.FaqsActivity;
import com.paramhans.ecommercesingle.Activities.HelpCenterActivity;
import com.paramhans.ecommercesingle.Activities.LoginActivity;
import com.paramhans.ecommercesingle.BuildConfig;
import com.paramhans.ecommercesingle.Interfaces.Home;
import com.paramhans.ecommercesingle.LocalDb.SharedPrefManager;
import com.paramhans.ecommercesingle.Models.User;
import com.paramhans.ecommercesingle.R;

public class ProfileFragment extends Fragment
{

    Home home;
    TextView tv_login,tv_email,tv_mobile,tv_name,tv_logout;
    RelativeLayout rel_my_address,rel_rate_us,rel_share,rel_faqs,rel_about_us,rel_help;
    ImageView iv_edit;




    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        tv_login = v.findViewById(R.id.tv_login);
        rel_my_address = v.findViewById(R.id.rel_my_address);
        tv_email = v.findViewById(R.id.tv_email);
        tv_mobile = v.findViewById(R.id.tv_mobile);
        tv_name = v.findViewById(R.id.tv_name);
        tv_logout = v.findViewById(R.id.tv_logout);
        iv_edit = v.findViewById(R.id.iv_edit);
        rel_rate_us = v.findViewById(R.id.rel_rate_us);
        rel_share = v.findViewById(R.id.rel_share);
        rel_faqs = v.findViewById(R.id.rel_faqs);
        rel_about_us = v.findViewById(R.id.rel_about_us);
        rel_help = v.findViewById(R.id.rel_help);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("id","manage");
                startActivity(intent);
            }
        });
        rel_rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
            }
        });

        rel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "OLLOF");
                    String shareMessage= "\nGet services near you.\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        rel_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });
        rel_faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FaqsActivity.class));
            }
        });
        rel_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelpCenterActivity.class));
            }
        });


        rel_my_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddressActivity.class);
                intent.putExtra("intentid","manage");
                startActivity(intent);
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
//        User user = SharedPrefManager.getInstance(getActivity()).getUser();
//        String id = user.getId();
//        if(user.getEmail()==null)
//        {
//            tv_email.setVisibility(View.GONE);
//        }
//        else {
//            tv_email.setText(user.getEmail());
//        }
//        if (user.getName()==null)
//        {
//            tv_name.setVisibility(View.GONE);
//        }
//        else
//            {
//                tv_name.setText(user.getName());
//            }
//
//        tv_mobile.setText(user.getMobile());

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getActivity()).logout();
                getActivity().finish();
            }
        });
        return v;
    }
    @Override
    public void onResume() {

        super.onResume();

        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        String id = user.getId();
        if(user.getEmail()==null)
        {
            tv_email.setVisibility(View.GONE);
        }
        else {
            tv_email.setText(user.getEmail());
        }
        if (user.getName()==null)
        {
            //tv_name.setVisibility(View.GONE);
            tv_name.setText("Verified Customer");
        }
        else
        {
            tv_name.setText(user.getName());
        }

        tv_mobile.setText("+91 "+user.getMobile());

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    home.home(true);
                    // call an interface for menu check
                    return true;
                }
                return false;
            }
        });
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
