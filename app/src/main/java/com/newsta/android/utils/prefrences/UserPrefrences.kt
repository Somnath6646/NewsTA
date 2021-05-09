package com.newsta.android.utils.prefrences


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPrefrences(context: Context) {

    private val applicationContext = context.applicationContext

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val accessToken: Flow<String?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    suspend fun saveAccessToken(accessToken: String) {
        applicationContext.dataStore.edit { settings ->
            settings[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun clearData(){
        applicationContext.dataStore.edit {
            it.clear()
        }
    }



    val isDatabaseEmpty: Flow<Boolean?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[IS_DATABASE_EMPTY]
        }

    suspend fun isDatabaseEmpty(isDatabaseEmpty: Boolean) {

        applicationContext.dataStore.edit { settings ->
            settings[IS_DATABASE_EMPTY] = isDatabaseEmpty
        }
    }

    val fontScale: Flow<Float?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[FONT_SCALE]
        }

    suspend fun setFontScale(fontScale: Float) {
        applicationContext.dataStore.edit { settings ->
            settings[FONT_SCALE] = fontScale
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access token")
        val ISS = stringPreferencesKey("iss")
        val IS_DATABASE_EMPTY = booleanPreferencesKey("is_database_empty")
        val FONT_SCALE = floatPreferencesKey("font_scale")
    }

}
