package com.newczl.clockwidget.worker

import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.work.*
import com.newczl.clockwidget.R
import com.newczl.clockwidget.http.UmsApiService
import java.lang.Exception

class SimpleWorker(val context: Context, private val parameters: WorkerParameters) :
    CoroutineWorker(context,parameters) {
    companion object{
        const val TAG = "SimpleWorker"
        const val DATA = "SIMPLE_WORKER_DATA"
    }
    override suspend fun doWork(): Result {
       return  try {
           inputData.getString(DATA)?.let{
               val hotPlaylist = UmsApiService.instance.getHotPlaylist(it)
               //更新remoteView
               Log.i(TAG,"hotplaylist:${hotPlaylist}")
           }
           Result.success()
        }catch (e:Exception){
           e.printStackTrace()
           Result.failure()
        }

    }
}