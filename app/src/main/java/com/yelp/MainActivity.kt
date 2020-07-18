package com.yelp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.fusion.client.models.Business
import com.yelp.fusion.client.models.Reviews
import com.yelp.fusion.client.models.SearchResponse
import com.yelp.model.BusinessData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class MainActivity : AppCompatActivity() {

    val apiFactory = YelpFusionApiFactory()
    val yelpFusionApi = apiFactory.createAPI("y71yZBsubF0HQh8mwL2-09ojQVq_ikpyf7kM8YbVeTJHI9ZnegD8mjQKppxy1YOFZae1M2hw_wgMWzS7-tx8WdBHFeyS2pvYQWQumphXoZi_c-bcO5m0MDszvVoTX3Yx")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        yelp()
    }

    private fun yelp(): List<BusinessData> {
        val params: MutableMap<String, String> = HashMap()
        params["term"] = "indian food"
        params["latitude"] = "40.581140"
        params["longitude"] = "-111.914184"
        params.put("limit", "50")

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

                        Log.e("Serach","Business Data: "+business.imageUrl)
                    }

                }

                override fun onFailure(
                    call: Call<SearchResponse>,
                    t: Throwable
                ) {
                   Log.e("Search","ERR: "+t.localizedMessage)
                }
            }

        call.enqueue(callback)

        return results
    }

    private fun yelpReview(id: String?): String {
        val call: Call<Reviews> = yelpFusionApi.getBusinessReviews(id,"en_US")
        var result = "--"
        val callback: Callback<Reviews> = object : Callback<Reviews> {
            override fun onResponse(
                call: Call<Reviews?>,
                response: Response<Reviews?>
            ) {
                val reviews = response.body()
                Log.e("Review","Top Review: "+reviews!!.reviews.get(0).text)
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