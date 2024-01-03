package com.martin.inputspy

import android.app.Notification
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
    }

    private val mInputManager: InputManager by lazy { this.getSystemService(InputManager::class.java) }
    private lateinit var mInputMonitor: InputMonitor
    private lateinit var mInputEventReceiver: InputEventReceiver

    private var isMonitoring: Boolean = false

    override fun onCreate() {
        Log.d(TAG, "$this onCreate")
        super.onCreate()
        makeSelfForeground()
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
        stopMonitor()
        super.onDestroy()
    }

    private fun makeSelfForeground() {
        val notificationId = 100
        val notification: Notification =
            Notification.Builder(this, InputSpyApp.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Input Spy")
                .setContentText("Input Spy is monitoring your input event")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker("This is the ticker")
                .build()
        startForeground(notificationId, notification)
    }

    private fun makeSelfBackground() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun startMonitor() {
        if (isMonitoring) {
            Log.i(TAG, "ignore duplicated startMonitor")
            return
        }
        try {
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
            Log.d(TAG, "created $mInputEventReceiver")
            isMonitoring = true
        } catch (t: Throwable) {
            Log.e(TAG, "can't create InputMonitor! is this app signed with system signature?")

            // this will trigger onDestroy() -> stopMonitor()
            stopSelf()
        }
    }

    private fun stopMonitor() {
        if (this::mInputMonitor.isInitialized) {
            mInputMonitor.dispose()
            Log.d(TAG, "disposed $mInputMonitor")
        }
        if (this::mInputEventReceiver.isInitialized) {
            mInputEventReceiver.dispose()
            Log.d(TAG, "disposed $mInputEventReceiver")
        }
        isMonitoring = false
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
}