package com.example.walled.feature.feature_feed.domain.usecase

data class FeedUseCase(
    val getRemoteImagesUseCase: GetRemoteImagesUseCase,
    val getLocalImagesUseCase: GetLocalImagesUseCase
)