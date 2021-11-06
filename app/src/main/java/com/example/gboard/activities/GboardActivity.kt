package com.example.gboard.activities

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread

abstract class GboardActivity : Activity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		currentActivity = this
	}

	override fun onResume() {
		super.onResume()
		currentActivity = this
	}

	companion object {
		var currentActivity: GboardActivity? = null

		@JvmStatic
		val instance: GboardActivity
			get() = currentActivity!!

		inline fun <reified T : GboardActivity> getInstanceType(): T? {
			if (currentActivity != null && currentActivity is T) {
				return currentActivity as T
			}
			return null
		}

		private var handlerField: Handler? = null
		val handler: Handler
			get() {
				if (handlerField == null) {
					if (handlerThread == null) {
						handlerThread = HandlerThread("ImageLoader")
						handlerThread!!.start()
					}
					handlerField = Handler(handlerThread!!.looper)
				}
				return handlerField!!
			}
		private var handlerThread: HandlerThread? = null
	}
}