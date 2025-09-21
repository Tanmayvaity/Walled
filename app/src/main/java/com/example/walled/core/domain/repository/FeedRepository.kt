package com.example.walled.core.domain.repository

import com.example.walled.core.domain.model.Media

interface FeedRepository {
    fun fetchMedia() : List<Media>
    fun favouriteMedia()
    fun unfavouriteMedia()
    fun downloadMedia()
}