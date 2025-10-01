package com.example.walled.di

import com.example.walled.feature.feature_feed.data.source.KtorClient
import com.example.walled.feature.feature_feed.data.repository.OnlineFeedRepositoryImpl
import com.example.walled.feature.feature_feed.domain.repository.FeedRepository
import com.example.walled.feature.feature_feed.domain.usecase.FeedUseCase
import com.example.walled.feature.feature_feed.domain.usecase.GetRemoteImagesUseCase
import com.example.walled.feature.feature_feed.presentation.online.OnlineViewModel

import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    
    single<HttpClient>{
        KtorClient.getClient()
    }
    single<FeedRepository>(named("online")){
        OnlineFeedRepositoryImpl(get())
    }
    single<FeedUseCase> {
        FeedUseCase(
            getRemoteImagesUseCase = GetRemoteImagesUseCase(get(named("online")))
        )
    }
    viewModel {
        OnlineViewModel(get())
    }






}