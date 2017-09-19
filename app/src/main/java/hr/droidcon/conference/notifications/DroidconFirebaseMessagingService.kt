package hr.droidcon.conference.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.troido.ekstend.android.debug.logD
import de.troido.ekstend.android.intents.activityIntent
import de.troido.ekstend.android.notifications.notification
import de.troido.ekstend.android.notifications.notify
import de.troido.ekstend.android.persistence.defaultPreferences
import de.troido.ekstend.android.persistence.edit
import hr.droidcon.conference.Constants
import hr.droidcon.conference.MainActivity

private const val CHANNEL_ID = "droidcon_zg"
private const val REFRESH_ACTION = "refresh"
private const val ID = 384
private const val REQUEST_CODE = 384

class DroidconFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        logD("""Msg from ${message.from}:
            data=${message.data},
            title=${message.notification?.title},
            body=${message.notification?.body}
        """.trimIndent())

        if (REFRESH_ACTION in message.data) {
            defaultPreferences.edit { remove(Constants.PREFS_SESSIONS_CACHE) }
        }

        message.notification?.let {
            notify(ID, notification(
                    channelId = CHANNEL_ID,
                    title = it.title,
                    text = it.body,
                    intent = activityIntent<MainActivity>(
                            REQUEST_CODE,
                            action = it.clickAction,
                            uri = it.link
                    )
            ))
        }
    }
}
