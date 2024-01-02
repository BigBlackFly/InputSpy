package com.martin.inputspy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        requestPermissions()
        logAppStatistics()
    }

    private fun initView() {
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startMonitor()
        }
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopMonitor()
        }
    }

    private fun startMonitor() {
        val serviceIntent = Intent(this, InputSpyService::class.java)
        startService(serviceIntent)
    }

    private fun stopMonitor() {
        val serviceIntent = Intent(this, InputSpyService::class.java)
        stopService(serviceIntent)
    }

    private fun requestPermissions() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (!hasPermission(permission)) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun logAppStatistics() {
        try {
            val pm = packageManager
            val info = pm.getPackageInfo(packageName, 0)
            val isPrivilegedApp = info.applicationInfo.isPrivilegedApp()
            val isSystemApp = info.applicationInfo.isSystemApp()
            val isUpdatedSystemApp = info.applicationInfo.isUpdatedSystemApp()
            val isSignedWithPlatformKey = info.applicationInfo.isSignedWithPlatformKey()

            Log.d(TAG, "isPrivilegedApp = $isPrivilegedApp")
            Log.d(TAG, "isSystemApp = $isSystemApp")
            Log.d(TAG, "isUpdatedSystemApp = $isUpdatedSystemApp")
            Log.d(TAG, "isSignedWithPlatformKey = $isSignedWithPlatformKey")
        } catch (e: Error) {
            Log.e(TAG, "can't access system private apis")
        }
    }
}