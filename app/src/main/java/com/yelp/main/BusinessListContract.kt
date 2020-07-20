package com.yelp.main

import com.yelp.model.BusinessData

internal interface BusinessListContract {
    interface View {
        fun showSpinner()
        fun hideSpinner()
        fun showBusiness(businessList: List<BusinessData>)
        fun refresh(businessList: List<BusinessData>)
        var isRefreshing: Boolean
        fun showError(message: String?)
        fun showListEndedError()
        fun showNetError()
    }

    interface Presenter {
        fun loadFirstPage(searchParam: String)
        fun loadMorePages(searchParam: String)
    }
}
