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



    val appInstalledJustNow: Flow<Boolean?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[APP_INSTALLED_JUST_NOW]
        }

    suspend fun appInstalledJustNow(appInstalledJustNow: Boolean) {

        applicationContext.dataStore.edit { settings ->
            settings[APP_INSTALLED_JUST_NOW] = appInstalledJustNow
        }
    }

    val hasChangedPreferences: Flow<Boolean?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[HAS_CHANGED_PREFERENCES]
        }

    suspend fun hasChangedPreferences(hasChangedPreferences: Boolean) {

        applicationContext.dataStore.edit { settings ->
            settings[HAS_CHANGED_PREFERENCES] = hasChangedPreferences
        }
    }

    val shownSwipeLeftDialog: Flow<Boolean?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[SHOWN_SWIPE_LEFT_DIALOG]
        }

    suspend fun shownSwipeLeftDialog(shownSwipeLeftDialog: Boolean) {

        applicationContext.dataStore.edit { settings ->
            settings[SHOWN_SWIPE_LEFT_DIALOG] = shownSwipeLeftDialog
        }
    }
    suspend fun setIsDarkMode(isDarkMode: Boolean) {

        applicationContext.dataStore.edit { settings ->
            settings[IS_DARK_MODE] = isDarkMode
        }
    }


    val isDatabaseEmpty: Flow<Boolean?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[IS_DATABASE_EMPTY]
        }
    val isDarkMode: Flow<Boolean?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE]
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
        val APP_INSTALLED_JUST_NOW = booleanPreferencesKey("app_installed_now")
        val HAS_CHANGED_PREFERENCES = booleanPreferencesKey("has_changed_preferences")

        val SHOWN_SWIPE_LEFT_DIALOG = booleanPreferencesKey("showswipeleftdialog")

        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

}
