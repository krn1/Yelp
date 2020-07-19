package com.yelp.main

import com.yelp.model.BusinessData

internal interface BusinessListContract {
    interface View {
        fun showSpinner()
        fun hideSpinner()
        fun showBusiness(business: List<BusinessData>)
        fun refresh(restaurants: List<BusinessData>)
        val isRefreshing: Boolean
        fun showError(message: String?)
        fun showNetError()
    }

    interface Presenter {
        fun loadFirstPage(searchParam: String)
        fun loadMorePages(searchParam: String)
    }
}
