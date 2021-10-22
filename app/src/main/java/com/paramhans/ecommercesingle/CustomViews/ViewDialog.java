package com.paramhans.ecommercesingle.CustomViews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.paramhans.ecommercesingle.R;


public class ViewDialog
{
    Context context;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor


    public ViewDialog(Context context) {
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    public void showDialog() {

        dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.progress_dialog);

        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }
}
