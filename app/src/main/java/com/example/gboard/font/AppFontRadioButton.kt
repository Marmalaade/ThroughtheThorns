package com.example.gboard.font

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import com.example.gboard.font.setAppFont
import com.example.gboard.font.setTypefaceFromAsset

open class AppFontRadioButton : RadioButton {
	constructor(context: Context) : super(context)
	constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
	constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)
	constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

	init {
		setAppFont()
	}
}