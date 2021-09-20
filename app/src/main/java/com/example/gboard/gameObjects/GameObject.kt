package com.example.gboard.gameObjects

import android.graphics.Bitmap
import android.graphics.Canvas

interface GameObject {
	fun draw(canvas: Canvas, relativeSize: Float)
	fun setPosition(relativeX: Float, relativeY: Float)
	fun getX1(): Float
	fun getY1(): Float
	fun getX2(): Float
	fun getY2(): Float
	fun getCenterX(): Float
	fun getCenterY(): Float
	fun getWidth(): Float
	fun getHeight(): Float
}