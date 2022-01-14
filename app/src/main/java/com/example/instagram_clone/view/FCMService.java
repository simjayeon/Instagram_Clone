package com.example.instagram_clone.view;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.instagram_clone.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FCMService extends FirebaseMessagingService {
    final String CHANNEL_ID = "CHANNEL_ID";
    final String CHANNEL_NAME = "CHANNEL_NAME";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channelMessage = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelMessage = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(true);
            channelMessage.setVibrationPattern(new long[]{1000, 1000});
            notificationManager.createNotificationChannel(channelMessage);
        }

        //푸시알림을 Builder를 이용하여 만듭니다.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_favorite)
                        .setContentTitle(title)//푸시알림의 제목
                        .setContentText(message)//푸시알림의 내용
                        .setChannelId(CHANNEL_ID)
                        .setAutoCancel(true)//선택시 자동으로 삭제되도록 설정.
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
