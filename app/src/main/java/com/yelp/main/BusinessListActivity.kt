package com.yelp.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yelp.R
import com.yelp.app.YelpApp
import com.yelp.app.utils.AlertUtil
import com.yelp.model.BusinessData
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class BusinessListActivity : AppCompatActivity(), BusinessListContract.View {

    @Inject
    lateinit var presenter: BusinessListPresenter

    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        searchBtn.setOnClickListener {
            if (validateInput()) {
                presenter.loadFirstPage(searchParam.text.toString())
            }
        }
        getComponent().inject(this)
    }

    // endregion

    // region private

    private fun getComponent(): BusinessListComponent {
        return DaggerBusinessListComponent.builder()
            .applicationComponent((application as YelpApp).getComponent())
            .businessListModule(BusinessListModule(this))
            .build()
    }

    private fun validateInput(): Boolean {
        if (TextUtils.isEmpty(searchParam.text.toString())) {
           // showError(getString(R.string.empty_list))
            AlertUtil.toastUser(this, "You are searching with out any Keyword!")
            return false
        }
        return true
    }
    // endregion

    // region View
    override fun showSpinner() {

    }

    override fun hideSpinner() {

    }

    override fun showBusiness(business: List<BusinessData>) {
        errContainer.visibility = View.GONE
        TODO("Not yet implemented")
    }

    override fun refresh(restaurants: List<BusinessData>) {
        TODO("Not yet implemented")
    }

    override val isRefreshing: Boolean
        get() = TODO("Not yet implemented")

    override fun showError(message: String?) {
        errContainer.visibility = View.VISIBLE

        if (message.isNullOrBlank()) {
            errorText.text = getString(R.string.empty_list)
        } else {
            errorText.text = message
        }
    }

    // endregion

}