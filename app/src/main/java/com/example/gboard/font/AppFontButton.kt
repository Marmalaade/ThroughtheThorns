package com.example.gboard.font

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import com.example.gboard.font.setAppFont
import com.example.gboard.font.setTypefaceFromAsset

open class AppFontButton : Button {
	constructor(context: Context) : super(context)
	constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
	constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)
	constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

	init {
		setAppFont()
	}
}