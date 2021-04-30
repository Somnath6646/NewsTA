package com.newsta.android

import android.app.Application
import com.facebook.AccessToken
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

        var max_story_id: Int? = 0

        fun getMaxStoryId(): Int? = max_story_id
        fun setMaxStoryId(maxStoryId: Int) {
            this.max_story_id = maxStoryId
        }

    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }
}
