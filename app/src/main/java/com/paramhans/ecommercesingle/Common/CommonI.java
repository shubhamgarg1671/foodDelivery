package com.paramhans.ecommercesingle.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.paramhans.ecommercesingle.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CommonI
{
    public static void setStatusBarTranslucent(AppCompatActivity activity, boolean makeTranslucent) {
        if (makeTranslucent) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    public static void changeStatusBarColor(AppCompatActivity activity, int color) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(color);
        }
    }

    public static byte[] getBytes(Context context, InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        try {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return byteBuffer.toByteArray();
    }


    public static File saveBitmapToFile(File file) {
        return saveBitmapToFile(file, 75);
    }

    public static File saveBitmapToFile(File file, int requiredSize) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = requiredSize;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean connectionAvailable(Context mContext) {
        ConnectivityManager
                cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static void disableTouch(Context context, boolean isTouchDisabled) {
        if (isTouchDisabled) {
            ((AppCompatActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            ((AppCompatActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

//    public static void redirectToErrorPage(final Context context, String errorText, String errorMessage, int errorType) {
//        Intent errorIntent = new Intent(context, ErrorPage.class);
//        switch (errorType) {
//            case 0: //When --> there is no internet connection
//                errorIntent.putExtra(Extras.ERROR_TEXT, errorText);
//                errorIntent.putExtra(Extras.ERROR_TYPE, errorType);
//                errorIntent.putExtra(Extras.ERROR_MESSAGE_TEXT, errorMessage);
//
//                context.startActivity(errorIntent);
//                break;
//            case 1: //When --> there is Exception
//                errorIntent.putExtra(Extras.ERROR_TEXT, errorText);
//                errorIntent.putExtra(Extras.ERROR_TYPE, errorType);
//                errorIntent.putExtra(Extras.ERROR_MESSAGE_TEXT, errorMessage);
//                context.startActivity(errorIntent);
//                break;
//            case 2: //When --> Failure in getting Response
//                errorIntent.putExtra(Extras.ERROR_TEXT, errorText);
//                errorIntent.putExtra(Extras.ERROR_TYPE, errorType);
//                errorIntent.putExtra(Extras.ERROR_MESSAGE_TEXT, errorMessage);
//                context.startActivity(errorIntent);
//                break;
//            case 3: //When --> response.getString(JsonChilds.ISERROR).equals("1")
//                errorIntent.putExtra(Extras.ERROR_TEXT, errorText);
//                errorIntent.putExtra(Extras.ERROR_TYPE, errorType);
//                errorIntent.putExtra(Extras.ERROR_MESSAGE_TEXT, errorMessage);
//                context.startActivity(errorIntent);
//                break;
//            case 4: //When --> there is Database Error
//                errorIntent.putExtra(Extras.ERROR_TEXT, errorText);
//                errorIntent.putExtra(Extras.ERROR_TYPE, errorType);
//                errorIntent.putExtra(Extras.ERROR_MESSAGE_TEXT, errorMessage);
//                context.startActivity(errorIntent);
//                break;
//            default:
//                break;
//
//        }
//    }

    public static void showSnackBar(Context context, View view, Typeface typeface, String message, int duration){
        Snackbar snackbarRetryButton = Snackbar.make(view,message,duration);
        View retryView = snackbarRetryButton.getView();
        retryView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        TextView snackbarErrorTextView =  retryView.findViewById(R.id.snackbar_text);
        snackbarErrorTextView.setTypeface(typeface);
        snackbarErrorTextView.setTextColor(context.getResources().getColor(R.color.black));;
        snackbarRetryButton.show();
    }

}
