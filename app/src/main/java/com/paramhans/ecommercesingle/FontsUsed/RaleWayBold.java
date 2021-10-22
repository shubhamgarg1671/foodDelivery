package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RaleWayBold extends androidx.appcompat.widget.AppCompatTextView
{
    public RaleWayBold(Context context) {
        super(context);
        init();
    }

    public RaleWayBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RaleWayBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Bold.ttf");
        setTypeface(tf ,1);

    }
}
