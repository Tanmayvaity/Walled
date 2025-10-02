package com.example.walled.feature.feature_feed.data.repository

import com.example.walled.core.domain.model.Media
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class OnlineFeedRepositoryImpl(
    val client : HttpClient
) : FeedRepository {
    override suspend fun fetchMedia(): List<Media> {
        return client.get {
            url{
                path("photos")
            }
        }.body()
    }
    override suspend fun downloadMedia() {

    }


}