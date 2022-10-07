package com.example.serviceandbroadcastreceiver

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PlayService : Service() {
    private val binder = CustomBinder()
    private val intent = Intent()
    private val PLAYING_TIME = "playing time"
    private lateinit var  mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        Log.d("ttt", "onCreate")
        // нотіфікейшн перед роботою
        showNotification()
        play()
    }

    // актівіті каже сервісу щось зробити. Діставати таким чином дані не треба
    fun sendCurrentPercent(value:Int) {
        Log.d("ttt", "PlayService sendCurrentPosition $value")
        mediaPlayer.seekTo(mediaPlayer.duration/100*value)

    }

//    private fun doSomethingOnCreate() {
//        // do something
//        // ...
//        stopSelf()
//    }

    private fun play() {
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.sound)
        mediaPlayer.start()
        intent.action = "PLAYING"
        val thread = Thread(Runnable {
            while (mediaPlayer.currentPosition < mediaPlayer.duration) {
                Thread.sleep(1000)
                val percent = mediaPlayer.currentPosition * 100 / mediaPlayer.duration
                intent.putExtra(PLAYING_TIME, percent)
                sendBroadcast(intent)
                Log.d("ttt", percent.toString())
            }
        })
        thread.start()
    }

    private fun showNotification() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }

        val channelId = getString(R.string.channel_id)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Playing",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(1, notification)
    }

    inner class CustomBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): PlayService = this@PlayService
    }
}