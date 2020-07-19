package com.yelp.main

import androidx.annotation.VisibleForTesting
import com.yelp.app.scope.PerActivity
import com.yelp.fusion.client.connection.YelpFusionApi
import com.yelp.fusion.client.models.Business
import com.yelp.fusion.client.models.Reviews
import com.yelp.fusion.client.models.SearchResponse
import com.yelp.model.BusinessData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@PerActivity
class BusinessListPresenter @Inject internal constructor(
    view: BusinessListContract.View,
    apiService: YelpFusionApi
) : BusinessListContract.Presenter {
    private val view: BusinessListContract.View
    private val yelpFusionApi: YelpFusionApi

    @VisibleForTesting
    var pageOffset = 50

    init {
        this.view = view
        this.yelpFusionApi = apiService
    }

    companion object {
        @VisibleForTesting
        private val PAGE_SIZE = 15
    }

    // region override
    override fun loadFirstPage(searchParam: String) {
        Timber.e("Searching for " + searchParam)
        pageOffset = PAGE_SIZE
        loadMorePages(searchParam)
    }

    override fun loadMorePages(searchParam: String) {
        view.showSpinner()
        // fetch(searchParam, "34.068921", "-118.4451811")
        fetch(searchParam)
    }

    // endregion

    // region private

    private fun fetch(searchParam: String) {
        val params: MutableMap<String, String> = HashMap()
        params.put("term", searchParam)
        params.put("latitude", "40.581140")
        params.put("longitude", "-111.914184")
        params.put("limit", pageOffset.toString())

        val call: Call<SearchResponse> = yelpFusionApi.getBusinessSearch(params)

        val callback: Callback<SearchResponse> =
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    val searchResponse = response.body()
                    val businesses: List<Business> = searchResponse.businesses

                    view.hideSpinner()
                    if (businesses.isNullOrEmpty()) {
                        Timber.e("Business Result empty ??: " + businesses.size)
                        view.showError("")
                        return
                    }

                    Timber.e("Business Result Size: " + businesses.size)
                    val results: MutableList<BusinessData> = mutableListOf()
                    for (business in businesses) {
                        val businessData = BusinessData(
                            business.id,
                            business.name,
                            business.location.address1,
                            business.imageUrl,
                            business.rating
                        )
                        yelpReview(businessData)
                        results.add(businessData)
                    }

                    view.showBusiness(results)
                }

                override fun onFailure(
                    call: Call<SearchResponse>,
                    t: Throwable
                ) {
                    Timber.e("ERR: " + t.localizedMessage)
                    view.hideSpinner()
                    if (t.message.isNullOrBlank()) {
                        view.showNetError()
                    } else {
                        view.showError(t.message)
                    }
                }
            }

        call.enqueue(callback)
    }

    private fun yelpReview(businessData: BusinessData) {
        val call: Call<Reviews> = yelpFusionApi.getBusinessReviews(businessData.id, "en_US")
        var result = "--"
        val callback: Callback<Reviews> = object : Callback<Reviews> {
            override fun onResponse(
                call: Call<Reviews?>,
                response: Response<Reviews?>
            ) {
                val reviews = response.body()
                if (!reviews!!.reviews.isNullOrEmpty()) {
                    for (review in reviews.reviews) {
                        result = review.text
                        if (!result.isBlank()) {
                            businessData.setReview(result)
                            return
                        }
                    }
                }

                Timber.d("Top Review: " + reviews!!.reviews.size)
            }

            override fun onFailure(call: Call<Reviews?>, t: Throwable) {
                Timber.e("Something went wrong. " + t.message)
                // ignore the error , need not have to update review
            }
        }
        call.enqueue(callback)
    }
}
