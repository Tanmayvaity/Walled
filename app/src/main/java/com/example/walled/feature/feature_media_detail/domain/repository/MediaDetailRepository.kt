package com.example.walled.feature.feature_media_detail.domain.repository


import com.example.walled.core.domain.model.Media

interface MediaDetailRepository {
    suspend fun fetchMedia(id : String) : Media
}