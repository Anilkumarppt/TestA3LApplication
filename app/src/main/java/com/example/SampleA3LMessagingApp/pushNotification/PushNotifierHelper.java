package com.example.SampleA3LMessagingApp.pushNotification;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class PushNotifierHelper {

    private static final String title = "Today's Gyan";
    private static final String body = "Software & cathedrals are same.Build 1st, then pray.";

    public static JSONObject getDefaultADMNotification() throws JSONException {
        final JSONObject notificationJSON = new JSONObject();
        final JSONObject admNotification = new JSONObject();
        if (title != null) {
            admNotification.put("a3l.notification.title", title);
        }
        if (body != null) {
            admNotification.put("a3l.notification.body", body);
        }

        notificationJSON.put("data", admNotification);
        return notificationJSON;
    }

    public static JSONObject getDefaultFCMNotification() throws JSONException {
        final JSONObject notificationJSON = new JSONObject();
        if (title != null) {
            notificationJSON.put("title", title);
        }
        if (body != null) {
            notificationJSON.put("body", body);
        }
        return notificationJSON;
    }

    public static JSONObject getGroupPayload(String operation, final String groupName, String token) throws JSONException {
        final JSONObject groupPayload = new JSONObject();
        groupPayload.put("operation", operation);
        groupPayload.put("notification_key_name", groupName);
        groupPayload.put("registration_ids", Collections.singletonList(token));
        return groupPayload;
    }

    public static JSONObject loadJSONFromAsset(Context context, String fileName) throws JSONException {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return new JSONObject(json);
    }

}
