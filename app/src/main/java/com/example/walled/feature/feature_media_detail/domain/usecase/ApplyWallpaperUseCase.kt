package com.example.walled.feature.feature_media_detail.domain.usecase

import android.net.Uri
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_media_detail.domain.repository.MediaDetailRepository

class ApplyWallpaperUseCase(
    private val repository : MediaDetailRepository
) {
    suspend operator fun invoke(uri : Uri) : Result<String>{
        return repository.applyWallpaper(uri)
    }
}