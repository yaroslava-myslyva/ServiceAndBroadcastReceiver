package com.example.serviceandbroadcastreceiver

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), CallBack {

    var customService: PlayService? = null
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // привязаться к сервису

        // у тебя сикбар в сервисе прописан
        // поэтому и не работает после перезапуска, не была установлена связь с сервисом
        // попробуй в активити вынести функционал

        val button = findViewById<Button>(R.id.button)
        seekBar = findViewById<SeekBar>(R.id.seek_bar)

        val receiver = PlayReceiver()
        receiver.setCallBack(this)
        this.registerReceiver(receiver, IntentFilter("PLAYING"))

        button.setOnClickListener {
            Intent(this, PlayService::class.java).also { intent ->
                ContextCompat.startForegroundService(this, intent)
                bindService(intent, serviceConnection, Context.BIND_IMPORTANT)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    customService?.sendCurrentPercent(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        customService = null
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            customService = (service as PlayService.CustomBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customService = null
        }
    }

    override fun percent(value: Int) {
        seekBar.progress = value
    }
}


