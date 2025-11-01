package com.example.walled.feature.feature_feed.domain.usecase

import android.util.Log
import com.example.walled.core.domain.model.Image
import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import org.koin.core.annotation.Named

class GetLocalImagesUseCase(
    @Named("online") private val repository : FeedRepository
) {
    suspend operator fun invoke(): Result<List<Image>>{
        Log.d("GetLocalImagesUseCase", "invoke: called")
        return repository.fetchMedia(Image::class)
    }
}