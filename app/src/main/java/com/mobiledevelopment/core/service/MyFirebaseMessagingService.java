package com.mobiledevelopment.core.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobiledevelopment.MyApplication;
import com.mobiledevelopment.R;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//
//        if(notification == null){
//            return;
//        }
//
//        String title = notification.getTitle();
//        String message = notification.getBody();

        // Date Messages
        Map<String, String> stringMap = remoteMessage.getData();

        if(stringMap == null) return;

        String title = stringMap.get("title");
        String description = stringMap.get("body");

        sendNotification(title,description);
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.app_icon);

        Notification notification = notificationBuilder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager != null){
            manager.notify(1,notification);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
