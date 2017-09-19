package hr.droidcon.conference.utils

import android.content.Context
import android.provider.Settings

fun Context.getDeviceId(): String =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
