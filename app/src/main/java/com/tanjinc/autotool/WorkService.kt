package com.tanjinc.autotool

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock
import android.util.Log
import java.util.*


class WorkService : Service() {

    override fun onCreate() {
        super.onCreate()
    }
    override fun onBind(intent: Intent?): IBinder ?{
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread(Runnable {
            autoTask()
        }).start()

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val anHour = 1  * 1000   // 这是一小时的毫秒数

        val triggerAtTime = SystemClock.elapsedRealtime() + anHour
        val i = Intent(this, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(this, 0, i, 0)

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun autoTask() {
        Log.d("LongRunningService", "executed at " + Date().toString())

    }
}