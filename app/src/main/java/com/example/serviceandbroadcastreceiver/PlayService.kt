package com.example.serviceandbroadcastreceiver

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class PlayService : Service() {
    private val binder = CustomBinder()

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        doSomethingOnCreate()
    }

    fun publicMethodToCall() {
        // do something
    }

    private fun doSomethingOnCreate() {
        // do something
        // ...
        stopSelf()
    }

    inner class CustomBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): PlayService = this@PlayService
    }
}