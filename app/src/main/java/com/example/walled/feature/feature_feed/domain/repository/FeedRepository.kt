package com.example.walled.feature.feature_feed.domain.repository


import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result


interface FeedRepository{
    suspend fun  fetchMedia() : Result<List<Media>>
    suspend fun downloadMedia()


}