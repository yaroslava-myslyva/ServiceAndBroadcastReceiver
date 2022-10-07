package com.example.serviceandbroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PlayReceiver : BroadcastReceiver() {
    private lateinit var callBack: CallBack

    fun setCallBack(callBack: CallBack){
        this.callBack = callBack
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ttt", "catch")
        if (intent.action == "PLAYING") {
            callBack.percent(intent.getIntExtra("playing time", 0))
        }
    }
}