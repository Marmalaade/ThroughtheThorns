package com.example.gboard.gameObjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Direct(private val length: Float, private val _height: Float, angle: Int) : GameObject {

	private var cos = 0f
	private var sin = 0f

	init {
		val rad = angle * PI.toFloat() / 180f
		cos = cos(rad)
		sin = sin(rad)
	}

	private var x = 0f
	private var y = 0f

	private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		//style = Paint.Style.STROKE
		strokeWidth = 30f
		color = Color.BLACK
	}

	override fun draw(canvas: Canvas, relativeSize: Float) {
		val _x = x * relativeSize
		val _y = y * relativeSize
		canvas.drawLine(_x, _y, _x + length * relativeSize * cos, _y + length * relativeSize * sin, paint)
		canvas.drawLine(_x, _y + _height * relativeSize, _x + length * relativeSize * cos, _y + length * relativeSize * sin + _height * relativeSize, paint)
	}

	override fun setPosition(relativeX: Float, relativeY: Float) {
		this.x = relativeX
		this.y = relativeY
	}

	override fun getX1(): Float = x

	override fun getY1(): Float = y

	override fun getX2(): Float = x + length * cos

	override fun getY2(): Float = y + length * sin

	override fun getCenterX(): Float = x + length * cos

	override fun getCenterY(): Float = y + length * sin + _height / 2f

	override fun getWidth(): Float = length * cos

	override fun getHeight(): Float = length * sin + _height

}