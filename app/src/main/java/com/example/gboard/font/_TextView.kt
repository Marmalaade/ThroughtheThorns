package com.example.gboard.font

import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import com.example.gboard.R

const val fontPath = "fonts/app-font.ttf"
lateinit var appFont: Typeface

fun TextView?.setAppFont() {
	if (this == null) return
	else {
		if (!::appFont.isInitialized) {
			appFont = Typeface.createFromAsset(context.assets, fontPath)
		}
		typeface = appFont
	}
}

fun TextView?.fontApply(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
	if (this == null) return
	if (attributeSet != null) {
		context.obtainStyledAttributes(attributeSet, R.styleable.FontTextView, defStyleAttr, defStyleRes).apply {
			setTypefaceFromAsset(getString(R.styleable.FontTextView_fontAssets))
		}.recycle()
	}
}

fun TextView?.setTypefaceFromAsset(fontPath: String?) {
	if (this == null) return
	if (fontPath == null) typeface = null
	else
		try {
			typeface = Typeface.createFromAsset(context.assets, fontPath)
		} catch (ex: Exception) {
			Log.e("FontTextView", ex.message.toString())
		}
}