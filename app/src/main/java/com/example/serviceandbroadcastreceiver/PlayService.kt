package com.example.serviceandbroadcastreceiver

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Button
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager

class PlayService : Service() {

    private val intent = Intent()
    private val PLAYING_TIME = "playing time"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        showNotification()
        play()
        Log.d("ttt", "onCreate")
    }

    private fun play() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.sound)
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
}