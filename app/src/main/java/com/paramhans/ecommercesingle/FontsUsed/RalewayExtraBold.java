package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewayExtraBold extends androidx.appcompat.widget.AppCompatTextView
{

    public RalewayExtraBold(Context context) {
        super(context);
        init();
    }

    public RalewayExtraBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewayExtraBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-ExtraBold.ttf");
        setTypeface(tf ,1);

    }
}
