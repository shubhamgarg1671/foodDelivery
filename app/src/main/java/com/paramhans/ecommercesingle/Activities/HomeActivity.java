package com.paramhans.ecommercesingle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.paramhans.ecommercesingle.Fragments.BookingsFragment;
import com.paramhans.ecommercesingle.Fragments.HomeFragment;
import com.paramhans.ecommercesingle.Fragments.OffersFragment;
import com.paramhans.ecommercesingle.Fragments.ProfileFragment;
import com.paramhans.ecommercesingle.Interfaces.BookServiceClicked;
import com.paramhans.ecommercesingle.Interfaces.Home;
import com.paramhans.ecommercesingle.R;

public class HomeActivity extends AppCompatActivity implements BookServiceClicked, Home {
    BottomNavigationView bottomNav;
    Fragment selectedFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent.hasExtra("booking"))
            {
                bottomNav.setSelectedItemId(R.id.nav_bookings);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BookingsFragment()).commit();

            }
            else if (intent.hasExtra("profile"))
            {
                bottomNav.setSelectedItemId(R.id.nav_bookings);
                if(intent.getStringExtra("profile").equalsIgnoreCase("1"))
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                    bottomNav.setSelectedItemId(R.id.nav_profile);
                }
            }
            else
            {
                //bottomNav.setSelectedItemId(R.id.nav_home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }
        }



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_bookings:
                            selectedFragment = new BookingsFragment();
                            break;
                        case R.id.nav_offers:
                            selectedFragment = new OffersFragment ();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment ();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

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

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
            {
                hideKeyboard(this);
                v.clearFocus();
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void home(int a) {
        if (a == 1) {
            bottomNav.setSelectedItemId(R.id.nav_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();

        }
    }

    @Override
    public void home(boolean b) {
        if (b) {
            selectedFragment = HomeFragment.newInstance();
            bottomNav.setSelectedItemId(R.id.nav_home);

        }
    }
}
