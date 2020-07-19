package com.yelp.main

import android.annotation.SuppressLint
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
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.collections.set

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

    override fun loadFirstPage(searchParam: String) {
        Timber.e("Searching for "+searchParam)
        pageOffset = PAGE_SIZE
        loadMorePages(searchParam)
    }

    override fun loadMorePages(searchParam: String) {
        view.showSpinner()
        fetch(searchParam, "34.068921", "-118.4451811")
        //  fetch("indian food", "40.581140", "-111.914184")
    }


    companion object {
        @VisibleForTesting
        private val PAGE_SIZE = 15
    }

    private fun fetch(searchParam: String, lat: String, long: String): List<BusinessData> {
        val params: MutableMap<String, String> = HashMap()
        params["term"] = searchParam
        params["latitude"] = "40.581140"
        params["longitude"] = "-111.914184"
        params.put("limit", pageOffset.toString())

        val results: List<BusinessData> =
            ArrayList()
        val call: Call<SearchResponse> = yelpFusionApi.getBusinessSearch(params)

        val callback: Callback<SearchResponse> =
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    val searchResponse = response.body()
                    val businesses: List<Business> =
                        searchResponse.businesses
                    for (business in businesses) {
                        results.plus(
                            BusinessData(
                                business.name,
                                business.location.address1,
                                business.imageUrl,
                                business.rating,
                                yelpReview(business.id)
                            )
                        )

                        Timber.e("Business Data: " + business.imageUrl)
                    }

                }

                override fun onFailure(
                    call: Call<SearchResponse>,
                    t: Throwable
                ) {
                    Timber.e("ERR: " + t.localizedMessage)
                }
            }

        call.enqueue(callback)

        return results
    }

    private fun yelpReview(id: String?): String {
        val call: Call<Reviews> = yelpFusionApi.getBusinessReviews(id, "en_US")
        var result = "--"
        val callback: Callback<Reviews> = object : Callback<Reviews> {
            override fun onResponse(
                call: Call<Reviews?>,
                response: Response<Reviews?>
            ) {
                val reviews = response.body()
                Timber.e("Top Review: " + reviews!!.reviews.size)
                result = reviews.reviews.get(0).text
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<Reviews?>, t: Throwable) {
                Timber.e("Something went wrong. " + t.message)
                view.showError(t.message)
            }
        }
        call.enqueue(callback)

        return result
    }
}
