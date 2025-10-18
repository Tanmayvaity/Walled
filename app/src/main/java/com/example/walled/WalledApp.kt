package com.example.walled

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.example.walled.di.appModule
import com.example.walled.feature.feature_media_detail.data.worker.DownloadWorkerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

const val DOWNLOAD_CHANNEL_ID = "downloads_channel"
const val DOWNLOAD_CHANNEL_NAME = "File Downloads"
const val DOWNLOAD_CHANNEL_DESC = "Notifications for image download progress and completion"

class WalledApp : Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                DelegatingWorkerFactory().apply {
                    addFactory(KoinWorkerFactory())           // from Koin
                    addFactory(DownloadWorkerFactory())       // your custom one
                }
            )
            .build()
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WalledApp)
            modules(appModule)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DOWNLOAD_CHANNEL_ID,DOWNLOAD_CHANNEL_NAME,importance)
            channel.description = DOWNLOAD_CHANNEL_DESC
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}