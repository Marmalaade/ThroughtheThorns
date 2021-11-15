package com.example.gboard.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.example.gboard.R
import com.example.gboard.data.Action
import com.example.gboard.data.Network
import kotlinx.android.synthetic.main.activity_connection.*
import java.net.DatagramPacket
import java.util.*

class ConnectionActivity : GboardActivity() {
	private var level = 0

	private val timer = Timer()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_connection)

		val arguments = intent.extras
		if (arguments != null) {
			level = arguments.getInt("Level", 0)
		}

		Network.receivePacket.onChange.add(object : Action<DatagramPacket> {
			override fun run(result: DatagramPacket) {
				val bytes = result.data
				when (bytes[0]) {
					Network.CONNECTION_STARTED -> {
						status_text.post { status_text.text = "Connection established" }
						status_text.postDelayed({
							status_text.text = "Searching for other players.."
						}, 500)
						Network.receiveMessage()
					}
					Network.CONNECTION_ESTABLISHED -> {
						status_text.post {
							status_text.text = "Player is found!"
						}
						openGame()
					}
				}
			}
		})

		Network.ping.onChange.add(object : Action<Long> {
			override fun run(result: Long) {
				ping_text.post {
					ping_text.visibility = View.VISIBLE
					ping_text.text = "You ping: ${result / 1000f} ms"
				}
				Network.ping.onChange.remove(this)
			}
		})

		Network.sendRequestToServer(level.toByte())
		Network.receiveMessage()

		back_button.setOnClickListener {
			finish()
		}

		timer.schedule(object : TimerTask() {
			override fun run() {
				pointAnimation()
			}
		}, 1000, 1000)
	}

	private val points = "..."
	private fun pointAnimation() {
		points_text.post {
			points_text.text = if (points_text.text.length == 3) "" else points.substring(0, points_text.text.length + 1)
		}
	}

	private fun openGame() {
		Network.receivePacket.onChange.clear()
		Network.ping.onChange.clear()
		startActivity(
			Intent(this, GameActivity::class.java)
				.putExtra("Multiplayer", true)
				.putExtra("Level", level)
		)
		finish()
	}

	override fun onBackPressed() {
		super.onBackPressed()
		Network.disconnect()
	}

	override fun onDestroy() {
		super.onDestroy()
		timer.cancel()
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