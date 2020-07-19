package com.yelp.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yelp.R
import com.yelp.app.YelpApp
import com.yelp.app.utils.AlertUtil
import com.yelp.app.utils.KeyBoardUtil
import com.yelp.app.utils.PaginationListenerUtil
import com.yelp.model.BusinessData
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class BusinessListActivity : AppCompatActivity(), BusinessListContract.View {

    @Inject
    lateinit var presenter: BusinessListPresenter

    var search: String = ""
    private lateinit var adapter: BusinessListAdapter
    // region lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getComponent().inject(this)

        setupToolbar()
        setupRecycleView()
        searchBtn.setOnClickListener {
            if (validateInput()) {
                KeyBoardUtil.hideSoftKeyBoard(this, searchParam)
                search = searchParam.text.toString()
                presenter.loadFirstPage(search)
            }
        }
    }

    override fun onBackPressed() {
        if (toolbar.title.equals(getString(R.string.app_name))) {

            super.onBackPressed()
        } else {

            showBackButton(false)
            initUI()
        }
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
            AlertUtil.toastUser(this, "You are searching with out any Keyword!")
            return false
        }
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(true)
        showBackButton(false)
    }

    private fun showBackButton(showBack: Boolean) {
        if (showBack) {
            toolbar.navigationIcon = getDrawable(R.drawable.abc_ic_ab_back_material)
            toolbar.setNavigationOnClickListener { onBackPressed() }
            toolbar.title = search

        } else {
            toolbar.navigationIcon = null
            toolbar.title = getString(R.string.app_name)
        }
    }

    private fun initUI() {
        searchContainer.visibility = View.VISIBLE
        errContainer.visibility = View.GONE
        list.visibility = View.GONE
    }

    private fun setupRecycleView() {
        searchContainer.visibility = View.VISIBLE
        list.visibility = View.VISIBLE
        errContainer.setVisibility(View.GONE)
        adapter = BusinessListAdapter()
        list.setAdapter(adapter)
        val layoutManager =
            list.getLayoutManager() as LinearLayoutManager
        list.addOnScrollListener(object :
            PaginationListenerUtil(layoutManager, pullToRefresh) {
            override fun loadNextPage() {
                if (!isLoading()) {
                    presenter.loadMorePages(search)
                }
            }
        })
        pullToRefresh.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        pullToRefresh.setOnRefreshListener({ refreshFeed() })
    }

    private fun isLoading(): Boolean {
        return progressBar.visibility === View.VISIBLE
    }

    private fun refreshFeed() {
        pullToRefresh.setRefreshing(true)
        pullToRefresh.postDelayed({ presenter.loadFirstPage(search) }, 1000)
    }

    // endregion

    // region View
    override fun showSpinner() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideSpinner() {
        progressBar.visibility = View.GONE
    }

    override fun showBusiness(businessList: List<BusinessData>) {
        errContainer.visibility = View.GONE
        searchContainer.visibility = View.GONE
        list.visibility = View.VISIBLE
        showBackButton(true)

        Timber.e("Business Data Size: " + businessList.size)
        adapter.loadPage(businessList)
        pullToRefresh.isRefreshing = false

    }


    override fun refresh(businessList: List<BusinessData>) {
        pullToRefresh.isRefreshing = false
        adapter.refresh(businessList)
        errContainer.visibility = View.GONE
        searchContainer.visibility = View.GONE
    }

    override var isRefreshing = false

    override fun showError(message: String?) {
        errContainer.visibility = View.VISIBLE

        if (message.isNullOrBlank()) {
            errorText.text = getString(R.string.empty_list)
        } else {
            errorText.text = message
        }
    }

    override fun showNetError() {
        showError(getString(R.string.network_error))
    }

    // endregion
}