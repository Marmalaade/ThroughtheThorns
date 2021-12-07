package com.example.gboard.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.gboard.data.Levels
import com.example.gboard.R
import com.example.gboard.data.Action
import com.example.gboard.data.Network
import com.example.gboard.data.Settings
import com.example.gboard.ext.isInternetAvailable
import com.example.gboard.players.GamePlayer
import com.example.gboard.players.Snake
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.finish_dialog.*
import java.net.DatagramPacket
import java.nio.ByteBuffer

class GameActivity : GboardActivity() {

	private var mediaPlayer: MediaPlayer? = null

	private fun getMediaPlayer(): MediaPlayer {
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(this, R.raw.game_music)
			mediaPlayer!!.isLooping = true
		}
		return mediaPlayer!!
	}

	private val finishDialog by lazy {
		Dialog(this).apply {
			window?.setBackgroundDrawableResource(android.R.color.transparent)
			setContentView(R.layout.finish_dialog)
			retry_button.setOnClickListener {
				retry_button.text = "Retry"
				if (!isMultiplayer) {
					snakes[0].respawn()
					dismiss()
				} else {
					Network.sendRetryFlag()
					retryPressed = true
					if (retryAccepted) {
						finishShowed = false
						dismiss()
						multiPlayerSnakes[0].respawn()
						multiPlayerSnakes[1].respawn()
						retryPressed = false
						retryAccepted = false
					}
				}
			}

			exit_button.setOnClickListener {
				dismiss()
				this@GameActivity.finish()
			}
		}
	}

	private var internetReceiver: MenuConnectionStateMonitor? = null

	inner class MenuConnectionStateMonitor : ConnectivityManager.NetworkCallback() {
		private val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		private val networkRequest = NetworkRequest.Builder()
			.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
			.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
			.build()

		private var isConnected = isInternetAvailable()

		override fun onAvailable(network: android.net.Network) {
			super.onAvailable(network)
			isConnected = true
		}

		override fun onUnavailable() {
			super.onUnavailable()
			isConnected = false
			Toast.makeText(this@GameActivity, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show()
			this@GameActivity.finish()
		}

		fun registerNetworkCallback() = connectivityManager.registerNetworkCallback(networkRequest, this)
		fun unregisterNetworkCallback() = connectivityManager.unregisterNetworkCallback(this)
	}

	private val levels by lazy {
		Levels()
	}

	private var isMultiplayer = false
	private var level = 0
	private val multiPlayerSnakes: Array<GamePlayer> = arrayOf(Snake(Color.RED, 255), Snake(Color.BLUE, 150))
	private val snakes: Array<GamePlayer> = arrayOf(Snake(Color.RED, 255))
	private var retryPressed = false
	private var retryAccepted = false
	private var finishShowed = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_game)
		val arguments = intent.extras
		if (arguments != null) {
			level = arguments.getInt("Level", 0)
			isMultiplayer = arguments.getBoolean("Multiplayer", false)
		}
		setBackground(level)

		if (isMultiplayer) {
			Network.waitGameStartFlag()
			Network.startGameFlag.onChange.add(object : Action<Byte> {
				override fun run(result: Byte) {
					multiPlayerSnakes[1].setRunning(true)
					Network.receiveMessage()
					Network.startGameFlag.onChange.remove(this)
					Network.startGameFlag.value = 0
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
						}
						Network.SNAKE_COLLIDED -> {
							multiPlayerSnakes[1].setCollided()
						}
						Network.SNAKE_RESPAWN -> {
							multiPlayerSnakes[1].respawn()
						}
						Network.SNAKE_VELOCITY -> {
							val velocity = ByteBuffer.wrap(bytes, 1, 4).float
							multiPlayerSnakes[1].setVelocity(velocity)
						}
						Network.SNAKE_FINISHED -> {
							if (!finishShowed) {
								retryAccepted = false
								retryPressed = false
								multiPlayerSnakes[1].setFinished(true)
								finishShowed = true
								this@GameActivity.runOnUiThread {
									finishDialog.apply {
										result_text.text = "You lose"
										result_text.setTextColor(Color.RED)
										coin.visibility = View.INVISIBLE
										coin_number.visibility = View.INVISIBLE
										show()
									}
								}
							}
						}
						Network.RETRY_FLAG -> {
							retryAccepted = true
							finishDialog.retry_button.post {
								finishDialog.retry_button.text = "Retry [1]"
							}
							if (retryPressed) {
								finishShowed = false
								retryAccepted = false
								retryPressed = false
								this@GameActivity.runOnUiThread {
									finishDialog.dismiss()
									multiPlayerSnakes[0].respawn()
									multiPlayerSnakes[1].respawn()
									finishDialog.retry_button.post {
										finishDialog.retry_button.text = "Retry"
									}
								}
							}
						}
						Network.GAME_STARTED -> {
							multiPlayerSnakes[1].setRunning(true)
						}
						Network.CONNECTION_END -> {
							Toast.makeText(this@GameActivity, "Player disconnected", Toast.LENGTH_SHORT).show()
							finish()
						}
					}
					Network.receiveMessage()
				}
			})
		}

		gameView.level = levels.getLevel(0/*level*/)
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
			gameView.levelMap = ContextCompat.getDrawable(this, getMapID(level, isMultiplayer))
		}
		gameView.isMultiplayer = isMultiplayer
		gameView.appleColor = ContextCompat.getColor(this, R.color.apple_color)
		gameView.finishColor = ContextCompat.getColor(this, R.color.finish_color)
		gameView.wallColor = ContextCompat.getColor(this, R.color.wall_color)
		gameView.onFinishAction = {
			if (!finishShowed) {
				this.runOnUiThread {
					finishDialog.apply {
						coin_number.visibility = View.VISIBLE
						coin.visibility = View.VISIBLE
						result_text.text = "You finished"
						result_text.setTextColor(Color.WHITE)
						Settings.coins += 1
						Settings.save(this@GameActivity)
						finishShowed = true
						show()
					}
				}
				Network.receiveMessage()
			}
		}
		gameView.players = if (isMultiplayer) multiPlayerSnakes else snakes//arrayOf(Snake(), Snake())
	}

	private fun setBackground(index: Int) {
		when (index) {
			0 -> game_layout.setBackgroundResource(R.drawable.arctic)
			1 -> game_layout.setBackgroundResource(R.drawable.desert)
			2 -> game_layout.setBackgroundResource(R.drawable.jungle)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		Network.disconnect()
	}

	override fun onResume() {
		super.onResume()
		playMusic(Settings.soundEnabled)
		(internetReceiver ?: MenuConnectionStateMonitor().also { internetReceiver = it }).registerNetworkCallback()
	}

	override fun onPause() {
		super.onPause()
		playMusic(false)
		internetReceiver?.unregisterNetworkCallback()
	}

	private fun playMusic(value: Boolean) {
		if (value && Settings.soundEnabled && !getMediaPlayer().isPlaying) {
			getMediaPlayer().start()
		} else if (getMediaPlayer().isPlaying) {
			getMediaPlayer().pause()
			getMediaPlayer().release()
			mediaPlayer = null
		}
	}

	override fun onBackPressed() {
		super.onBackPressed()
		finish()
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

	private fun getMapID(level: Int, isMultiplayer: Boolean): Int {
		when (level) {
			0 -> {
				return if (isMultiplayer) R.drawable.map_arctic_multiplayer else R.drawable.map_arctic_singleplayer
			}
			1 -> {
				return if (isMultiplayer) R.drawable.map_desert_multiplayer else R.drawable.map_desert_singleplayer
			}
			2 -> {
				return if (isMultiplayer) R.drawable.map_jungle_multiplayer else R.drawable.map_jungle_singleplayer
			}
		}
		return R.drawable.map_arctic_singleplayer
	}
}