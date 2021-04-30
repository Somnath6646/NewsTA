package com.newsta.android.utils.prefrences


import android.content.Context
import androidx.datastore.core.DataStore
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
    
    val maxStoryId: Flow<Int?> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[MAX_STORY_ID]
        }

    suspend fun maxStoryId(maxStoryId: Int) {
        applicationContext.dataStore.edit { settings ->
            settings[MAX_STORY_ID] = maxStoryId
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access token")
        val MAX_STORY_ID = intPreferencesKey("max_story_id")
    }

}