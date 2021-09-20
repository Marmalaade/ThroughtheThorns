package com.example.gboard

import com.example.gboard.gameObjects.Direct
import com.example.gboard.gameObjects.GameObject

class Levels {
	fun getLevel(level: Int): Array<GameObject>? {
		when (level) {
			0 -> {
				return arrayOf(
					Direct(10f, 5f, 0),
					Direct(10f, 5f, 45),
					Direct(10f, 5f, 0),
					Direct(10f, 5f, -45),
					Direct(10f, 5f, 0),
					Direct(10f, 5f, 45),
					Direct(10f, 5f, 0),
					Direct(10f, 5f, -45),
					Direct(10f, 5f, 0),
					Direct(10f, 5f, 45),
					Direct(10f, 5f, 0),
					Direct(10f, 5f, -45),
					Direct(10f, 5f, 0)
				)

			}
		}
		return null
	}
}