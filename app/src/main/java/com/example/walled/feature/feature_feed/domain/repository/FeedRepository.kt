package com.example.walled.feature.feature_feed.domain.repository


import com.example.walled.core.domain.model.Media



interface FeedRepository{
    suspend fun  fetchMedia() : List<Media>
    suspend fun downloadMedia()


}