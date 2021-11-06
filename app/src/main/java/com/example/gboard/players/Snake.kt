package com.example.gboard.players

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.example.gboard.FrameTimer

class Snake : GamePlayer {

	private val lines = ArrayList<Line>()

	private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		color = Color.RED
		strokeWidth = 5f
		strokeCap = Paint.Cap.ROUND
	}

	private var _cos = 0f
	private var _sin = 0f
	private var maxLineLength = 0
	private var _isRunning = false
	private var _isCollided = false
	private var _isDisappear = false
	private var _size = 40
	private val frameTimer = FrameTimer()
	private var defaultVelocity = 0.5f
	private var velocity = 0.5f

	override fun create(startX: Float, startY: Float) {
		_isRunning = false
		_isCollided = false
		_isDisappear = false
		_cos = 0f
		_sin = 0f
		for (i in 0 until getSize()) {
			lines.add(Line(startX - (i + 1) * maxLineLength, startY, startX, startY, getSize() - i.toFloat()))
		}
	}

	override fun draw(canvas: Canvas) {
		var i = 0
		val delta = frameTimer.getDeltaTime().toFloat()
		val k = if (delta > 0) 11.1f / delta else 1f
		velocity = defaultVelocity * k
		while (i < lines.size) {
			paint.strokeWidth = lines[i].width
			canvas.drawLine(lines[i].x1, lines[i].y1, lines[i].x2, lines[i].y2, paint)
			if (isRunning())
				lines[i].width -= velocity
			if (lines[i].width <= 0f) {
				val line = lines[lines.size - 1]
				if (!_isCollided)
					lines.add(Line(line.x2, line.y2, line.x2 + maxLineLength * _cos, line.y2 + maxLineLength * _sin, getSize().toFloat()))
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

	@SuppressLint("DrawAllocation")
	override fun onMeasure(measuredWidth: Int, measuredHeight: Int) {
		_size = (40f * measuredWidth / 2280f).toInt()
		defaultVelocity = 0.5f * 2280f / measuredWidth
		maxLineLength = measuredWidth * 14 / 2280
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

	override fun setRunning(value: Boolean) {
		_isRunning = value
	}

	override fun isCollided(bitmap: Bitmap, color: Int): Boolean {
		if (lines.size > 0) {
			val centerX = bitmap.width / 2
			val centerY = bitmap.height / 2
			val size = 18//getSize() / 3
			val pixels = arrayOf(Pair(centerX, centerY), Pair(centerX - size, centerY), Pair(centerX, centerY - size), Pair(centerX + size, centerY), Pair(centerX, centerY + size), Pair(centerX + size, centerY + size), Pair(centerX - size, centerY - size), Pair(centerX - size, centerY + size), Pair(centerX + size, centerY - size))
			pixels.forEach {
				if (bitmap.getPixel(it.first, it.second) == Color.BLACK) {
					_isCollided = true
					return true
				}
			}
		}
		return false
	}

	class Line(val x1: Float, val y1: Float, val x2: Float, val y2: Float, var width: Float)
}