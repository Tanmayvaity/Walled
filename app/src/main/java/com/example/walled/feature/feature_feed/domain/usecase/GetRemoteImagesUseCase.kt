package com.example.walled.feature.feature_feed.domain.usecase

import com.example.walled.core.domain.model.Media
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import org.koin.core.annotation.Named

class GetRemoteImagesUseCase(
    @Named("online") private val repository : FeedRepository
) {
    suspend operator fun invoke(): List<Media>{
        return repository.fetchMedia()
    }
}