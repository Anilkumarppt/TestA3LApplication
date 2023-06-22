package com.example.SampleA3LMessagingApp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.amazon.A3L.messaging.A3LMessagingService;
import com.amazon.A3L.messaging.Notification;
import com.amazon.A3L.messaging.RemoteMessage;

import java.util.Map;

public class MyA3LMessagingService extends A3LMessagingService {

    private static final String TAG = "MyA3LMessagingService";
    public static final String FALLBACK_NOTIFICATION_CHANNEL =
            "fallback_notification_channel";
    public static final String FALLBACK_NOTIFICATION_CHANNEL_LABEL = "Miscellaneous";


    @Override
    public void onMessageReceived(Context context, RemoteMessage remoteMessage) {
        Map<String,String> data = remoteMessage.getData();
        for (String key : data.keySet()) {
            Log.i(TAG, "key: " + key + ", value: " + data.get(key));
        }
        createNotification(context, remoteMessage.getNotification());
    }

    @Override
    public void onNewToken(Context context, String token) {
        Log.d(TAG, "In onNewDeviceId");
        Log.d(TAG, "Device token: " + token);
    }

    public static void createNotification(final Context context, Notification a3lNotification)
    {
        android.app.Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new android.app.Notification.Builder(context,
                    getOrCreateNotificationChannel(context, "channel_id"));
        } else {
            builder = new android.app.Notification.Builder(context);
        }

        if (a3lNotification.getTitle() != null){
            builder.setContentTitle(a3lNotification.getTitle());
        }
        if (a3lNotification.getBody() != null){
            builder.setContentText(a3lNotification.getBody());
        }
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);

        android.app.Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private static String getOrCreateNotificationChannel(final Context context,
                                                         final String channelId) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            if (!TextUtils.isEmpty(channelId)) {
                if (notificationManager.getNotificationChannel(channelId) != null) {
                    return channelId;
                } else {
                    Log.w(TAG, "ChannelId requested is " + channelId +
                            " has not been created by the app." + "Default ChannelId will be used");
                }
            }

            //Create the default channel if it has not been created yet.
            if (notificationManager.getNotificationChannel(FALLBACK_NOTIFICATION_CHANNEL) == null) {
                notificationManager.createNotificationChannel(
                        new NotificationChannel(FALLBACK_NOTIFICATION_CHANNEL,
                                FALLBACK_NOTIFICATION_CHANNEL_LABEL,
                                NotificationManager.IMPORTANCE_DEFAULT));
            }
        }
        return FALLBACK_NOTIFICATION_CHANNEL;
    }
}
