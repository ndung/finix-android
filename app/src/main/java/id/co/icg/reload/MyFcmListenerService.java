package id.co.icg.reload;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Random;

import id.co.icg.reload.R;
import id.co.icg.reload.activity.ChatActivity;
import id.co.icg.reload.activity.SplashActivity;
import id.co.icg.reload.model.ChatMessage;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.NotificationUtils;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;

/**
 * Created by ndung on 12/21/2015.
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map bundle = message.getData();

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        String title = (String) bundle.get("title");
        Boolean isBackground = Boolean.valueOf((String)bundle.get("is_background"));
        String flag = (String)bundle.get("flag");
        String data = (String)bundle.get("data");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "isBackground: " + isBackground);
        Log.d(TAG, "flag: " + flag);
        Log.d(TAG, "data: " + data);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         *
         *     if (flag!=null)
         *         switch (Integer.parseInt(flag)) {
         *             case Config.PUSH_TYPE_CHATROOM:
         *             // push notification belongs to a chat room
         *             case Config.PUSH_TYPE_USER:
         *             // push notification is specific to user
         *     else
         *         sendNotification
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        processChatRoomPush(title, isBackground, data);
    }
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        String[] notif = message.split(";");
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.smalllogo)
                .setTicker(notif[0])
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(notif[1])
                .setSubText(notif[2])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notificationBuilder.build());
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Processing chat room push message
     * this message will be broadcasts to all the activities registered
     * */
    private void processChatRoomPush(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                Reseller rs = Preferences.getUser(getApplicationContext());

                JSONObject datObj = new JSONObject(data);

                String chatRoomId = datObj.getString("chat_room_id");
                String userId = datObj.getString("user_id");

                JSONObject mObj = datObj.getJSONObject("message");
                ChatMessage message = new ChatMessage();
                message.setMessage(mObj.getString("message"));
                message.setId(Long.parseLong(mObj.getString("message_id")));
                message.setUserId(userId);
                message.setCreated(sdf.parse(mObj.getString("created_at")));

                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (rs!=null && rs.getId().equals(userId)) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }

                // verifying whether the app is in background or foreground
                //if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                Intent pushNotification = new Intent(Static.PUSH_NOTIFICATION);
                pushNotification.putExtra("type", Static.PUSH_TYPE_CHATROOM);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("chat_room_id", chatRoomId);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();
                //} else {
                if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    resultIntent.putExtra("chat_room_id", chatRoomId);
                    if (rs==null){
                        resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                    }
                    showNotificationMessage(getApplicationContext(), title, message.getUserId() + " : " + message.getMessage(),
                            sdf.format(message.getCreated()), resultIntent);
                }

            } catch (Exception e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
        Preferences.setUnreadMessage(getApplicationContext(), true);
        Intent intent = new Intent(INTENT_FILTER);
        sendBroadcast(intent);
    }


    public static final String INTENT_FILTER = "INTENT_FILTER";

    private NotificationUtils notificationUtils;

    /**
     * Showing notification with text only
     * */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
}