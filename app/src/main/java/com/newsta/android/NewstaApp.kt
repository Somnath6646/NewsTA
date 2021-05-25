package com.newsta.android

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.newsta.android.utils.helpers.Indicator
import com.newsta.android.utils.prefrences.UserPrefrences
import com.newsta.android.utils.workers.DatabaseClearer
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject



@HiltAndroidApp
class NewstaApp : Application() {



    companion object {

        const val ISSUER_NEWSTA = "newsta"

        const val DEFAULT_FONT_SCALE = 1.05f
        const val LARGE_FONT_SCALE = 1.15f
        const val SMALL_FONT_SCALE = 0.95f

        const val DEFAULT_FONT_NAME = "Default"
        const val LARGE_FONT_NAME = "Large"
        const val SMALL_FONT_NAME = "Small"

        lateinit var prefrences: UserPrefrences
        var access_token: String? = null

        fun getAccessToken(): String? = access_token
        fun setAccessToken(accessToken: String?) {
            this.access_token = accessToken
        }

        var liveData_isDataBaseMadeEmpty: MutableLiveData<Indicator<Boolean?>> = MutableLiveData(Indicator(true))

        var is_database_empty: Boolean = false

        fun getIsDatabaseEmpty(): Boolean = is_database_empty

        fun setIsDatabaseEmpty(isDatabaseEmpty: Boolean) {
            this.is_database_empty = isDatabaseEmpty
        }

        var font_scale: Float? = DEFAULT_FONT_SCALE

        fun getFontScale(): Float? = font_scale
        fun setFontScale(fontScale: Float) {
            this.font_scale = fontScale
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


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }




}
