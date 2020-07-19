package com.yelp.main

import android.util.Log
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
import java.util.ArrayList
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

    override fun loadFirstPage() {
        pageOffset = PAGE_SIZE
        loadMorePages()
    }

    override fun loadMorePages() {
        view.showSpinner()
        fetch("indian food", "40.581140", "-111.914184")
    }


    companion object {
        @VisibleForTesting
        private val PAGE_SIZE = 15
    }


    private fun fetch(term: String, lat: String, long: String): List<BusinessData> {
        val params: MutableMap<String, String> = HashMap()
        params["term"] = "indian food"
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

                        Log.e("Serach", "Business Data: " + business.imageUrl)
                    }

                }

                override fun onFailure(
                    call: Call<SearchResponse>,
                    t: Throwable
                ) {
                    Log.e("Search", "ERR: " + t.localizedMessage)
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
                Log.e("Review", "Top Review: " + reviews!!.reviews.get(0).text)
                result = reviews.reviews.get(0).text
            }

            override fun onFailure(call: Call<Reviews?>, t: Throwable) {
                // HTTP error happened, do something to handle it.
            }
        }
        call.enqueue(callback)

        return result
    }
}
