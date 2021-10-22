package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewayRegular extends androidx.appcompat.widget.AppCompatTextView
{

    public RalewayRegular(Context context) {
        super(context);
        init();
    }

    public RalewayRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewayRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
        setTypeface(tf ,1);

    }
}
