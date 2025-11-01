package com.example.walled.feature.feature_media_detail.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.walled.core.data.source.MediaManager
import com.example.walled.core.data.source.NotificationService
import com.example.walled.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.path
import io.ktor.util.network.UnresolvedAddressException

/**
 * A [CoroutineWorker] responsible for downloading a media file from a given URL in the background.
 *
 * This worker handles the entire download process, including:
 * 1.  Notifying a remote server to increment the download count for the media.
 * 2.  Using a [MediaManager] to download the actual image file.
 * 3.  Displaying and updating a foreground notification to show the download progress.
 * 4.  Displaying a final notification upon successful download completion, allowing the user to view the file.
 *
 * The worker is designed to be robust and run as a foreground service to ensure the download is not
 * interrupted by the system.
 *
 * @param appContext The application context.
 * @param workerParams Parameters to setup the worker, including input data.
 * @param client An [HttpClient] for making network requests, specifically to trigger the download count API.
 * @param notificationService A service for creating and managing notifications.
 * @param downloadManager A manager class that handles the low-level logic of downloading and saving the file.
 */
class MediaDownloadWorker(
    appContext: Context,
    val workerParams: WorkerParameters,
    private val client: HttpClient,
    private val notificationService: NotificationService,
    private val downloadManager: MediaManager
) : CoroutineWorker(appContext, workerParams) {
    private val logger: Logger = Logger("MediaDownloadWorker")
    private val notificationId = id.hashCode() xor System.currentTimeMillis().toInt()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return notificationService.createDownloadProgressNotification(
            workerParams.id,
            notificationId,
            0
        )
    }


    override suspend fun doWork(): Result {
        try {
            val id = inputData.getString("PHOTO_ID") ?: return Result.failure()
            val url = inputData.getString("PHOTO_URL") ?: return Result.failure()

            logger.debug("Photo Url : ${url}")

            setForeground(
                notificationService.createDownloadProgressNotification(
                    workerParams.id,
                    notificationId,
                    0
                )
            )
            // download count increment
            val downloadResponse = client.get {
                url {
                    path("photos/${id}/download")
                }
            }

            logger.info("download count api request status code : ${downloadResponse.status}")

            if (downloadResponse.status != HttpStatusCode.OK) {
                return Result.failure()
            }


            val itemUri = downloadManager.downloadRemoteImage(
                url = url,
                onProgress = { progress, size ->
                    setForeground(
                        notificationService.createDownloadProgressNotification(
                            workerParams.id,
                            notificationId,
                            progress,
                            size
                        )
                    )
                },
                block = { url -> client.get(url) }

            )
//


            notificationService.showCompletedNotification(
                id.hashCode() xor System.currentTimeMillis().toInt(),
                itemUri
            )



            logger.debug("work done")
            return Result.success()

        } catch (e: UnresolvedAddressException) {
            logger.error("Work Failed $e")
            e.printStackTrace()
            notificationService.showErrorNotification(
                id.hashCode() xor System.currentTimeMillis().toInt(), "Internet is turned off"
            )
            return Result.failure()
        } catch (e: Exception) {
            logger.error("Work Failed $e")
            e.printStackTrace()
            notificationService.showErrorNotification(
                id.hashCode() xor System.currentTimeMillis().toInt(), e.message.toString()
            )
            return Result.failure()
        }

    }

}