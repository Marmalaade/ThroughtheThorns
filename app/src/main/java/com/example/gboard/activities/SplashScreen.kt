package com.example.gboard.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
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
		load_view.postDelayed({
			startActivity(intent)
			finish()
		}, 600)
	}

	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		if (hasFocus) {
			window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				window.setDecorFitsSystemWindows(false)
				window.insetsController!!.hide(WindowInsets.Type.navigationBars())
				window.insetsController!!.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
			}
		} else {
			window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					or View.SYSTEM_UI_FLAG_VISIBLE)
		}
	}
}
