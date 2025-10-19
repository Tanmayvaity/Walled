package com.example.walled.feature.feature_media_detail.domain.usecase

data class MediaDetailUseCase(
    val getMediaByIdUseCase: GetMediaByIdUseCase,
    val downloadMediaUseCase: DownloadMediaUseCase,
    val downloadToInternalCacheUseCase: DownloadToInternalCacheUseCase,
    val applyWallpaperUseCase: ApplyWallpaperUseCase
)