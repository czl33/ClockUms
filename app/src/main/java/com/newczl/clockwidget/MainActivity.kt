package com.newczl.clockwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.work.*
import com.google.gson.Gson
import com.newczl.clockwidget.worker.SimpleWorker

class MainActivity : AppCompatActivity() {

    val test = "{\"err\":0,\"msg\":{\"TimeArray\":[\"13:33\"],\"hourArray\":[813],\"dateType\":4,\"week\":0}}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    companion object {
        const val TAG = "com.newczl.service.SimpleWorker"
    }
}