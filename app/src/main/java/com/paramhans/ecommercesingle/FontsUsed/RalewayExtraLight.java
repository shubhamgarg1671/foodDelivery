package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewayExtraLight extends androidx.appcompat.widget.AppCompatTextView {
    public RalewayExtraLight(Context context) {
        super(context);
        init();
    }

    public RalewayExtraLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewayExtraLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-ExtraLight.ttf");
        setTypeface(tf ,1);

    }
}
