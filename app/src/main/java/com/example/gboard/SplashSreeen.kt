package com.example.gboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_splash_sreeen.*
import java.util.*
import kotlin.concurrent.thread
import java.util.TimerTask as TimerTask

class SplashSreeen : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash_sreeen)
		progressBarLoading()

	}

	private fun progressBarLoading() {
		val intent = Intent(this, MainActivity::class.java)
		var counter = 0
		val limitValue = 100
		val timer = Timer()
		timer.schedule(object : TimerTask() {

			override fun run() {
				counter++
				progressBar.progress = counter
				if (counter > limitValue) {
					timer.cancel()
					startActivity(intent)
					finish()
				}
			}
		}, 4, 10)

	}

}
