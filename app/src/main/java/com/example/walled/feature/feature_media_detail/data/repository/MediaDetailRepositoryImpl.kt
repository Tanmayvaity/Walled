package com.example.walled.feature.feature_media_detail.data.repository

import com.example.walled.core.domain.model.Media
import com.example.walled.feature.feature_media_detail.domain.repository.MediaDetailRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class MediaDetailRepositoryImpl(
    private val client : HttpClient
): MediaDetailRepository {
    override suspend fun fetchMedia(id: String): Media {
        return client.get {
            url {
                path("photos/${id}")
            }
        }.body()
    }
}