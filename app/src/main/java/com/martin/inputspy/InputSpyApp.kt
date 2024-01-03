package com.martin.inputspy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class InputSpyApp : Application() {

    companion object {
        // The id of the notification channel. Must be unique per package. The value may be truncated if it is too long.
        const val NOTIFICATION_CHANNEL_ID = "input_spy_app_notification_channel"

        lateinit var appContext: InputSpyApp
    }

    init {
        appContext = this
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Create the NotificationChannel. Duplicate creating an existing channel has actually no effects.
     *
     * Official doc: https://developer.android.com/develop/ui/views/notifications/channels#CreateChannel
     */
    private fun createNotificationChannel() {
        val name = "channel_name"
        val descriptionText = "description_name"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance or other notification behaviors after that.
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(mChannel)
    }
}