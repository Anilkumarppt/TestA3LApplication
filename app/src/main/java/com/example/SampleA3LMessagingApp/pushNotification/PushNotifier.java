package com.example.SampleA3LMessagingApp.pushNotification;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.TextView;

import com.amazon.A3L.messaging.A3LMessaging;
import com.amazon.A3L.messaging.registration.InitCallbackResponse;
import com.amazon.A3L.messaging.registration.OnInitCallback;
import com.amazon.A3L.messaging.util.A3LMessagingConstants;
import com.example.SampleA3LMessagingApp.R;


import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class PushNotifier {
    public static String ADM_Configuration_file = null;
    public static String FCM_Configuration_file = null;
    public static String token = null;

    public static void sendPushNotification(View view, Context context){
        final OnInitCallback onInitCallback = new OnInitCallback() {
            @Override
            public void onReady(InitCallbackResponse initCallbackResponse) {
                final String deviceToken = initCallbackResponse.getToken();
                token = deviceToken;
                TextView textView = (TextView) view.findViewById(R.id.debug_textview);
                String message = String.format("Messaging platform: %s\n",
                        A3LMessaging.getCurrentPlatform(context)) +
                        String.format("Device ID: %s\n", deviceToken);
                textView.setText(message);
                switch (A3LMessaging.getCurrentPlatform(context)){
                    case A3LMessagingConstants.ADM_PLATFORM:
                        try {
                            ADMPushSender.sendADMPushNotification(context, deviceToken, ADM_Configuration_file);
                        } catch (JSONException | IOException jsonException) {
                            jsonException.printStackTrace();
                        }
                        break;
                    case A3LMessagingConstants.FCM_PLATFORM:
                        try {
                            FCMPushSender.sendFCMPushNotification(context, deviceToken, FCM_Configuration_file);
                        } catch (JSONException | IOException jsonException) {
                            jsonException.printStackTrace();
                        }
                        break;
                    default:
                        throw new RuntimeException("Invalid Platform.");

                }
            }
        };
        A3LMessaging.getToken(context, onInitCallback);
    }

    public static void sendMessageToTopic(View view, Context context, String topic) {
        switch (A3LMessaging.getCurrentPlatform(context)){
            case A3LMessagingConstants.ADM_PLATFORM:
                try {
                    ADMPushSender.sendADMPushNotificationToTopic(context, topic, ADM_Configuration_file);
                } catch (JSONException | IOException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            case A3LMessagingConstants.FCM_PLATFORM:
                try {
                    FCMPushSender.sendFCMPushNotificationToTopic(context, topic, FCM_Configuration_file);
                } catch (JSONException | IOException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            default:
                throw new RuntimeException("Invalid Platform.");
        }
    }

    public static void sendMessageToGroup(Context context, String group) {
        //            case A3LMessagingConstants.ADM_PLATFORM:
        //                try {
        //                    ADMPushSender.sendADMPushNotificationToTopic(context, topic, ADM_Configuration_file);
        //                } catch (JSONException | IOException jsonException) {
        //                    jsonException.printStackTrace();
        //                }
        //                break;
        if (A3LMessagingConstants.FCM_PLATFORM.equals(A3LMessaging.getCurrentPlatform(context))) {
            try {
                FCMPushSender.sendFCMPushNotificationToGroup(context, group, FCM_Configuration_file);
            } catch (JSONException | IOException jsonException) {
                jsonException.printStackTrace();
            }
        } else {
            throw new RuntimeException("Invalid Platform.");
        }
    }

    public static void createGroup(Context context, String group) {
        //            case A3LMessagingConstants.ADM_PLATFORM:
        //                try {
        //                    ADMPushSender.sendADMPushNotificationToTopic(context, topic, ADM_Configuration_file);
        //                } catch (JSONException | IOException jsonException) {
        //                    jsonException.printStackTrace();
        //                }
        //                break;
        if (A3LMessagingConstants.FCM_PLATFORM.equals(A3LMessaging.getCurrentPlatform(context))) {
            try {
                FCMPushSender.createFCMGroup(context, group, FCM_Configuration_file, token);
            } catch (JSONException | IOException jsonException) {
                jsonException.printStackTrace();
            }
        } else {
            throw new RuntimeException("Invalid Platform.");
        }
    }

    public static void validateServerConfigurations(Context context)
            throws InvalidServerConfigurationException{
        try{
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo!=null){
                ADM_Configuration_file = applicationInfo.metaData.getString("ADM_SERVER_CONFIG_FILE_NAME");
                FCM_Configuration_file = applicationInfo.metaData.getString("FCM_SERVER_CONFIG_FILE_NAME");
            }
            if (ADM_Configuration_file == null || ADM_Configuration_file.length() == 0
                    || FCM_Configuration_file == null || FCM_Configuration_file.length() == 0){
                throw new InvalidServerConfigurationException("ADM_Configuration_file or " +
                        "FCM_Configuration_file not provided in manifest.");
            }
            StringBuilder errorMessageBuilder = new StringBuilder();
            AssetManager assetManager = context.getAssets();
            if (!isAssetExists(context, ADM_Configuration_file)){
                errorMessageBuilder.append(String.format("ADM Configuration file %s does not exist",
                        ADM_Configuration_file));
                throw new InvalidServerConfigurationException(errorMessageBuilder.toString());
            }
            if (!isAssetExists(context, FCM_Configuration_file)){
                errorMessageBuilder.append(String.format("FCM Configuration file %s does not exist",
                        FCM_Configuration_file));
                throw new InvalidServerConfigurationException(errorMessageBuilder.toString());
            }
        }
        catch (PackageManager.NameNotFoundException e){
            throw new InvalidServerConfigurationException("PackageManager.NameNotFoundException", e);
        }
    }

    private static boolean isAssetExists(Context context, String pathInAssetsDir){
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(pathInAssetsDir);
            if(null != inputStream ) {
                return true;
            }
        }  catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
