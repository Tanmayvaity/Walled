package com.example.walled.feature.feature_feed.domain.repository


import com.example.walled.feature.feature_feed.domain.model.Media

interface FeedRepository {
    suspend fun  fetchMedia() : List<Media>
    suspend fun favouriteMedia()
    suspend fun unfavouriteMedia()
    suspend fun downloadMedia()
}