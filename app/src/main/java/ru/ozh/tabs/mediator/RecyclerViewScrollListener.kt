package ru.ozh.tabs.mediator

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewScrollListener(
    private val onStateChanged: (recyclerView: RecyclerView, newState: Int) -> Unit = { _, _ -> },
    private val onScroll: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit = { _, _, _ -> }
) : RecyclerView.OnScrollListener(){

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        onStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        onScroll(recyclerView, dx, dy)
    }
}