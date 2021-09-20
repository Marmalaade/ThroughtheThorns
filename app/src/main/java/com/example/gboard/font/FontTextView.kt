package com.example.gboard.font

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

open class FontTextView : TextView {

	constructor(context: Context) : super(context)
	constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
		fontApply(attributeSet, 0, 0)
	}

	constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
		fontApply(attributeSet, defStyleAttr, 0)
	}

	constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes) {
		fontApply(attributeSet, defStyleAttr, defStyleRes)
	}

}