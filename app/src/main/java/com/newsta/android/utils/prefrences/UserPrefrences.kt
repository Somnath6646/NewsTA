package com.newsta.android.utils.prefrences


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPrefrences (
        context: Context
){


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





    companion object{
        val ACCESS_TOKEN = stringPreferencesKey("access token")
    }
}