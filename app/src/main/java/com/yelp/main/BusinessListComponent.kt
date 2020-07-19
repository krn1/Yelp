package com.yelp.main

import com.yelp.app.ApplicationComponent
import com.yelp.app.scope.PerActivity
import com.yelp.fusion.client.connection.YelpFusionApi
import com.yelp.repository.NetworkModule
import dagger.Component

@PerActivity
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [BusinessListModule::class]
)
interface BusinessListComponent {
    fun inject(businessListActivity: BusinessListActivity)
    //  fun yelpApi(): YelpFusionApi
}
