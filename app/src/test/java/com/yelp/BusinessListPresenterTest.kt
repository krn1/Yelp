package com.yelp

import com.yelp.fusion.client.connection.YelpFusionApi
import com.yelp.fusion.client.models.*
import com.yelp.main.BusinessListContract
import com.yelp.main.BusinessListPresenter
import com.yelp.model.BusinessData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.VerificationModeFactory
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.mock.Calls

@RunWith(MockitoJUnitRunner::class)
class BusinessListPresenterTest {

    @Mock
    private lateinit var yelpFusionApi: YelpFusionApi

    @InjectMocks
    private lateinit var presenter: BusinessListPresenter

    @Mock
    private lateinit var view: BusinessListContract.View


    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = BusinessListPresenter(view, yelpFusionApi)
    }

    @Test
    @Throws(Exception::class)
    fun emptyBusinessListTest() {
        // Given
        val searchResponse = searchResponse(0)
        val reviews = buildReviews(0)
        val searchCall: retrofit2.Call<SearchResponse> = Calls.response(searchResponse)
        val searchKey = "bla"
        val reviewResponse: retrofit2.Call<Reviews> = Calls.response(reviews)
        val businessDataList = buildBusinessDataList(searchResponse)

        Mockito.`when`(yelpFusionApi.getBusinessSearch(getSearchParams(searchKey))).thenReturn(searchCall)
      //  Mockito.`when`(yelpFusionApi.getBusinessReviews(any(), eq("en_US"))).thenReturn(reviewResponse)

        // When
        presenter.loadFirstPage(searchKey)

        // Then

        Mockito.verify(view, VerificationModeFactory.times(1)).showError(null)
    }


    @Test
    @Throws(Exception::class)
    fun loadFirstPageTest() {
        // Given
        val searchResponse = searchResponse(1)
        val reviews = buildReviews(1)
        val searchCall: retrofit2.Call<SearchResponse> = Calls.response(searchResponse)
        val searchKey = "bla"
        val reviewResponse: retrofit2.Call<Reviews> = Calls.response(reviews)
        val businessDataList = buildBusinessDataList(searchResponse)

        Mockito.`when`(yelpFusionApi.getBusinessSearch(getSearchParams(searchKey))).thenReturn(searchCall)
        Mockito.`when`(yelpFusionApi.getBusinessReviews(any(), eq("en_US"))).thenReturn(reviewResponse)

        // When
        presenter.loadFirstPage(searchKey)

        // Then

        Mockito.verify(view, VerificationModeFactory.times(1)).showSpinner()
        Mockito.verify(view, VerificationModeFactory.times(1)).hideSpinner()
        Mockito.verify(view, VerificationModeFactory.times(1)).refresh(businessDataList)
    }

    @Test
    @Throws(Exception::class)
    fun loadMorePagesTest() {
        // Given
        presenter.pageOffset = 7
        val searchResponse = searchResponse(1)
        val reviews = buildReviews(1)
        val searchCall: retrofit2.Call<SearchResponse> = Calls.response(searchResponse)
        val searchKey = "bla"
        val reviewResponse: retrofit2.Call<Reviews> = Calls.response(reviews)
        val businessDataList = buildBusinessDataList(searchResponse)

        Mockito.`when`(yelpFusionApi.getBusinessSearch(getSearchParams(searchKey))).thenReturn(searchCall)
        Mockito.`when`(yelpFusionApi.getBusinessReviews(any(), eq("en_US"))).thenReturn(reviewResponse)

        // When
        presenter.loadMorePages(searchKey)

        // Then

        Mockito.verify(view, VerificationModeFactory.times(1)).showSpinner()
        Mockito.verify(view, VerificationModeFactory.times(1)).hideSpinner()
        Mockito.verify(view, VerificationModeFactory.times(1)).showBusiness(businessDataList)
    }

    // region private
    private fun buildBusinessDataList(response: SearchResponse): List<BusinessData> {
        val businesses: List<Business> = response.businesses
        val results: MutableList<BusinessData> = mutableListOf()
        for (business in businesses) {
            val businessData = BusinessData(
                business.id,
                business.name,
                business.categories[0].title,
                business.imageUrl,
                business.rating
            )
            businessData.setReview("jjkls")
            results.add(businessData)
        }
        return results
    }

    private fun createBusinessData(id: Int): BusinessData {
        val businessData = BusinessData(
            "1ab2c3-{$id}",
            "businessName",
            "category desc",
            "http://image.url",
            id.toDouble()
        )

        businessData.setReview("review 123")
        return businessData
    }

    private fun getSearchParams(searchKey: String): MutableMap<String, String>? {
        val params: MutableMap<String, String> = HashMap()
        params.put("term", searchKey)
        params.put("latitude", "34.068921")
        params.put("longitude", "-118.4451811")
        params.put("offset", presenter.pageOffset.toString())

        return params
    }

    private fun businessList(size: Int): List<Business> {
        val businessList: MutableList<Business> = ArrayList()
        for (i in 1..size) {
            businessList.add(createBusiness(i))
        }
        return businessList
    }

    private fun searchResponse(size: Int): SearchResponse {
        val searchResponse = SearchResponse()
        val businessList = businessList(size)
        searchResponse.businesses = businessList as java.util.ArrayList<Business>
        searchResponse.total = size
        return searchResponse
    }

    private fun createBusiness(id: Int): Business {
        val business = Business()
        business.name = "businessName"
        business.id = "1ab2c3-{$id}"
        business.imageUrl = "http://image.url"
        business.rating = id.toDouble()
        business.categories = categoryList(id)

        return business
    }

    private fun categoryList(id: Int): ArrayList<Category>? {
        val list = ArrayList<Category>()
        val category = Category()
        category.title = "bla{$id}"
        list.add(category)
        return list
    }

    private fun buildReviews(size: Int): Reviews {
        val review = review()
        val reviewList: MutableList<Review> = ArrayList()
        for (i in 1..size) {
            reviewList.add(review)
        }
        val reviews = Reviews()
        reviews.total = size
        reviews.reviews = reviewList as java.util.ArrayList<Review>?
        return reviews
    }

    private fun review(): Review {
        val review = Review()
        review.text = "jkaJLK KLKSAD"
        return review
    }

    //endregion
}