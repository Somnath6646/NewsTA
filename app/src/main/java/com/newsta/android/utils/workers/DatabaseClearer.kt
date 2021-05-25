package com.newsta.android.utils.workers

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.newsta.android.NewstaApp
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.utils.helpers.Indicator
import java.lang.Exception

class DatabaseClearer
    constructor(
         context: Context,
         parameters: WorkerParameters
    ) : Worker(context, parameters) {

    override fun doWork(): Result {
        return try {


            Result.success()
        } catch (e: Exception) {
            Log.i("WORK MANAGER", "Exception")
            e.printStackTrace()
            Result.failure()
        }

    }

}
