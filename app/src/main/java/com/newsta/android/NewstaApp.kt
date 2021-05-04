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

        fun setTime(updatedAt: Long): String {

            val time = System.currentTimeMillis()

            val diff = time - updatedAt

            val seconds: Long = diff / 1000

            val minutes: Int = (seconds / 60).toInt()

            val hours: Int = minutes / 60
            val days: Int = hours / 24
            val months: Int = days / 30
            val years: Int = months / 12

            if (minutes <= 30) {
                return "Few minutes ago"
            }
            else if (minutes > 30 && minutes < 60) {
                return "Less than an hour ago"
            } else {
                if (hours == 1)
                    return "1 hour ago"
                else if (hours > 1 && hours < 24) {
                    return "$hours hours ago"
                } else {
                    if (days == 1)
                        return "1 day ago"
                    else if (days > 1 && days < 30) {
                        return "$days days ago"
                    } else {
                        if (months >= 1 && months < 12) {
                            return "$months months ago"
                        } else {
                            if (years == 1)
                                return "An year ago"
                            else if (years > 1)
                                return "$years years ago"
                            else
                                return "Time unknown"
                        }
                    }
                }
            }

        }

    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }

}
