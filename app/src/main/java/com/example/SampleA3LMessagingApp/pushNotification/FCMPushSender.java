package com.example.SampleA3LMessagingApp.pushNotification;

import android.content.Context;
import android.util.Log;

import com.example.SampleA3LMessagingApp.R;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public class FCMPushSender {

    public static final String TAG = "FCMPushSender";

    public static final String send_message_url_part = "https://fcm.googleapis.com";

    public static void sendFCMPushNotification(Context context, final String deviceToken,
                                               final String configurationFilePath) throws JSONException, IOException {
        String fcmProjectID = getFCMProjectID(context, configurationFilePath);
        String accessToken = getAccessToken(context, configurationFilePath);
        String fcmProjectKey = getFCMProjectKey(context);
        final JSONObject notification = PushNotifierHelper.getDefaultFCMNotification();
        final JSONObject to = new JSONObject();
        to.put("token", deviceToken);
        final String output = sendMessage(fcmProjectID, fcmProjectKey, accessToken, to, notification, false);
        Log.d(TAG, "Send output: " + output);
    }

    public static void sendFCMPushNotificationToTopic(
            final Context context,
            final String topic,
            final String configurationFilePath
    ) throws JSONException, IOException {
        String fcmProjectID = getFCMProjectID(context, configurationFilePath);
        String accessToken = getAccessToken(context, configurationFilePath);
        String fcmProjectKey = getFCMProjectKey(context);
        final JSONObject notification = PushNotifierHelper.getDefaultFCMNotification();
        final JSONObject to = new JSONObject();
        to.put("topic", topic);
        final String output = sendMessage(fcmProjectID, fcmProjectKey, accessToken, to, notification, false);
        Log.d(TAG, "Send output: " + output);
    }

    public static void sendFCMPushNotificationToGroup(
            final Context context,
            final String group,
            final String configurationFilePath
    ) throws JSONException, IOException {
        String fcmProjectID = getFCMProjectID(context, configurationFilePath);
        String accessToken = getAccessToken(context, configurationFilePath);
        String fcmProjectKey = getFCMProjectKey(context);
        final JSONObject notification = PushNotifierHelper.getDefaultFCMNotification();
        final JSONObject to = new JSONObject();
        to.put("to", "APA91bFibxqOaNjiJAwIoQgfFflGCYYWEY71LY9pKyHj0O35jnawjGWtQEaFECGj-ImoLYRV-8b4Lt1IgDe51biVJUJ44v90YQVh8ZHHGHnAZdCJXS2Yc4w");
        final String output = sendMessage(fcmProjectID, fcmProjectKey, accessToken, to, notification, true);
        Log.d(TAG, "Send output: " + output);
    }

    public static void createFCMGroup(
            final Context context,
            final String group,
            final String configurationFilePath,
            final String deviceToken
    ) throws JSONException, IOException {
        String fcmProjectID = getFCMSenderID(context, configurationFilePath);
        String apiKey = getAPIKey(context, configurationFilePath);
        String accessToken = getAccessToken(context, configurationFilePath);
        String fcmProjectKey = getFCMProjectKey(context);
        final JSONObject sendMessage = PushNotifierHelper.getGroupPayload("create", group, deviceToken);

        Log.d(TAG, "Create group: " + sendMessage.toString());
        byte[] send_message_bytes = sendMessage.toString().getBytes( StandardCharsets.UTF_8 );

        final String send_message_url = send_message_url_part + "/fcm/notification";

        URL url = new URL(send_message_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput( true );

        con.setRequestProperty("Content-Type", "application/json; UTF-8");
        con.setRequestProperty("Authorization", "key=" + "AAAAFvHfuV8:APA91bHRpG4PPN4sf55ad32RrN8Ph91CB1a4qhdE3479Y-j0nIfdADfZLpPwR1seoZWegwyKUsozVl7yyiLvzDCXepGtKW_g_u3qgSOVl52JN29UX1FE0rnS1vlnb1qnoK1CHwEcgvfs");
        con.setRequestProperty("project_id", "98547251551");

        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( send_message_bytes );
        }
        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            Log.d(TAG, "Create output" + content.toString());
        }
        Log.d(TAG,"Status: " + status + " Response: " + con.getResponseMessage());
        con.disconnect();
    }

    private static String getFCMProjectID(Context context, final String configurationFilePath){
        try {
            JSONObject fcmServerCredential = PushNotifierHelper.loadJSONFromAsset(context, configurationFilePath);
            return fcmServerCredential.getString("project_id");
        }
        catch (JSONException jsonException){
            throw new InvalidServerConfigurationException("ADM Server Configuration is not valid JSON.");
        }
    }

    private static String getFCMSenderID(Context context, final String configurationFilePath){
        try {
            JSONObject fcmServerCredential = PushNotifierHelper.loadJSONFromAsset(context, configurationFilePath);
            return fcmServerCredential.getString("client_id");
        }
        catch (JSONException jsonException){
            throw new InvalidServerConfigurationException("ADM Server Configuration is not valid JSON.");
        }
    }

    private static String getAPIKey(Context context, final String configurationFilePath){
        try {
            JSONObject fcmServerCredential = PushNotifierHelper.loadJSONFromAsset(context, configurationFilePath);
            return fcmServerCredential.getString("private_key_id");
        }
        catch (JSONException jsonException){
            throw new InvalidServerConfigurationException("ADM Server Configuration is not valid JSON.");
        }
    }

    private static String getFCMProjectKey(Context context){
        return context.getResources().getString(R.string.google_api_key);
    }

    private static String sendMessage(
            final String fcmProjectID,
            final String fcmProjectKey,
            final String accessToken,
            final JSONObject to,
            final JSONObject notification,
            final boolean group
    ) throws IOException, JSONException {

        JSONObject notificationMessage = new JSONObject();
//        if (topic == null) {
//            notificationMessage.put("token", deviceToken);
//        } else {
//            notificationMessage.put("topic", topic);
//        }
        notificationMessage.put("notification", notification);
        for (Iterator<String> it = to.keys(); it.hasNext(); ) {
            String key = it.next();
            notificationMessage.put(key, to.get(key));
        }

        byte[] send_message_bytes;
        if (group) {
            Log.d(TAG, "Send Message: " + notificationMessage.toString());
            send_message_bytes = notificationMessage.toString().getBytes(StandardCharsets.UTF_8);
        } else {
            JSONObject sendMessage = new JSONObject();
            sendMessage.put("message", notificationMessage);

            Log.d(TAG, "Send Message: " + sendMessage.toString());
            send_message_bytes = sendMessage.toString().getBytes(StandardCharsets.UTF_8);
        }
        String send_message_url = send_message_url_part + "/v1/projects/" + fcmProjectID +
                "/messages:send" + "?key=" + fcmProjectKey;

        if (group) {
            send_message_url = send_message_url_part + "/fcm/send";
        }

        URL url = new URL(send_message_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput( true );

        con.setRequestProperty("Content-Type", "application/json; UTF-8");
        if (!group) {
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
        } else {
            con.setRequestProperty("Authorization", "key=" + "AAAAFvHfuV8:APA91bHRpG4PPN4sf55ad32RrN8Ph91CB1a4qhdE3479Y-j0nIfdADfZLpPwR1seoZWegwyKUsozVl7yyiLvzDCXepGtKW_g_u3qgSOVl52JN29UX1FE0rnS1vlnb1qnoK1CHwEcgvfs");
        }
        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( send_message_bytes );
        }
        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        }
        Log.d(TAG,"Status: " + status + " Response: " + con.getResponseMessage());
        con.disconnect();
        return null;
    }

    private static String getAccessToken(Context context, final String configurationFilePath) throws IOException {
        final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
        final String[] SCOPES = { MESSAGING_SCOPE };
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(context.getAssets().open(configurationFilePath))
                .createScoped(Arrays.asList(SCOPES));
        AccessToken accessToken = googleCredentials.refreshAccessToken();
        return accessToken.getTokenValue();
    }
}
