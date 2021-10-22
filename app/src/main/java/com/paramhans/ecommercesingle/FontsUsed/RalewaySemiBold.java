package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewaySemiBold extends androidx.appcompat.widget.AppCompatTextView
{
    public RalewaySemiBold(Context context) {
        super(context);
        init();
    }

    public RalewaySemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewaySemiBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
        setTypeface(tf ,1);

    }
}
