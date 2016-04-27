package hr.droidcon.conference.hack.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.urbanairship.actions.ActionValue;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

import java.util.Iterator;

import hr.droidcon.conference.hack.Constants;

/**
 * Created by Ivan Carapovic on 01/04/16.
 *
 */
public class UAReceiver extends BaseIntentReceiver {

        private static final String TAG = "IntentReceiver";

        @Override
        protected void onChannelRegistrationSucceeded(Context context, String channelId) {
            Log.i(TAG, "Channel registration updated. Channel Id:" + channelId + ".");
        }

        @Override
        protected void onChannelRegistrationFailed(Context context) {
            Log.i(TAG, "Channel registration failed.");
        }

        @Override
        protected void onPushReceived(Context context, PushMessage message, int notificationId) {
            SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);

            Iterator<ActionValue> iter = message.getActions().values().iterator();

            while(iter.hasNext()){
                if(iter.next().getString().contains("refresh")){
                    prefs.edit()
                            .remove(Constants.PREFS_SESSIONS_CACHE)
                            .apply();
                    Log.d(TAG, "onPushReceived: REFRESH");
                }
            }
        }

        @Override
        protected void onBackgroundPushReceived(Context context, PushMessage message) {
            Log.i(TAG, "Received background push message: " + message);
        }

        @Override
        protected boolean onNotificationOpened(Context context, PushMessage message,
                int notificationId) {
            Log.i(TAG, "User clicked notification. Alert: " + message.getAlert());

            // Return false to let UA handle launching the launch activity
            return false;
        }

        @Override
        protected boolean onNotificationActionOpened(Context context, PushMessage message,
                int notificationId, String buttonId, boolean isForeground) {
            Log.i(TAG, "User clicked notification button. Button ID: " + buttonId + " Alert: " +
                    message.getAlert());

            // Return false to let UA handle launching the launch activity
            return false;
        }

        @Override
        protected void onNotificationDismissed(Context context, PushMessage message,
                int notificationId) {
            Log.i(TAG,
                    "Notification dismissed. Alert: " + message.getAlert() + ". Notification ID: " +
                            notificationId);
        }
    }
