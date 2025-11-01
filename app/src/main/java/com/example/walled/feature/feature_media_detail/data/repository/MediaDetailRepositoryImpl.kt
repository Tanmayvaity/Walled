package com.example.walled.feature.feature_media_detail.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.walled.core.data.source.MediaManager
import com.example.walled.core.data.source.WallpaperHelper
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_media_detail.data.worker.MediaDownloadWorker
import com.example.walled.feature.feature_media_detail.domain.repository.MediaDetailRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class MediaDetailRepositoryImpl(
    private val client: HttpClient,
    private val context: Context,
    private val downloadManager: MediaManager,
    private val wallpaperHelper: WallpaperHelper
) : MediaDetailRepository {
    override suspend fun fetchMedia(id: String): Media {
        return client.get {
            url {
                path("photos/${id}")
            }
        }.body()
    }

    override suspend fun download(id: String,url:String) {
        Log.d("MediaDetailRepositoryImpl", "url : ${url}")

        val mediaDownloadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<MediaDownloadWorker>()
                .setInputData(workDataOf("PHOTO_ID" to id,"PHOTO_URL" to url))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        WorkManager
            .getInstance(context)
            .enqueue(mediaDownloadWorkRequest)
    }

    override suspend fun downloadToInternalCache(url: String): Result<Uri> {
        return downloadManager.downloadFileToCache(url,{}, block = {url -> client.get(url)})
    }

    override suspend fun applyWallpaper(uri: Uri) : Result<String> {
        return wallpaperHelper.setWallpaper(uri)
    }


}