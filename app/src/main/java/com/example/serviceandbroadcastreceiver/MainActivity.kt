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

    //Сделать сервис как Bound(т.е. у вас получится одновременно Bound и Foreground сервис.
    // Просто он будет Bound до тех пор, пока не закроется приложение) поменять
    // TextView/ProgressBar на SeekBar и при его использовании вызывать метод MediaPlayer.seekTo() в сервисе.
    //Т.е. чтобы был ползунок, которым можно выбирать с какого места запускать или продолжать проигрывание саундтрека.
    //3. Если вы закрыли приложение, ваш Bound Foreground сервис должен продолжать жить
    // и при запуске приложения нужно обратно привязаться к сервису, а не запускать с нуля, если он остановился.
    // Подсказка: почитать разницу между startService() и bindService().
    // Понимать, в каких ситуациях Bound сервис продолжит жить, а в каких нет:

    var customService: PlayService? = null
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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


    }


    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        customService = null
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            customService = (service as PlayService.CustomBinder).getService()
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        Log.d("ttt", "seekBar.setOnSeekBarChangeListener onProgressChanged $progress")
                        customService?.sendCurrentPercent(progress)

                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customService = null
        }
    }

    override fun percent(value: Int) {
        Log.d("ttt", "override percent = $value")
        seekBar.progress = value
    }
}


