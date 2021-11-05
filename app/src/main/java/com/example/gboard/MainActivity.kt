package com.example.gboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.parser.IntegerParser
import com.divyanshu.colorseekbar.ColorSeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.options_dialog.*

import java.lang.Math.abs
import java.lang.Math.random

class MainActivity : GboardActivity() {

	private var mediaPlayer: MediaPlayer? = null
	private lateinit var viewPager2: ViewPager2

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		alphaAnimation(gameTittle)
		mediaPlayer = MediaPlayer.create(this, R.raw.menu_music)
		mediaPlayer!!.start()
		viewPager2 = findViewById(R.id.view_Pager_lvlSlider)
		val sliderItems: MutableList<SliderItem> = ArrayList()
		sliderItems.add(SliderItem(R.drawable.arctic))
		sliderItems.add(SliderItem(R.drawable.desert))
		sliderItems.add(SliderItem(R.drawable.jungle))
		val pageClickListenner = object : OnPageClickListener {
			override fun onPageClick(position: Int, sliderItem: SliderItem) {
				when (position) {
					0 -> customDialog(0)
					1 -> customDialog(1)
					2 -> customDialog(2)
				}
			}

		}

		viewPager2.adapter = SliderAdapter(sliderItems, viewPager2, pageClickListenner)

		viewPager2.clipToPadding = false
		viewPager2.clipChildren = false
		viewPager2.offscreenPageLimit = 3
		viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

		val compositePageTransformer = CompositePageTransformer()
		compositePageTransformer.addTransformer(MarginPageTransformer(30))
		compositePageTransformer.addTransformer { page, position ->
			val r = 1 - abs(position)
			page.scaleY = 0.85f + r * 0.15f
			page.alpha = 0.5f + r * 0.5f
		}
		viewPager2.setPageTransformer(compositePageTransformer)
		viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)
			}
		})

		coin.setOnClickListener {
			flipCoin(R.drawable.ic_coin)
			coin_nubmer.text = (0..10).random().toString()
		}
		settings_button.setOnClickListener {
			scaleAnimation(settings_button)
			optionsDialog()
		}
		staticCoinflip(R.drawable.ic_coin)
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

	private fun scaleAnimation(imageButton: ImageButton) {
		imageButton.animate().apply {
			duration = 150
			scaleX(1.5f)
			scaleY(1.5f)
			startDelay = 15
		}.withEndAction {
			imageButton.animate().apply {
				duration = 150
				scaleX(1.0f)
				scaleY(1.0f)
				startDelay = 30
			}
		}.start()
	}

	private fun staticCoinflip(imageId: Int) {
		coin.animate().apply {
			duration = 20000
			rotationYBy(1800f)
			coin.isClickable = true
		}.withEndAction {
			coin.setImageResource(imageId)
		}.start()
	}

	private fun flipCoin(imageId: Int) {
		coin.animate().apply {
			duration = 800
			rotationYBy(1800f)
			coin.isClickable = true
		}.withEndAction {
			coin.setImageResource(imageId)
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

	private fun optionsDialog() {
		val optionsDialog = Dialog(this)
		optionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		optionsDialog.setContentView(R.layout.options_dialog)
		optionsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		optionsDialog.roundedImageViewSettings.setImageResource(R.drawable.background)
//		fun saveData(isChacked: Boolean, key: String) {
//			val sharedPreferences = getSharedPreferences("sahredPref", Context.MODE_PRIVATE)
//			val editor = sharedPreferences.edit()
//			editor.putBoolean(key, isChacked)
//			editor.apply()
//		}
//
//		fun loadData(key: String): Boolean {
//			val sharedPreferences = getSharedPreferences("sahredPref", Context.MODE_PRIVATE)
//			return sharedPreferences.getBoolean(key, false)
//		}
//		optionsDialog.music_checkbox.isChecked = loadData("save")
		optionsDialog.save_button.setOnClickListener {
//			saveData(optionsDialog.music_checkbox.isChecked, "save")
		}
		optionsDialog.color_seekbar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
			override fun onColorChangeListener(color: Int) {
				optionsDialog.circle_field.background.setTint(color)
			}
		})
		optionsDialog.music_checkbox.isChecked = true
		optionsDialog.music_checkbox.setOnCheckedChangeListener { _, isChecked ->
			if (!isChecked) {
				controlMusic()
			} else controlMusic()
		}

		optionsDialog.show()
	}

	private fun customDialog(index: Int) {

		val dialog = Dialog(this)
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		dialog.setContentView(R.layout.custom_dialog)
		when (index) {
			0 -> dialog.roundedImageView.setImageResource(R.drawable.arctic)
			1 -> dialog.roundedImageView.setImageResource(R.drawable.desert)
			2 -> dialog.roundedImageView.setImageResource(R.drawable.jungle)
		}
		dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		if (index == 1) {
			dialog.startSinglePlayer.setOnClickListener {
				startActivity(Intent(this, GameActivity::class.java))
			}
		}
		dialog.show()
	}


	private fun controlMusic() {
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(this, R.raw.menu_music);
			mediaPlayer!!.isLooping = true
			mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
		}
		if (mediaPlayer!!.isPlaying()) {
			mediaPlayer!!.pause();

		} else {
			mediaPlayer!!.start();
		}
	}

}
