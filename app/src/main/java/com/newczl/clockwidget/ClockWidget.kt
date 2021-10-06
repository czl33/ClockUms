package com.newczl.clockwidget

import Msg
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.newczl.clockwidget.http.UmsApiService
import com.newczl.clockwidget.service.TimeInsureService
import com.newczl.clockwidget.utils.GlobalConst
import com.newczl.clockwidget.utils.TimeUtil
import com.newczl.clockwidget.utils.TimeUtil.formatTimeString
import com.newczl.clockwidget.utils.WorkState
import getBottomMsg
import getCenterMsg
import getTopMsg
import getWorkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ClockWidgetConfigureActivity]
 */
class ClockWidget : AppWidgetProvider() {

    companion object {
        //点击意图
        const val CLICK_INTENT = "com.newczl.clockwidget.click_update"
        const val TAG = "ClockWidget"
    }

    private lateinit var remoteViews: RemoteViews


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "OnReceive:Action: " + intent.action);
        when (intent.action) {
            //点击事件刷新View
            CLICK_INTENT -> {
                Log.i(TAG,"点击刷新！")
                //启用服务
                enabledService(context)
                //获取服务器数据
                getServerTime(context)
            }
            //更新时间数字
            GlobalConst.ACTION_UPDATE_ALL -> {
                onTimeMinutesChange(context)
                getServerTime(context)
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun getServerTime(context:Context) {
        GlobalScope.launch {
            val hotPlaylist =
                    try{
                        UmsApiService.instance.getHotPlaylist(Status.userId)
                    }catch (e:Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(context,"更新失败,存在错误",Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
            val msg = hotPlaylist.msg
            Log.i(TAG,"hotPlaylist:$hotPlaylist")
            Log.i(TAG,TimeUtil.getCurrentMinute().toString())
            Log.i(TAG,TimeUtil.getTime(TimeUtil.getCurrentMinute()))

            //更新整个界面
            updateRemoteView(context,msg)
        }
    }

    /**
     * 更新时间
     */
    private fun onTimeMinutesChange(context: Context) {
        updateRemoteTimeText(context, formatTimeString())
    }
    private fun updateRemoteTimeText(context: Context, text: String) {
        if (!this::remoteViews.isInitialized) {
            remoteViews = RemoteViews(context.packageName, R.layout.clock_widget)
        }

        remoteViews.setTextViewText(
            R.id.current_time,
            text
        )
        val componentName = ComponentName(context, ClockWidget::class.java)
        AppWidgetManager.getInstance(context)
            .updateAppWidget(componentName, remoteViews)
    }

    /**
     * 更新背景
     */
    private fun updateRemoteBackground(context: Context,work:WorkState){
        if (!this::remoteViews.isInitialized) {
            remoteViews = RemoteViews(context.packageName, R.layout.clock_widget)
        }
        remoteViews.setImageViewResource(
                R.id.background,
                getBackground(work)
        )
        val componentName = ComponentName(context, ClockWidget::class.java)
        AppWidgetManager.getInstance(context)
                .updateAppWidget(componentName, remoteViews)
    }


    private fun updateRemoteView(context: Context, message: Msg) {
        if (!this::remoteViews.isInitialized) {
            remoteViews = RemoteViews(context.packageName, R.layout.clock_widget)
        }
        //更新背景
        remoteViews.setImageViewResource(
                R.id.background,
                getBackground(message.getWorkState())
        )
        //更新下文字
        if(message.hourArray.isEmpty()){
            remoteViews.setViewVisibility(R.id.top_left_text, View.GONE)
            remoteViews.setViewVisibility(R.id.right_bottom_text, View.GONE)
        }else{
            remoteViews.setViewVisibility(R.id.top_left_text, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.right_bottom_text, View.VISIBLE)
        }

         remoteViews.setTextViewText(R.id.appwidget_text, message.getCenterMsg())
         remoteViews.setTextViewText(R.id.top_left_text, message.getTopMsg())
         remoteViews.setTextViewText(R.id.right_bottom_text, message.getBottomMsg())

        //提交更新
        val componentName = ComponentName(context, ClockWidget::class.java)
        AppWidgetManager.getInstance(context)
                .updateAppWidget(componentName, remoteViews)
    }




    /**
     * 调用更新时
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        //创建远程视图
        remoteViews = RemoteViews(context.packageName, R.layout.clock_widget)
        //注册点击刷新事件
        remoteViews.setOnClickPendingIntent(
            R.id.refresh,
            registerRefreshAction(context)
        )

        remoteViews.setTextViewText(R.id.current_time, formatTimeString())

        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }



    override fun onEnabled(context: Context) {
        enabledService(context)
    }

    private fun enabledService(context: Context){
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(
                Intent(
                    context,
                    TimeInsureService::class.java
                )
            )
        } else {
            context.startService(
                Intent(
                    context,
                    TimeInsureService::class.java
                )
            )
        }
    }

    override fun onDisabled(context: Context) {
        context.stopService(
            Intent(
                context,
                TimeInsureService::class.java
            )
        )
    }

    private fun registerRefreshAction(context: Context): PendingIntent {
        //注册广播
        val intent = Intent(CLICK_INTENT).apply {
            component = ComponentName(
                context,
                ClockWidget::class.java
            )
        }

        return PendingIntent.getBroadcast(
            context,
            R.id.refresh,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getBackground(workState: WorkState): Int {
        return when (workState) {
            WorkState.OF_WORK -> R.drawable.of_working_background
            WorkState.UN_PUNCH -> R.drawable.no_punch_background
            WorkState.WORKING -> R.drawable.workings_background
            WorkState.NO_WORK -> R.drawable.no_work_background
        }
    }
}

