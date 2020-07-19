package com.yelp.model

import timber.log.Timber

data class BusinessData(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double
) {
    private var review = "----"
    fun setReview(review: String) {
        Timber.e("Reviews? " + review)
        this.review = review
    }
}
