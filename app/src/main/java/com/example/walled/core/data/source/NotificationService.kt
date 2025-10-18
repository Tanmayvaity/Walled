package com.example.walled.core.data.source

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.example.walled.DOWNLOAD_CHANNEL_ID
import com.example.walled.R
import java.util.UUID

class NotificationService(val context: Context) {

    private val notificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }


    @SuppressLint("MissingPermission")
    fun createDownloadProgressNotification(
        workId: UUID,
        notificationId: Int,
        progress: Int,
        totalSize: Float = 0f
    ): ForegroundInfo {
        val cancelIntent = WorkManager.getInstance(context).createCancelPendingIntent(workId)

        val builder =
            getDownloadNotificationBuilder(
                title = "Walled",
                text = if (progress < 100) "Downloaded $progress%" else "Download complete",
                onGoing = true,
                autoCancel = false,
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(100, progress, false)
            .addAction(R.drawable.ic_favourite, "cancel", cancelIntent)
            .build()

        return ForegroundInfo(notificationId, builder, FOREGROUND_SERVICE_TYPE_DATA_SYNC)

    }

    fun showCompletedNotification(
        notificationId: Int,
        itemUri: Uri?
    ) {
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(itemUri, "image/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completedNotification =
            getDownloadNotificationBuilder(
                "Download Complete",
                "Image downloaded successfully",
                onGoing = false,
                autoCancel = true
            )
                .setContentIntent(pendingIntent)
                .build()
        notificationManager.notify(notificationId, completedNotification)
    }

    fun showErrorNotification(
        notificationId: Int,
        error: String
    ) {
        val completedNotification = getDownloadNotificationBuilder(
            "Download Failed",
            error,
            onGoing = false,
            autoCancel = true
        ).build()
        notificationManager.notify(notificationId, completedNotification)
    }

    private fun getDownloadNotificationBuilder(
        title: String,
        text: String,
        icon: Int = R.drawable.ic_launcher_foreground,
        onGoing: Boolean,
        autoCancel: Boolean,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(icon)
            .setOngoing(onGoing)
            .setAutoCancel(autoCancel)

    }
}