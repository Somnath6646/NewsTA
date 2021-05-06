package com.newsta.android.utils.workers

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.newsta.android.data.local.StoriesDAO
import java.lang.Exception

class DatabaseClearer
@WorkerInject
    constructor(
        @Assisted context: Context,
        @Assisted parameters: WorkerParameters,
        private val dao: StoriesDAO
    ) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        try {
            dao.deleteAllStories()
            Log.i("WORK MANAGER", "Called")
            return Result.success()
        } catch (e: Exception) {
            Log.i("WORK MANAGER", "Exception")
            e.printStackTrace()
            return Result.success()
        }

    }

}
