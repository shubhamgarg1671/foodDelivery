package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RaleWayBlack extends androidx.appcompat.widget.AppCompatTextView
{
    public RaleWayBlack(Context context) {
        super(context);
        init();
    }

    public RaleWayBlack(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RaleWayBlack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Black.ttf");
        setTypeface(tf ,1);

    }
}
