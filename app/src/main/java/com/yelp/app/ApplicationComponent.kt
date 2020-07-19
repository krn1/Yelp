package com.yelp.app

import com.yelp.fusion.client.connection.YelpFusionApi
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.repository.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, NetworkModule::class])
interface ApplicationComponent {
    fun inject(application: YelpApp)
    fun application(): YelpApp
    fun yelpApi(): YelpFusionApi
}
