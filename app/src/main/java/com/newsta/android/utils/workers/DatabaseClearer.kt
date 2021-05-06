package com.newsta.android.utils.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.newsta.android.data.local.StoriesDAO

class DatabaseClearer
@WorkerInject
    constructor(
        @Assisted context: Context,
        @Assisted parameters: WorkerParameters,
        private val dao: StoriesDAO
    ) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        dao.deleteAllStories()
        return Result.success()
    }

}
