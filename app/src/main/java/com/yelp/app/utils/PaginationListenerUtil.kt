package com.yelp.app.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Adds listener to display the next set of pages on scrolling. This abstract class let the derived
 * class to define the action to call behavior with loadNextPage() method
 */
abstract class PaginationListenerUtil protected constructor(
    private val layoutManager: LinearLayoutManager,
    private val pullToRefresh: SwipeRefreshLayout?,
    visibleThreshold: Int
) :
    RecyclerView.OnScrollListener() {
    // The minimum amount of items to have below your current scroll position before loading more.
    private var visibleThreshold: Int = PaginationListenerUtil.Companion.VISIBLE_THRESHOLD_DEFAULT

    protected constructor(
        layoutManager: LinearLayoutManager,
        pullToRefresh: SwipeRefreshLayout?
    ) : this(
        layoutManager,
        pullToRefresh,
        PaginationListenerUtil.Companion.VISIBLE_THRESHOLD_DEFAULT
    ) {
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        // if pull to refresh exist enable it
        if (pullToRefresh != null) {
            pullToRefresh.isEnabled = layoutManager.findFirstCompletelyVisibleItemPosition() == 0
        }

        // End has been reached to take action
        if (totalItemCount - visibleItemCount
            <= firstVisibleItemPosition + visibleThreshold
        ) {
            loadNextPage()
        }
    }

    // caller specific behavior left for derived class implementation
    abstract fun loadNextPage()

    companion object {
        private const val VISIBLE_THRESHOLD_DEFAULT = 3
    }

    init {
        this.visibleThreshold = visibleThreshold
    }
}