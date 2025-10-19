package com.example.walled.feature.feature_feed.data.repository


import com.example.walled.core.domain.model.Media
import com.example.walled.core.domain.model.Result
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import com.example.walled.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class OnlineFeedRepositoryImpl(
    val client: HttpClient
) : FeedRepository {
    private val logger = Logger("OnlineFeedRepositoryImpl")
    override suspend fun fetchMedia(): Result<List<Media>> {
        return try {
            val response = client.get {
                url {
                    path("photos")
                }
            }.body<List<Media>>()
            Result.Success(response)
        }catch (e : Exception){
            logger.error(e.message.toString())
            e.printStackTrace()
            Result.Error(e)
        }

    }

    override suspend fun downloadMedia() {

    }


}