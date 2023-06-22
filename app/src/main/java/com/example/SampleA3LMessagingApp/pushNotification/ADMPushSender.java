package com.example.SampleA3LMessagingApp.pushNotification;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ADMPushSender {

    public static final String TAG = "ADMPushSender";
    public static final String Auth_token_URL = "https://api.amazon.com/auth/O2/token";

    public static final String send_message_url_part = "https://api.amazon.com/messaging/registrations/";
    public static final String send_message_to_topic_url = "https://api.amazon.com/v1/messaging/topic/messages";
    public static final String send_message_to_group_url = "https://api.amazon.com/v1/messaging/group/messages";

    public static void sendADMPushNotification(Context context, final String deviceToken,
                                               final String configurationFilePath) throws JSONException, IOException {
        JSONObject admServerCredential = getADMServerCredential(context, configurationFilePath);
        final String clientID = admServerCredential.getString("client_id");
        final String clientSecret = admServerCredential.getString("client_secret");

        final String accessToken = getAccessToken(clientID, clientSecret);
        Log.d(TAG, "AccessToken: " + accessToken);
        final String output = sendMessage(accessToken, deviceToken,
                PushNotifierHelper.getDefaultADMNotification());
        Log.d(TAG, "Send output: " + output);

    }

    public static void sendADMPushNotificationToTopic(
            final Context context,
            final String topic,
            final String configurationFilePath
    ) throws JSONException, IOException {
        final JSONObject admServerCredential = getADMServerCredential(context, configurationFilePath);
        final String clientID = admServerCredential.getString("client_id");
        final String clientSecret = admServerCredential.getString("client_secret");
        final String accessToken = getAccessToken(clientID, clientSecret);
        Log.d(TAG, "AccessToken: " + accessToken);
        final String output = sendMessageToTopic(accessToken, topic, PushNotifierHelper.getDefaultADMNotification());
        Log.d(TAG, "Send output: " + output);
    }

    public static void sendADMPushNotificationToGroup(
            final Context context,
            final String group,
            final String configurationFilePath
    ) throws JSONException, IOException {
        final JSONObject admServerCredential = getADMServerCredential(context, configurationFilePath);
        final String clientID = admServerCredential.getString("client_id");
        final String clientSecret = admServerCredential.getString("client_secret");
        final String accessToken = getAccessToken(clientID, clientSecret);
        Log.d(TAG, "AccessToken: " + accessToken);
        final String output = sendMessageToGroup(accessToken, group, PushNotifierHelper.getDefaultADMNotification());
        Log.d(TAG, "Send output: " + output);
    }

    private static JSONObject getADMServerCredential(Context context, final String configurationFilePath){
        try {
            JSONObject admServerCredential = PushNotifierHelper.loadJSONFromAsset(context, configurationFilePath);
            final String clientID = admServerCredential.getString("client_id");
            final String clientSecret = admServerCredential.getString("client_secret");
            return admServerCredential;
        }
        catch (JSONException jsonException){
            throw new InvalidServerConfigurationException("ADM Server Configuration is not valid JSON.");
        }
    }

    private static String getAccessToken(final String clientID, final String clientSecret)
            throws IOException, JSONException {
        final String  auth_token_data = "grant_type=client_credentials&scope=messaging:push" +
                "&client_id=" + clientID + "&client_secret=" + clientSecret;
        byte[] auth_token_data_bytes = auth_token_data.getBytes( StandardCharsets.UTF_8 );

        URL url = new URL(Auth_token_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput( true );

        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept-Charset", "application/x-www-form-urlencoded");
        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( auth_token_data_bytes );
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
            String response = content.toString();
            JSONObject responseJsonObj = new JSONObject(response);
            return responseJsonObj.getString("access_token");
        }
        Log.d(TAG,"Status: " + status + " Response: " + con.getResponseMessage());
        con.disconnect();
        return null;
    }

    private static String sendMessage(final String accessToken, final String deviceToken,
                                      final JSONObject notification) throws IOException {
        final String send_message = notification.toString();
        byte[] send_message_bytes = send_message.getBytes( StandardCharsets.UTF_8 );

        final String sendMessageUrl = send_message_url_part + deviceToken + "/messages";

        URL url = new URL(sendMessageUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput( true );

        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("x-amzn-type-version", "com.amazon.device.messaging.ADMMessage@1.0");
        con.setRequestProperty("x-amzn-accept-type", "com.amazon.device.messaging.ADMSendResult@1.0");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

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

    private static String sendMessageToTopic(final String accessToken, final String topic,
                                             final JSONObject notification) throws IOException, JSONException {
        notification.put("topic", topic);
        Log.d(TAG, String.format("Topic is %s", topic));
        final String send_message = notification.toString();
        Log.d(TAG, send_message);
        byte[] send_message_bytes = send_message.getBytes( StandardCharsets.UTF_8 );

        URL url = new URL(send_message_to_topic_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput( true );

        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("x-amzn-type-version", "com.amazon.device.messaging.ADMMessage@1.0");
        con.setRequestProperty("x-amzn-accept-type", "com.amazon.device.messaging.ADMSendResult@1.0");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( send_message_bytes );
        }
        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
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

    private static String sendMessageToGroup(
            final String accessToken,
            final String group,
            final JSONObject notification
    ) throws IOException, JSONException {
        notification.put("group", group);
        Log.d(TAG, String.format("Group is %s", group));
        final String send_message = notification.toString();
        Log.d(TAG, send_message);
        byte[] send_message_bytes = send_message.getBytes( StandardCharsets.UTF_8 );

        URL url = new URL(send_message_to_group_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput( true );

        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("x-amzn-type-version", "com.amazon.device.messaging.ADMMessage@1.0");
        con.setRequestProperty("x-amzn-accept-type", "com.amazon.device.messaging.ADMSendResult@1.0");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( send_message_bytes );
        }
        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
}
