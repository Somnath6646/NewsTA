package com.newsta.android

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.work.*
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.newsta.android.utils.prefrences.UserPrefrences
import com.newsta.android.utils.workers.DatabaseClearer
import dagger.hilt.android.HiltAndroidApp
import java.lang.invoke.ConstantCallSite
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class NewstaApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
        setWorks()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setWorks() {

        val constraints = Constraints.Builder()
            .setRequiresDeviceIdle(true)
            .build()

        val periodicDatabaseClearRequest = PeriodicWorkRequestBuilder<DatabaseClearer>(3, TimeUnit.DAYS)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(periodicDatabaseClearRequest)

        println("METHOD CALLED FOR WORKER")

        workManager.getWorkInfoByIdLiveData(periodicDatabaseClearRequest.id)
            .observeForever { info ->
                if(info.state.isFinished) {
                    Toast.makeText(applicationContext, "Work ${info.state.name}", Toast.LENGTH_SHORT).show()
                    is_database_empty = true
                    setIsDatabaseEmpty(true)
                } else {
                    Toast.makeText(applicationContext, "Work ${info.state.name}", Toast.LENGTH_SHORT).show()
                    is_database_empty = true
                    setIsDatabaseEmpty(true)
                }
            }

    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}
