package hr.droidcon.conference.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.troido.ekstend.android.debug.logD

private const val CHANNEL_ID = "droidcon_zg"

class DroidconFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        logD("Msg from ${message.from}:\n${message.data}")
    }
}
