package com.example.gboard.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.collect

class Settings {

	companion object {
		private val Context.dataStore by preferencesDataStore("settings")
		private val SNAKE_COLOR = intPreferencesKey("snake_color")
		private val SOUND_KEY = booleanPreferencesKey("sound_enabled")
		var snakeColor: Int = 0
		var soundEnabled: Boolean = true

		suspend fun load(context: Context) {
			val dataStore: DataStore<Preferences> = context.dataStore
			dataStore.data.collect { preferences ->
				snakeColor = preferences[SNAKE_COLOR] ?: 0
				soundEnabled = preferences[SOUND_KEY] ?: true
			}
		}

		suspend fun save(context: Context) {
			val dataStore: DataStore<Preferences> = context.dataStore
			dataStore.edit { preferences ->
				preferences[SNAKE_COLOR] = snakeColor
				preferences[SOUND_KEY] = soundEnabled
			}
		}
	}
}