package com.example.gboard.data

open class OnChangerHashSetAction<T> : HashSet<Action<T>>(), Action<T> {
	override fun run(result: T) {
		val iterator = iterator()
		while (iterator.hasNext()) {
			try {
				val item = iterator.next()
				item.run(result)
			} catch (e: java.util.ConcurrentModificationException) {
				clear()
				break
			}
		}
	}
}