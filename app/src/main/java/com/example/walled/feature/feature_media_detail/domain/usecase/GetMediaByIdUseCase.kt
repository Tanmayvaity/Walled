package com.example.walled.feature.feature_media_detail.domain.usecase

import com.example.walled.core.domain.model.Media
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import com.example.walled.feature.feature_media_detail.domain.repository.MediaDetailRepository
import org.koin.core.annotation.Named

class GetMediaByIdUseCase(
    private val repository : MediaDetailRepository
) {
    suspend operator fun invoke(id:String): Media{
        return repository.fetchMedia(id)
    }
}