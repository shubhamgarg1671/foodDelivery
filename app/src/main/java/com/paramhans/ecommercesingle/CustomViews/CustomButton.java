package com.paramhans.ecommercesingle.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomButton extends androidx.appcompat.widget.AppCompatButton {
    public CustomButton(Context context) {
        super(context);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
        setTypeface(tf ,1);

    }
}
