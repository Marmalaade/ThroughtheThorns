package com.example.gboard

class FrameTimer {
	private var previousTime = -1L

	fun reset() {
		previousTime = -1L
	}

	fun getDeltaTime(): Double {
		if (previousTime == -1L) previousTime = System.nanoTime()
		val currentTime = System.nanoTime()
		val delta = (currentTime - previousTime) / 1000000.0
		previousTime = currentTime
		return delta
	}
}