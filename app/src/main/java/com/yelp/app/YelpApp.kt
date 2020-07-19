package com.yelp.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import timber.log.Timber

class YelpApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // dependency injection graph
        getComponent().inject(this)

        Timber.plant(Timber.DebugTree())
        initializeFresco()
    }

    fun getComponent(): ApplicationComponent {
        val component: ApplicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()

        return component
    }

    // region private
    private fun initializeFresco() {
        val config = ImagePipelineConfig.newBuilder(this)
            .setDownsampleEnabled(true)
            .build()
        Fresco.initialize(this, config)
    } //endregion
}
