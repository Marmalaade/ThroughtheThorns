package com.example.gboard.data

open class OnChanger<T>(value: T) {
	var value: T = value
		set(value) {
			field = value
			onChange.run(field)
		}

	val onChange = OnChangerHashSetAction<T>()

	override fun equals(other: Any?): Boolean = super.equals(other) || (value?.equals(other) ?: false)
}