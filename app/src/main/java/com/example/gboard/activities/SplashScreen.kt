package com.example.gboard.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.gboard.R
import kotlinx.android.synthetic.main.activity_splash_sreeen.*
import java.util.Timer
import java.util.TimerTask

@SuppressLint("CustomSplashScreen")
class SplashScreen : GboardActivity() {
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
