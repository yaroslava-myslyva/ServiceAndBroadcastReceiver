package com.example.serviceandbroadcastreceiver

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.serviceandbroadcastreceiver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CallBack {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button = findViewById<Button>(R.id.button)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        val receiver = PlayingReceiver()
        receiver.setCallBack(this)
        this.registerReceiver(receiver, IntentFilter("PLAYING"))

        button.setOnClickListener {
            Intent(this, PlayService::class.java).also { intent ->
                ContextCompat.startForegroundService(this, intent)
            }
        }
    }

    override fun percent(value: Int) {
        progressBar.progress = value
    }

}