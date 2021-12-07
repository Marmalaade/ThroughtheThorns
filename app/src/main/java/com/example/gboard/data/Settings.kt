package com.example.gboard.data

import android.content.Context
import android.content.SharedPreferences

class Settings {

	companion object {
		private var settings: SharedPreferences? = null

		private fun getSettingsPreferences(context: Context): SharedPreferences {
			if (settings == null) {
				settings = context.getSharedPreferences(Settings::class.java.name, Context.MODE_PRIVATE)
			}
			return settings!!
		}

		//private val Context.myDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

		//private val Context.dataStore by preferencesDataStore("settings")
		//private val SNAKE_COLOR = intPreferencesKey("snake_color")
		private val SOUND_KEY = "sound_enabled"
		private val COINS_KEY = "coins"
		private val PURCHASES_KEY = "purchases"
		var snakeColor: Int = 0
		var soundEnabled: Boolean = true
		var coins: Int = 0
		var levelPurchases = arrayOf(true, false, false)

		fun load(context: Context) {
			val prefs = getSettingsPreferences(context)
			coins = prefs.getInt(COINS_KEY, 0)
			soundEnabled = prefs.getBoolean(SOUND_KEY, true)
			for (i in levelPurchases.indices) {
				levelPurchases[i] = prefs.getBoolean("$PURCHASES_KEY$i", false)
			}
			levelPurchases[0] = true
			/*Log.e("Settings", "Load")
			val dataStore: DataStore<Preferences> = context.myDataStore
			val data = dataStore.data
			Log.e("Settings", "Load1")
			data.collect { preferences ->
				//snakeColor = preferences[SNAKE_COLOR] ?: 0
				soundEnabled = preferences[SOUND_KEY] ?: true
				coins = preferences[COINS_KEY] ?: 0
			}
			Log.e("Settings", "Loaded")*/
		}

		fun save(context: Context) {
			val prefs = getSettingsPreferences(context)
			val ed = prefs.edit()
			ed.clear()
			ed.putInt(COINS_KEY, coins)
			ed.putBoolean(SOUND_KEY, soundEnabled)
			for (i in levelPurchases.indices) {
				ed.putBoolean("$PURCHASES_KEY$i", levelPurchases[i])
			}
			ed.apply()
			/*val dataStore: DataStore<Preferences> = context.myDataStore
			dataStore.edit { preferences ->
				//preferences[SNAKE_COLOR] = snakeColor
				preferences[SOUND_KEY] = soundEnabled
				preferences[COINS_KEY] = coins
			}*/
		}
	}
}