package com.example.gboard.players

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import com.example.gboard.FrameTimer

class Snake(private val color: Int, private val alpha: Int) : GamePlayer {

	private val lines = ArrayList<Line>()

	private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = this@Snake.color
		alpha = this@Snake.alpha
		strokeWidth = 5f
		strokeCap = Paint.Cap.ROUND
	}

	private var _cos = 0f
	private var _sin = 0f
	private var maxLineLength = 0
	private var maxLineWidth = 0f
	private var _isRunning = false
	private var _isCollided = false
	private var _isDisappear = false
	private var _isFinished = false
	private var _isAccelerated = false
	private var _isDecelerated = false
	private var _isMultiplayer = false
	private var _size = 40
	private val frameTimer = FrameTimer()
	private var defaultVelocity = 5f
	private var velocity = 0.5f
	private var _startX = 0f
	private var _startY = 0f

	override fun create(startX: Float, startY: Float) {
		_startX = startX
		_startY = startY
		_isRunning = false
		_isCollided = false
		_isDisappear = false
		_isAccelerated = false
		_isDecelerated = false
		_isFinished = false
		paint.alpha = 255
		defaultVelocity = 5f
		for (i in 0 until getSize()) {
			val width = maxLineWidth - i
			if (width > 0) {
				val x1 = startX - (i + 1) * maxLineLength
				lines.add(Line(x1, startY, startX, startY, width))
			}
		}
	}

	override fun draw(canvas: Canvas) {
		var i = 0
		val delta = frameTimer.getDeltaTime().toFloat()
		val k = if (delta > 0) delta / 11.1f else 1f
		if (!_isMultiplayer) {
			if (_isAccelerated) {
				defaultVelocity += 0.1f
				if (defaultVelocity >= 10f) {
					_isDecelerated = true
					_isAccelerated = false
				}
			} else if (_isDecelerated) {
				defaultVelocity -= 0.1f
				if (defaultVelocity <= 5f) {
					defaultVelocity = 5f
					_isDecelerated = false
				}
			}
		}
		velocity = defaultVelocity * canvas.width * k / 2280f
		val vX = velocity * _cos
		val vY = velocity * _sin
		if (lines.size > 0) {
			val line = lines[lines.size - 1]
			if (isRunning() && !_isCollided) {
				line.x2 += vX
				line.y2 += vY
			}
			if (_isFinished) {
				if (paint.alpha > 0)
					paint.alpha -= 5
				else {
					_isDisappear = true
					_isRunning = false
				}
			}
			while (i < lines.size) {
				paint.strokeWidth = lines[i].width
				canvas.drawLine(lines[i].x1, lines[i].y1, lines[i].x2, lines[i].y2, paint)
				if (isRunning())
					lines[i].width -= velocity / maxLineLength
				if (lines[i].width <= 0f) {
					if (!_isCollided)
						lines.add(Line(line.x2, line.y2, line.x2, line.y2, maxLineWidth))
					lines.remove(lines[i])
					if (lines.size < 1) {
						_isDisappear = true
						_isRunning = false
					}
					i--
				}
				i++
			}
		}
	}

	@SuppressLint("DrawAllocation")
	override fun onMeasure(measuredWidth: Int, measuredHeight: Int) {
		_size = (40f * 2280f / measuredWidth).toInt()
		maxLineLength = 2280 * 14 / measuredWidth
		maxLineWidth = 40f * measuredWidth / 2280f
		create(measuredWidth / 2f, measuredHeight / 2f)
	}

	override fun getX(): Float = if (lines.size > 0) lines[lines.size - 1].x2 else 0f

	override fun getY(): Float = if (lines.size > 0) lines[lines.size - 1].y2 else 0f

	override fun setCos(value: Float) {
		_cos = value
	}

	override fun setSin(value: Float) {
		_sin = value
	}

	override fun getCos(): Float = _cos

	override fun getSin(): Float = _sin

	override fun getHeight(): Float = getSize().toFloat()

	override fun getMaxLength(): Int = maxLineLength

	override fun getSize(): Int = _size

	override fun isRunning(): Boolean = _isRunning

	override fun isDisappear(): Boolean = _isDisappear

	override fun setDisappear(value: Boolean) {
		_isDisappear = value
	}

	override fun setRunning(value: Boolean) {
		_isRunning = value
	}

	override fun setCollided() {
		_isCollided = true
	}

	override fun setIsMultiplayer() {
		_isMultiplayer = true
	}

	override fun setVelocity(value: Float) {
		defaultVelocity = value
	}

	override fun getVelocity(): Float = defaultVelocity

	override fun respawn() {
		create(_startX, _startY)
	}

	override fun setFinished(value: Boolean) {
		_isFinished = value
	}

	override fun isFinished(): Boolean = _isFinished

	override fun isVelocityChanged(): Boolean = _isAccelerated || _isDecelerated

	override fun isCollided(): Boolean = _isCollided

	override fun isCollided(bitmap: Bitmap, wallColor: Int, appleColor: Int, finishColor: Int): Boolean {
		if (lines.size > 0 && !_isCollided && !_isFinished) {
			val centerX = bitmap.width / 2
			val centerY = bitmap.height / 2
			val size = 18
			val pixels = arrayOf(Pair(centerX, centerY), Pair(centerX - size, centerY), Pair(centerX, centerY - size), Pair(centerX + size, centerY), Pair(centerX, centerY + size), Pair(centerX + size, centerY + size), Pair(centerX - size, centerY - size), Pair(centerX - size, centerY + size), Pair(centerX + size, centerY - size))
			pixels.forEach {
				when (bitmap.getPixel(it.first, it.second)) {
					wallColor -> {
						_isCollided = true
						return true
					}
					finishColor -> {
						_isFinished = true
					}
					appleColor -> {
						if (!_isAccelerated) {
							_isAccelerated = true
							_isDecelerated = false
						}
					}
				}
			}
		}
		return false
	}

	class Line(val x1: Float, val y1: Float, var x2: Float, var y2: Float, var width: Float)
}