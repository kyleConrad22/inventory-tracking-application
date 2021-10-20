package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReceptionUploadWorker(context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        //val receptionData = inputData.getString()
        //uploadReception()

        return Result.success()
    }
}