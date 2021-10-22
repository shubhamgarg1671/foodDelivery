package com.paramhans.ecommercesingle.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.mukesh.OtpView;

public class JpOtpView extends OtpView
{
    public JpOtpView(Context context) {
        super(context);
        init();
    }

    public JpOtpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JpOtpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
        setTypeface(tf ,1);

    }
}
