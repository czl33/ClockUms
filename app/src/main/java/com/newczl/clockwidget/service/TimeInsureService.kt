package com.newczl.clockwidget.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.newczl.clockwidget.R
import com.newczl.clockwidget.broadcast.MinutesTimerBroadcast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 该服务用来启动每分钟定时触发的广播，并确保应用程序所发出的任务的执行
 * */
class TimeInsureService: Service() {

    private val channelId = "time_insure_service_id"

    private val channelName = "time_insure_service_name"

    private val notificationId = 1

    private val receiver: MinutesTimerBroadcast = MinutesTimerBroadcast()


    override fun onBind(intent: Intent?): IBinder? {
//        return alarmTaskInterface
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notificationBuilder: Notification.Builder = generateNotificationBuilder()

        /**开启前台服务*/
        notificationBuilder.apply {
            setContentTitle("哦吼")
            setContentText("正在进行倒时监听～通知不见了要刷新哦～")
            setSmallIcon(R.drawable.dog)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.dog))
        }.build().also {
            startForeground(notificationId, it)
        }

        /**时钟更新receiver*/
        registerReceiver(receiver, IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            //提升优先级，尽可能每分钟能触发任务
            priority = 1000
        })

        /**开启整点报时*/
        //TimeReminder.start(applicationContext)

        /**开启闹钟任务*/
//        alarmReminder.initAllAlarm()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        //TimeReminder.stop(applicationContext)
//        alarmReminder.removeAllAlarmJob()
        stopForeground(true)
    }

    private fun generateNotificationBuilder(): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= 26) {

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .apply {
                    createNotificationChannel(
                        NotificationChannel(
                            channelId,
                            channelName,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    )
                }

            Notification.Builder(this, channelId)

        } else {
            Notification.Builder(this)
        }
    }

}