package com.newczl.clockwidget.broadcast

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.newczl.clockwidget.ClockWidget
import com.newczl.clockwidget.Status
import com.newczl.clockwidget.utils.GlobalConst

/**
 * 该广播用于监听时刻变化
 * */
class MinutesTimerBroadcast: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        when(intent.action) {

            Intent.ACTION_TIME_TICK -> {
                //发送更新时间广播

                if (!Status.isScreenOff) {
                    context.sendBroadcast(Intent(GlobalConst.ACTION_UPDATE_ALL).apply {
                        component = ComponentName(
                            context, ClockWidget::class.java
                        )
                    })

                }

            }
            //亮屏
            Intent.ACTION_SCREEN_ON -> {
                Status.isScreenOff = false
                context.sendBroadcast(Intent(GlobalConst.ACTION_UPDATE_ALL).apply {
                    component = ComponentName(
                            context, ClockWidget::class.java
                    )
                })
            }
            //熄屏
            Intent.ACTION_SCREEN_OFF -> {
                Status.isScreenOff = true
            }

        }

    }


}