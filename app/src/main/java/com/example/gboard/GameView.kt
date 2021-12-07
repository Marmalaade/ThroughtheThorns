package com.example.gboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import androidx.annotation.RequiresApi
import com.example.gboard.activities.GboardActivity
import com.example.gboard.data.Network
import com.example.gboard.gameObjects.GameObject
import com.example.gboard.players.GamePlayer
import kotlin.math.pow
import kotlin.math.sqrt

class GameView : View {

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	var players: Array<GamePlayer>? = null
	var level: Array<GameObject>? = null
	var isMultiplayer = false
	var levelMap: Drawable? = null

	var appleColor: Int = 0
	var finishColor: Int = 0
	var wallColor: Int = 0

	var onFinishAction: () -> Unit = {}

	init {
		setWillNotDraw(false)
		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
			isDrawingCacheEnabled = true*/
	}

	private var isFirstDraw = true

	override fun onDraw(canvas: Canvas) {
		if (level != null && players != null) {
			if (isFirstDraw) {
				players!!.forEach {
					it.onMeasure(width, height)
				}

				levelMap?.setBounds((width * 0.25f).toInt(), -height * 3, width * 4, height * 4)
				isFirstDraw = false
			}

			if (players!![0].isRunning()) {
				val newX = -players!![0].getX() + width / 2f
				val newY = -players!![0].getY() + height / 2f
				canvas.translate(newX, newY)
			} else if (players!![0].isDisappear() && !players!![0].isFinished()) {
				players!![0].create(width / 2f, height / 2f)
				if (isMultiplayer)
					Network.sendRespawnFlag()
			}

			super.onDraw(canvas)

			levelMap?.draw(canvas)
//			level!!.forEach {
//				it.draw(canvas, players!![0].getHeight())
//			}

			players!!.forEach {
				it.draw(canvas)
			}

			if (!isMultiplayer && players!![0].isFinished() && players!![0].isDisappear()) {
				onFinishAction()
			}

			if (!players!![0].isDisappear() && players!![0].isRunning()) {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
					getBitmap {
						players!![0].isCollided(it, wallColor, appleColor, finishColor)
						it.recycle()
					}
				}
				if (isMultiplayer) {
					if (players!![0].isCollided()) {
						Network.sendCollidedFlag()
					}
					if (players!![0].isVelocityChanged()) {
						Network.sendVelocity(players!![0].getVelocity())
					}
					if (players!![0].isFinished()) {
						Network.sendFinishFlag()
						onFinishAction()
					}
				}
			}

			invalidate()
		}
	}

	private var touchX = -1f
	private var touchY = -1f

	private var difX = 0f
	private var difY = 0f

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		if (players != null) {
			when (event?.actionMasked) {
				MotionEvent.ACTION_DOWN -> {
					touchX = event.x
					touchY = event.y
					if (!players!![0].isRunning()) {
						players!![0].setRunning(true)
						if (isMultiplayer) {
							Network.sendStartFlag()
						}
					}
				}

				MotionEvent.ACTION_MOVE -> {
					difX = event.x - width / 2f
					difY = event.y - height / 2f
					//if(abs(difX) >= 10f && abs(difY) >= 10f) {
					val len = sqrt(difX.pow(2) + difY.pow(2))
					//if (touchX >= 0f && touchY >= 0f && abs(difX) > 1f && abs(difY) > 1f) {
					players!![0].setCos(difX / len)
					players!![0].setSin(difY / len)
					if (isMultiplayer)
						Network.sendDirection(players!![0].getCos(), players!![0].getSin())
					//}
					//	difX = 0f
					//	difY = 0f
					//}
					//touchX = event.x
					//touchY = event.y
				}

				MotionEvent.ACTION_UP -> {
					if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O && !players!![0].isFinished()) {
						if (isMultiplayer) {
							players!![0].setFinished(true)
							Network.sendFinishFlag()
							onFinishAction()
						}
					}
					touchX = -1f
					touchY = -1f
				}
			}
		}
		return true
	}

	@RequiresApi(Build.VERSION_CODES.O)
	fun getBitmap(callback: (Bitmap) -> Unit) {
		GboardActivity.instance.window?.let { window ->
			val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
			val locationOfViewInWindow = IntArray(2)
			this.getLocationInWindow(locationOfViewInWindow)
			try {
				PixelCopy.request(window, Rect(locationOfViewInWindow[0], locationOfViewInWindow[1], locationOfViewInWindow[0] + this.width, locationOfViewInWindow[1] + this.height), bitmap, { copyResult ->
					if (copyResult == PixelCopy.SUCCESS) {
						callback(bitmap)
					}
				}, GboardActivity.handler)
			} catch (e: IllegalArgumentException) {
				e.printStackTrace()
			}
		}
	}
}