package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewayMedium extends androidx.appcompat.widget.AppCompatTextView
{
    public RalewayMedium(Context context) {
        super(context);
        init();
    }

    public RalewayMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewayMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Medium.ttf");
        setTypeface(tf ,1);

    }
}
