package com.newsta.android.utils.workers

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.newsta.android.NewstaApp
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.utils.prefrences.UserPrefrences
import java.lang.Exception

class DatabaseClearer
@WorkerInject
    constructor(
        @Assisted context: Context,
        @Assisted parameters: WorkerParameters,
        private val repository: StoriesRepository,
        private val prefrences: UserPrefrences
    ) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        try {
            val THREE_DAYS_OLD = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
            repository.deleteAllStories(THREE_DAYS_OLD)
            repository.deleteAllRecommendedStories(THREE_DAYS_OLD)
            NewstaApp.is_database_empty = true
            NewstaApp.setIsDatabaseEmpty(true)
            prefrences.isDatabaseEmpty(true)
            Log.i("WORK MANAGER", "Called")
            return Result.success()
        } catch (e: Exception) {
            Log.i("WORK MANAGER", "Exception")
            e.printStackTrace()
            prefrences.isDatabaseEmpty(false)
            return Result.failure()
        }

    }

}
