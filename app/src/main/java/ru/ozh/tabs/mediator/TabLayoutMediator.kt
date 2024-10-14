package ru.ozh.tabs.mediator

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.tabs.TabLayout

typealias Page = Pair<Int, Int>

class TabLayoutMediator(
    private val recyclerView: RecyclerView,
    private val tabLayout: TabLayout,
    private val tabFactory: (tab: TabLayout.Tab, position: Int) -> Unit,
    private val indicesProvider: () -> List<Int>,
    private val tabSelectListener: (categoryTabIndex: Int, categoryListIndex: Int) -> Unit = { categoryTabIndex: Int, categoryListIndex -> },
) {

    private var pagerAdapterObserver: PagerAdapterObserver? = null
    private val smoothScroller: SmoothScroller by lazy {
        object : LinearSmoothScroller(recyclerView.context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    private var cellIndices: List<Int> = emptyList()
    private var pages: List<Page> = emptyList()
    private var tabIsClicked = false

    // Поля для контроля состояния скролла
    private var isScrollByTabClick: Boolean = false
    private var previousScrollState = SCROLL_STATE_IDLE
    private var scrollState = SCROLL_STATE_IDLE
    private var chosenPage: Page? = Pair(0, 0)

    private val onScrollListener = RecyclerViewScrollListener(
        onStateChanged = { _, newState ->
            previousScrollState = scrollState
            scrollState = newState

            val isDraggingNow = newState == SCROLL_STATE_DRAGGING
            val isSettlingAfterClick =
                newState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE
            val isSettlingAfterScroll =
                newState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_DRAGGING
            val isScrollFinished = newState == SCROLL_STATE_IDLE

            when {
                isDraggingNow || isSettlingAfterScroll -> {
                    isScrollByTabClick = false
                }

                isSettlingAfterClick -> {
                    isScrollByTabClick = true
                }

                isScrollFinished -> {
                    isScrollByTabClick = false
                }
            }
        },
        onScroll = { recyclerView, _, _ ->
            if (isScrollByTabClick) return@RecyclerViewScrollListener

            val linearLayoutManager: LinearLayoutManager =
                recyclerView.layoutManager as LinearLayoutManager

            var firstVisibleCellIndex =
                linearLayoutManager.findFirstCompletelyVisibleItemPosition()

            if (firstVisibleCellIndex == -1) {
                firstVisibleCellIndex =
                    linearLayoutManager.findFirstVisibleItemPosition()
            }

            if (isScrolling()) {

                val lastVisibleCellIndex =
                    linearLayoutManager.findLastVisibleItemPosition()

                val itemCount = linearLayoutManager.itemCount

                if (lastVisibleCellIndex == itemCount - 1) {
                    selectTabBy(cellIndices.lastIndex)
                } else {
                    val page =
                        pages.firstOrNull { (startPageIndex, endPageIndex) -> firstVisibleCellIndex in startPageIndex..endPageIndex }

                    if (chosenPage == page) {
                        return@RecyclerViewScrollListener
                    }

                    Log.d("TabLayoutMediator", "$this found page $page")
                    chosenPage = page
                    page?.let { (startPageIndex, endPageIndex) ->
                        val categoryTabIndex = cellIndices.indexOf(startPageIndex)
                        selectTabBy(categoryTabIndex)
                        tabSelectListener(categoryTabIndex, startPageIndex)
                    }
                }
            }
        }
    )

    fun attach() {
        pagerAdapterObserver = PagerAdapterObserver { populateTabs() }
        pagerAdapterObserver?.let { recyclerView.adapter?.registerAdapterDataObserver(it) }
        recyclerView.addOnScrollListener(onScrollListener)
        populateTabs()

        cellIndices = indicesProvider()

        pages = cellIndices.zipWithNext { current, next ->
            current to next - 1
        } + (cellIndices.last() to Int.MAX_VALUE)

        log("pages: $pages")

        setTabsClickListener { tabIndex ->
            log("setTabsClickListener: tabIndex: $tabIndex| isScrolling(): ${isScrolling()}")
            if (isScrolling()) {
                return@setTabsClickListener
            }
            tabIsClicked = true
            val cellIndex = cellIndices[tabIndex]
            tabSelectListener(tabIndex, cellIndex)
            smoothScroller.targetPosition = cellIndex
            recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
        }
        selectTabBy(0)
    }

    fun detach() {
        pagerAdapterObserver?.let { recyclerView.adapter?.unregisterAdapterDataObserver(it) }
        pagerAdapterObserver = null
        recyclerView.removeOnScrollListener(onScrollListener)
        tabLayout.clearOnTabSelectedListeners()
        tabLayout.removeAllTabs()
    }

    private fun selectTabBy(index: Int) {
        if (tabLayout.getTabAt(index)?.isSelected == false) {
            tabLayout.getTabAt(index)?.select()
        }
    }

    private fun isScrolling(): Boolean {
        return scrollState == SCROLL_STATE_DRAGGING
                || scrollState == SCROLL_STATE_SETTLING
    }

    private fun populateTabs() {
        tabLayout.removeAllTabs()

        val adapter = recyclerView.adapter
        val indicesCount: Int = indicesProvider().size
        if (adapter != null) {
            for (i in 0 until indicesCount) {
                val tab = tabLayout.newTab()
                tabFactory(tab, i)
                tabLayout.addTab(tab, false)
            }
        }
    }

    private fun setTabsClickListener(onClick: (tabIndex: Int) -> Unit) {
        tabLayout.addOnTabSelectedListener(TabSelectListener(
            onSelected = {
                onClick(it.position)
            }
        ))
    }

    private fun log(message: String) {
        Log.d("TabLayoutMediator", "$this $message")
    }

}