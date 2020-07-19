package com.yelp.repository

import com.yelp.fusion.client.connection.YelpFusionApi
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesApiService(): YelpFusionApi {

        val yelpFusionApi =
            YelpFusionApiFactory().createAPI("y71yZBsubF0HQh8mwL2-09ojQVq_ikpyf7kM8YbVeTJHI9ZnegD8mjQKppxy1YOFZae1M2hw_wgMWzS7-tx8WdBHFeyS2pvYQWQumphXoZi_c-bcO5m0MDszvVoTX3Yx")

        return yelpFusionApi
    }
}
