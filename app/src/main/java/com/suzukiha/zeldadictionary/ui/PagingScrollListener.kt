package com.suzukiha.zeldadictionary.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PagingScrollListener(
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

        if (isLoadingPage() || isLastPage()) {
            return
        }
        if (layoutManager.childCount + firstVisibleItemPosition >= layoutManager.itemCount && firstVisibleItemPosition >= 0) {
            loadMoreItems()
        }
    }

    abstract fun loadMoreItems()

    abstract fun isLastPage(): Boolean

    abstract fun isLoadingPage(): Boolean
}
