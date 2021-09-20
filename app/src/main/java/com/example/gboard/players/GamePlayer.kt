package com.example.gboard.players

import android.graphics.Bitmap
import android.graphics.Canvas

interface GamePlayer {
	fun create(startX: Float, startY: Float)
	fun draw(canvas: Canvas)
	fun getX(): Float
	fun getY(): Float
	fun setCos(value: Float)
	fun setSin(value: Float)
	fun getCos(): Float
	fun getSin(): Float
	fun getHeight(): Float // max height of snake
	fun getMaxLength(): Int // max length of snake fragment
	fun getSize(): Int // number of snake fragments (lines array length)
	fun isRunning(): Boolean
	fun isDisappear(): Boolean
	fun setRunning(value: Boolean)
	fun isCollided(bitmap: Bitmap, color: Int): Boolean
	fun onMeasure(measuredWidth: Int, measuredHeight: Int)
}