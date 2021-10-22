package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewayLight extends androidx.appcompat.widget.AppCompatTextView
{
    public RalewayLight(Context context) {
        super(context);
        init();
    }

    public RalewayLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewayLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Light.ttf");
        setTypeface(tf ,1);

    }
}
