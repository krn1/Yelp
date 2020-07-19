package com.yelp.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.R
import com.yelp.app.YelpApp
import com.yelp.model.BusinessData
import javax.inject.Inject


class BusinessListActivity : AppCompatActivity(), BusinessListContract.View {

    @Inject
    lateinit var presenter: BusinessListPresenter

    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        getComponent().inject(this)
    }

    override fun onStart() {
        super.onStart()
        fetchFirstPage()
    }

//    override fun onStop() {
//        super.onStop()
//        presenter.stop()
//    }

    // endregion

    // region private

    private fun fetchFirstPage() {
        presenter.loadFirstPage()
    }

    private fun getComponent(): BusinessListComponent {
        return DaggerBusinessListComponent.builder()
            .applicationComponent((application as YelpApp).getComponent())
            .businessListModule(BusinessListModule(this))
            .build()
    }

    // endregion

    // region View
    override fun showSpinner() {

    }

    override fun hideSpinner() {

    }

    override fun showBusiness(business: List<BusinessData>) {
        TODO("Not yet implemented")
    }

    override fun refresh(restaurants: List<BusinessData>) {
        TODO("Not yet implemented")
    }

    override val isRefreshing: Boolean
        get() = TODO("Not yet implemented")

    override fun showError(message: String?) {
        TODO("Not yet implemented")
    }

    // endregion

}