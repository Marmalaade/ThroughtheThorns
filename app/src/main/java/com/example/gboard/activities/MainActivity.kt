package com.example.gboard.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import com.example.gboard.*
import com.example.gboard.data.Settings
import com.example.gboard.ext.isInternetAvailable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.options_dialog.*
import kotlin.math.abs
import kotlinx.android.synthetic.main.slide_item_container.*

class MainActivity : GboardActivity() {

	private var mediaPlayer: MediaPlayer? = null

	private fun getMediaPlayer(): MediaPlayer {
		if(mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(this, R.raw.menu_music)
			mediaPlayer!!.isLooping = true
		}
		return mediaPlayer!!
	}

	private val levelDialog by lazy {
		Dialog(this).apply {
			window?.setBackgroundDrawableResource(android.R.color.transparent)
			setContentView(R.layout.custom_dialog)
			startSinglePlayer.setOnClickListener {
				startActivity(
					Intent(it.context, GameActivity::class.java)
						.putExtra("Multiplayer", false)
						.putExtra("Level", selectedLevel)
				)
				dismiss()
			}
			startMultiPlayer.setOnClickListener {
				if (isInternetAvailable()) {
					startActivity(
						Intent(it.context, ConnectionActivity::class.java)
							.putExtra("Level", selectedLevel)
							.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
					)
					dismiss()
				} else Toast.makeText(this@MainActivity, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show()
			}
		}
	}

	/*private val settingsDialog by lazy {
		Dialog(this).apply {
			window?.setBackgroundDrawableResource(android.R.color.transparent)
			setContentView(R.layout.options_dialog)
			music_checkbox.isChecked = Settings.soundEnabled
			save_button.setOnClickListener {
				Settings.soundEnabled = music_checkbox.isChecked
				Settings.save(context)
				onSettingsChanged()
				dismiss()
			}
		}
	}*/

	private val sliderAdapter by lazy {
		val sliderItems: MutableList<SliderItem> = ArrayList()
		sliderItems.add(SliderItem(R.drawable.arctic_named))
		sliderItems.add(SliderItem(R.drawable.desert_named))
		sliderItems.add(SliderItem(R.drawable.jungle_named))
		SliderAdapter(sliderItems, pager_level_slider, object : OnPageClickListener {
			override fun onPageClick(position: Int, sliderItem: SliderItem) {
				if(Settings.levelPurchases[position]) {
					showLevelDialog(position)
				} else {
					if(Settings.coins > 0) {
						Settings.coins -= 1
						Settings.levelPurchases[position] = true
						coin_text.text = "${Settings.coins}"
						Settings.save(this@MainActivity)
						showLevelDialog(position)
						pager_level_slider.adapter?.notifyItemChanged(position)
						Toast.makeText(this@MainActivity, "You have purchased this level", Toast.LENGTH_SHORT).show()
					} else {
						Toast.makeText(this@MainActivity, "Not enough money", Toast.LENGTH_SHORT).show()
					}
				}
			}
		})
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		alphaAnimation(gameTittle)

		pager_level_slider.apply {
			adapter = sliderAdapter
			clipToPadding = false
			clipChildren = false
			offscreenPageLimit = 3
			getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
			setPageTransformer(CompositePageTransformer().apply {
				addTransformer { page, position ->
					val r = 1 - abs(position)
					page.scaleY = 0.85f + r * 0.15f
					page.scaleX = 0.85f + r * 0.15f
					page.alpha = 0.5f + r * 0.5f
				}
			})
		}

		coin.setOnClickListener {
			flipCoin()
		}

		Settings.load(this)

		settings_button.apply {
			setImageResource(R.drawable.anim_volume_on_to_off)
		}.setOnClickListener {
			Settings.soundEnabled = !Settings.soundEnabled
			playMusic(Settings.soundEnabled)
			Settings.save(it.context)
			(it as ImageButton).setImageResource(if (!Settings.soundEnabled) R.drawable.anim_volume_on_to_off else R.drawable.anim_volume_off_to_on)
			it.post {
				(settings_button.drawable as? AnimatedVectorDrawable)?.start()
			}
		}
		staticCoinflip()
	}

	private fun staticCoinflip() {
		coin.animate().apply {
			duration = 20000
			rotationYBy(1800f)
		}.start()
	}

	private fun flipCoin() {
		coin.animate().apply {
			duration = 800
			rotationYBy(1800f)
		}.start()
	}

	private fun alphaAnimation(imageView: ImageView) {
		imageView.alpha = 0.0f
		imageView.animate().apply {
			interpolator = LinearInterpolator()
			duration = 800
			alpha(1f)
			startDelay = 10
			start()
		}
	}

	private var selectedLevel = 0

	private fun showLevelDialog(index: Int) {
		selectedLevel = index
		when (index) {
			0 -> levelDialog.background_image.setBackgroundResource(R.drawable.arctic)
			1 -> levelDialog.background_image.setBackgroundResource(R.drawable.desert)
			2 -> levelDialog.background_image.setBackgroundResource(R.drawable.jungle)
		}
		levelDialog.window?.setWindowAnimations(R.style.DialogScale)
		levelDialog.show()
	}

	override fun onPause() {
		super.onPause()
		playMusic(false)
	}

	override fun onResume() {
		super.onResume()
		Settings.load(applicationContext)
		onSettingsChanged()
	}

	private fun onSettingsChanged() {
		playMusic(Settings.soundEnabled)
		coin_text.text = "${Settings.coins}"
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
