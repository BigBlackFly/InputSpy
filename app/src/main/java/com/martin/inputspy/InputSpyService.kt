package com.martin.inputspy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.input.InputManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.InputEvent
import android.view.InputEventReceiver
import android.view.InputMonitor
import android.view.MotionEvent


class InputSpyService : Service() {

    companion object {
        private const val TAG = "TAG"
        private const val INPUT_CHANNEL_NAME = "asqa_input_spy_channel_name"
        private const val NOTIFICATION_CHANNEL_ID = "notification_channel_634h7hfd9"
        private const val NOTIFICATION_ID = 100
    }

    private lateinit var mInputManager: InputManager
    private lateinit var mInputMonitor: InputMonitor
    private lateinit var mInputEventReceiver: InputEventReceiver

    override fun onCreate() {
        Log.d(TAG, "$this onCreate")
        super.onCreate()
        makeSelfForeground()
        mInputManager = getSystemService(InputManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "$this onStartCommand")
        startMonitor()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "$this onDestroy")
        super.onDestroy()
        stopMonitor()
    }

    private fun handleMotionEvent(event: MotionEvent) {
        // suppress the log for a cleaner logcat
//        Log.d(TAG, "onMotionEvent $event")
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)
        Log.d(TAG, "pointerIndex = $pointerIndex, pointerId = $pointerId, [x,y] = [$x,$y]")
    }

    private fun startMonitor() {
        mInputMonitor =
            mInputManager.monitorGestureInput(INPUT_CHANNEL_NAME, Display.DEFAULT_DISPLAY)
        Log.d(TAG, "created $mInputMonitor")

        mInputEventReceiver =
            object : InputEventReceiver(mInputMonitor.inputChannel, Looper.getMainLooper()) {
                override fun onInputEvent(event: InputEvent) {
                    if (event is MotionEvent) {
                        handleMotionEvent(event)
                    }
                    super.onInputEvent(event)
                }
            }
    }

    private fun stopMonitor() {
        mInputMonitor.dispose()
        Log.d(TAG, "disposed $mInputMonitor")

        mInputEventReceiver.dispose()
        Log.d(TAG, "disposed $mInputEventReceiver")
    }


    private fun makeSelfForeground() {
        ensureChannelIsCreated()
        val notification: Notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Input Spy")
            .setContentText("Input Spy is monitoring your input")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setTicker("This is the ticker text")
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun makeSelfBackground() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun ensureChannelIsCreated() {
        // duplicate creating an existing channel has actually no effects.
        createNotificationChannel()
    }

    /**
     * Create the NotificationChannel.
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