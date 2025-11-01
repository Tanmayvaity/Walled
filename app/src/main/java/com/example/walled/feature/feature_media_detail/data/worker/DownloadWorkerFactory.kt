package com.example.walled.feature.feature_media_detail.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.walled.core.data.source.MediaManager
import com.example.walled.core.data.source.NotificationService
import io.ktor.client.HttpClient
import org.koin.java.KoinJavaComponent.getKoin


class DownloadWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val koin = getKoin()
        return when(workerClassName){
            MediaDownloadWorker::class.qualifiedName -> {
                val client = koin.get<HttpClient>()
                val notificationService = koin.get<NotificationService>()
                val downloadManager = koin.get<MediaManager>()
                MediaDownloadWorker(appContext,workerParameters,client,notificationService,downloadManager)
            }
            else -> null
        }
    }

}