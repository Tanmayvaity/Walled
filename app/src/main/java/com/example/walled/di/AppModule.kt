package com.example.walled.di

import com.example.walled.core.data.source.DownloadManager
import com.example.walled.core.data.source.KtorClient
import com.example.walled.core.data.source.NotificationService
import com.example.walled.core.data.source.WallpaperHelper
import com.example.walled.feature.feature_feed.data.repository.OnlineFeedRepositoryImpl
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import com.example.walled.feature.feature_feed.domain.usecase.FeedUseCase
import com.example.walled.feature.feature_feed.domain.usecase.GetRemoteImagesUseCase
import com.example.walled.feature.feature_feed.presentation.online.OnlineViewModel
import com.example.walled.feature.feature_media_detail.data.repository.MediaDetailRepositoryImpl
import com.example.walled.feature.feature_media_detail.data.worker.MediaDownloadWorker
import com.example.walled.feature.feature_media_detail.domain.repository.MediaDetailRepository
import com.example.walled.feature.feature_media_detail.domain.usecase.ApplyWallpaperUseCase
import com.example.walled.feature.feature_media_detail.domain.usecase.DownloadMediaUseCase
import com.example.walled.feature.feature_media_detail.domain.usecase.DownloadToInternalCacheUseCase
import com.example.walled.feature.feature_media_detail.domain.usecase.GetMediaByIdUseCase
import com.example.walled.feature.feature_media_detail.domain.usecase.MediaDetailUseCase
import com.example.walled.feature.feature_media_detail.presentation.detail.MediaDetailViewModel

import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    
    single<HttpClient>{
        KtorClient.getClient()
    }
    single<FeedRepository>(named("online")){
        OnlineFeedRepositoryImpl(get())
    }
    single<FeedUseCase> {
        FeedUseCase(
            getRemoteImagesUseCase = GetRemoteImagesUseCase(get(named("online")))
        )
    }
    viewModel {
        OnlineViewModel(get())
    }

    single<MediaDetailRepository>{
        MediaDetailRepositoryImpl(get(),androidContext(),get(),get())
    }

    single<MediaDetailUseCase>{
        MediaDetailUseCase(
            getMediaByIdUseCase = GetMediaByIdUseCase(get()),
            downloadMediaUseCase = DownloadMediaUseCase(get()),
            downloadToInternalCacheUseCase = DownloadToInternalCacheUseCase(get()),
            applyWallpaperUseCase = ApplyWallpaperUseCase(get())
        )
    }

    viewModel {
        MediaDetailViewModel(get())
    }

    single {
        NotificationService(androidContext())
    }
    single {
        DownloadManager(
            androidContext(),
            get()
        )
    }

    single{
        WallpaperHelper(androidContext())
    }









}