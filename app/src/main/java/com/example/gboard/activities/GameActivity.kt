package com.example.gboard.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.example.gboard.data.Levels
import com.example.gboard.R
import com.example.gboard.data.Action
import com.example.gboard.data.Network
import com.example.gboard.players.GamePlayer
import com.example.gboard.players.Snake
import kotlinx.android.synthetic.main.activity_game.*
import java.net.DatagramPacket
import java.nio.ByteBuffer

class GameActivity : GboardActivity() {

	private val levels by lazy {
		Levels()
	}

	private var isMultiplayer = false
	private var level = 0
	private val multiPlayerSnakes: Array<GamePlayer> = arrayOf(Snake(), Snake())
	private val snakes: Array<GamePlayer> = arrayOf(Snake())

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_game)
		val arguments = intent.extras
		if (arguments != null) {
			level = arguments.getInt("Level", 0)
			isMultiplayer = arguments.getBoolean("Multiplayer", false)
		}
		Log.e("Multiplayer", "$isMultiplayer")
		setBackground(level)

		if(isMultiplayer) {
			Network.waitGameStartFlag()

			Network.startGameFlag.onChange.add(object : Action<Byte> {
				override fun run(result: Byte) {
					multiPlayerSnakes[1].setRunning(true)
					Network.receiveMessage()
				}
			})

			Network.receivePacket.onChange.add(object : Action<DatagramPacket> {
				override fun run(result: DatagramPacket) {
					val bytes = result.data
					when (bytes[0]) {
						Network.SNAKE_DIRECTION -> {
							val cos = ByteBuffer.wrap(bytes, 1, 4).float
							val sin = ByteBuffer.wrap(bytes, 5, 4).float
							multiPlayerSnakes[1].setCos(cos)
							multiPlayerSnakes[1].setSin(sin)
							Network.receiveMessage()
						}
					}
				}
			})
		}

		gameView.level = levels.getLevel(0/*level*/)
		gameView.isMultiplayer = isMultiplayer
		gameView.players = if(isMultiplayer) multiPlayerSnakes else snakes//arrayOf(Snake(), Snake())
	}

	private fun setBackground(index: Int) {
		when (index) {
			0 -> game_layout.setBackgroundResource(R.drawable.arctic)
			1 -> game_layout.setBackgroundResource(R.drawable.desert)
			2 -> game_layout.setBackgroundResource(R.drawable.jungle)
		}
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