package com.example.gboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gboard.gameObjects.GameObject
import com.example.gboard.players.GamePlayer
import kotlin.math.pow
import kotlin.math.sqrt

class GameView : View {

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	var players: Array<GamePlayer>? = null
	var level: Array<GameObject>? = null

	init {
		setWillNotDraw(false)
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
			isDrawingCacheEnabled = true
	}

	private var isFirstDraw = true
	private var currX = 0f
	private var currY = 0f

	override fun onDraw(canvas: Canvas) {
		if (level != null && players != null) {
			if (isFirstDraw) {
				players!!.forEach {
					it.onMeasure(width, height)
				}
				var currX = width / (2f * players!![0].getHeight())
				var currY = height / (2f * players!![0].getHeight()) - level!![0].getHeight() / 2f
				level!!.forEach {
					it.setPosition(currX, currY)
					currX = it.getX2()
					currY = it.getY2()
				}
				isFirstDraw = false
			}

			if (players!![0].isRunning()) {
				val newX = -players!![0].getX() + width / 2f
				val newY = -players!![0].getY() + height / 2f
				if(currX < newX)
					currX += 1f
				else if(currX > newX)
					currX -= 1f
				if(currY < newY)
					currY += 1f
				else if(currY > newY)
					currY -= 1f
				//currX -= (currX - newX) / 30f
				//currY -= (currY - newY) / 30f
				canvas.translate(newX, newY)
			} else if (players!![0].isDisappear()) players!![0].create(width / 2f, height / 2f)

			super.onDraw(canvas)

			level!!.forEach {
				it.draw(canvas, players!![0].getHeight())
			}

			players!!.forEach {
				it.draw(canvas)
			}

			if (!players!![0].isDisappear() && players!![0].isRunning()) {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					buildDrawingCache()
					players!![0].isCollided(getDrawingCache(true), Color.BLACK)
					destroyDrawingCache()
				} else {
					getBitmap {
						players!![0].isCollided(it, Color.BLACK)
						it.recycle()
					}
				}
			}

			invalidate()
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		if (players != null) {
			if (event?.actionMasked == MotionEvent.ACTION_DOWN) {
				if (!players!![0].isRunning())
					players!![0].setRunning(true)
			} else if (event?.actionMasked == MotionEvent.ACTION_MOVE) {
				val difX = event.x - width / 2f
				val difY = event.y - height / 2f
				val len = sqrt(difX.pow(2) + difY.pow(2))
				players!![0].setCos(difX / len)
				players!![0].setSin(difY / len)
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
				Toast.makeText(context, "Hi", Toast.LENGTH_SHORT).show()
				e.printStackTrace()
			}
		}
	}
}