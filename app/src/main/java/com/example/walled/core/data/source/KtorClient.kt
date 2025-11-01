package com.example.walled.core.data.source


import com.example.walled.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    private val TAG = "KtorClient"
    fun getClient() : HttpClient = HttpClient(CIO){
        install(Logging){
            logger = Logger.ANDROID
            level = LogLevel.BODY
        }
        install(ContentNegotiation){
            json(json = Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout){
            socketTimeoutMillis = 3000
            connectTimeoutMillis = 3000
            requestTimeoutMillis = 3000
        }
        install(DefaultRequest){
            url{
                protocol = URLProtocol.HTTPS
                host = "api.unsplash.com"
                header(HttpHeaders.Authorization,"Client-ID ${BuildConfig.apiKey}")
            }
        }

    }
}

