package com.yelp.main

import com.yelp.app.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
internal class BusinessListModule(view: BusinessListContract.View) {
    private val view: BusinessListContract.View

    @PerActivity
    @Provides
    fun providesView(): BusinessListContract.View {
        return view
    }

    init {
        this.view = view
    }
}
