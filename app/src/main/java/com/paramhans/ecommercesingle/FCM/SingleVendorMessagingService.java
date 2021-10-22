package com.paramhans.ecommercesingle.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.paramhans.ecommercesingle.Activities.SplashActivity;
import com.paramhans.ecommercesingle.R;

public class SingleVendorMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("mdsdsdad", remoteMessage.getNotification().getBody().toString());
        Log.e("sdasfsdfs", remoteMessage.getData().toString());
        Log.e("SDADQWEQW", remoteMessage.getNotification().getTitle());

        String body = remoteMessage.getNotification().getBody();
        String NOTIFICATION_CHANNEL_ID = "SINGLEVENDOR_NOTIFICATION";

        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "SINGLEVENDOR_NOTIFICATION",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription(body);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        mNotificationManager.notify(1000, notificationBuilder.build());
    }
}
