package com.newsta.android

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.newsta.android.utils.prefrences.UserPrefrences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewstaApp : Application() {


    companion object {

        const val ISSUER_NEWSTA = "newsta"

        lateinit var prefrences: UserPrefrences
        var access_token: String? = null

        fun getAccessToken(): String? = access_token
        fun setAccessToken(accessToken: String) {
            this.access_token = accessToken
        }

        var is_database_empty: Boolean? = true

        fun getIsDatabaseEmpty(): Boolean? = is_database_empty
        fun setIsDatabaseEmpty(isDatabaseEmpty: Boolean) {
            this.is_database_empty = isDatabaseEmpty
        }

    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }
}
