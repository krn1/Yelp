package com.yelp.app

import dagger.Module
import dagger.Provides

@Module
internal class ApplicationModule(application: YelpApp) {
    private val application: YelpApp

    @Provides
    fun provideApplication(): YelpApp {
        return application
    }

    init {
        this.application = application
    }
}
