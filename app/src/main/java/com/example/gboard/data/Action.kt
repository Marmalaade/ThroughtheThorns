package com.example.gboard.data

interface Action<T> {
	fun run(result: T)
}