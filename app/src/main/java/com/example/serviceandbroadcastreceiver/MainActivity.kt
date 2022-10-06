package com.example.serviceandbroadcastreceiver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder

class MainActivity : AppCompatActivity() {

    //Сделать сервис как Bound(т.е. у вас получится одновременно Bound и Foreground сервис.
    // Просто он будет Bound до тех пор, пока не закроется приложение) поменять
    // TextView/ProgressBar на SeekBar и при его использовании вызывать метод MediaPlayer.seekTo() в сервисе.
    //Т.е. чтобы был ползунок, которым можно выбирать с какого места запускать или продолжать проигрывание саундтрека.
    //3. Если вы закрыли приложение, ваш Bound Foreground сервис должен продолжать жить
    // и при запуске приложения нужно обратно привязаться к сервису, а не запускать с нуля, если он остановился.
    // Подсказка: почитать разницу между startService() и bindService().
    // Понимать, в каких ситуациях Bound сервис продолжит жить, а в каких нет:

    var customService: PlayService? = null

    override fun onStart() {
        super.onStart()
        Intent(this, PlayService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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
            customService?.publicMethodToCall()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customService = null
        }
    }
}