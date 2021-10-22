package com.paramhans.ecommercesingle.FontsUsed;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RalewayThin  extends androidx.appcompat.widget.AppCompatTextView {
    public RalewayThin(Context context) {
        super(context);
        init();
    }

    public RalewayThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RalewayThin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Thin.ttf");
        setTypeface(tf ,1);

    }
}
