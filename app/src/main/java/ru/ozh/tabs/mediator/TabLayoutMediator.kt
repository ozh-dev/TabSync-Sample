package ru.ozh.tabs.mediator

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.tabs.TabLayout

/**
 * This class is made to provide the ability to sync between RecyclerView's specific items with
 * TabLayout tabs.
 *
 * @param recyclerView     The RecyclerView that is going to be synced with the TabLayout
 * @param tabLayout        The TabLayout that is going to be synced with the RecyclerView specific
 *                          items.
 */
class TabLayoutMediator(
    private val recyclerView: RecyclerView,
    private val tabLayout: TabLayout,
) {

    var isSmoothScroll: Boolean = false
    private var isAttached = false
    private var indices: List<Int> = emptyList()
    private var recyclerState = RecyclerView.SCROLL_STATE_IDLE
    private var tabClickFlag = false

    private val smoothScroller: SmoothScroller =
        object : LinearSmoothScroller(recyclerView.context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

    private var tabViewCompositeClickListener: TabViewCompositeClickListener =
        TabViewCompositeClickListener(tabLayout)

    private val onTabSelectedListener = TabSelectListener(
        onSelected = { tab ->
            if (!tabClickFlag) return@TabSelectListener

            val position = tab.position

            if (isSmoothScroll) {
                smoothScroller.targetPosition = indices[position]
                recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
            } else {
                recyclerView.layoutManager?.scrollToPosition(indices[position])
                tabClickFlag = false
            }
        }
    )

    private val onScrollListener = RecyclerViewScrollListener(
        onStateChanged = { _, newState ->
            recyclerState = newState
            if (isSmoothScroll && newState == RecyclerView.SCROLL_STATE_IDLE) {
                tabClickFlag = false
            }
        },
        onScroll = { recyclerView, dx, dy ->
            if (tabClickFlag) {
                return@RecyclerViewScrollListener
            }

            val linearLayoutManager: LinearLayoutManager =
                recyclerView.layoutManager as? LinearLayoutManager
                    ?: error("No LinearLayoutManager attached to the RecyclerView.")

            var itemPosition =
                linearLayoutManager.findFirstCompletelyVisibleItemPosition()

            if (itemPosition == -1) {
                itemPosition =
                    linearLayoutManager.findFirstVisibleItemPosition()
            }

            if (recyclerState == RecyclerView.SCROLL_STATE_DRAGGING
                || recyclerState == RecyclerView.SCROLL_STATE_SETTLING
            ) {
                for (i in indices.indices) {
                    if (itemPosition == indices[i]) {
                        if (!tabLayout.getTabAt(i)!!.isSelected) {
                            tabLayout.getTabAt(i)!!.select()
                        }
                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == indices[indices.size - 1]) {
                            if (!tabLayout.getTabAt(indices.size - 1)!!.isSelected) {
                                tabLayout.getTabAt(indices.size - 1)!!.select()
                            }
                            return@RecyclerViewScrollListener
                        }
                    }
                }
            }
        }
    )

    /**
     * Calling this method will
     */
    fun setIndices(newIndices: List<Int>) {
        indices = newIndices

        if (isAttached) {
            reAttach()
        }
    }

    /**
     * @param listener the listener the will applied on "the view" of the tab. This method is useful
     * when attaching a click listener on the tabs of the TabLayout.
     * Note that this method is REQUIRED in case of the need of adding a click listener on the view
     * of a tab layout. Since the mediator uses a click flag @see TabLayoutMediator#mTabClickFlag
     * it's taking the place of the normal on click listener, and thus the need of the composite click
     * listener pattern, so adding listeners should be done using this method.
     */
    fun addOnViewOfTabClickListener(
        listener: (tab: TabLayout.Tab, position: Int) -> Unit
    ) {
        tabViewCompositeClickListener.addListener(listener)
        if (isAttached) {
            notifyIndicesChanged()
        }
    }

    /**
     * Calling this method will ensure that the data that has been provided to the mediator is
     * valid for use, and start syncing between the the RecyclerView and the TabLayout.
     *
     * Call this method when you have:
     *      1- provided a RecyclerView Adapter,
     *      2- provided a TabLayout with the appropriate number of tabs,
     *      3- provided indices of the recyclerview items that you are syncing the tabs with. (You
     *         need to be providing indices of at most the number of Tabs inflated in the TabLayout.)
     */
    fun attach() {
        recyclerView.adapter ?: error("Cannot attach with no Adapter provided to RecyclerView")
        if (tabLayout.tabCount == 0) error("Cannot attach with no tabs provided to TabLayout")
        if (indices.size > tabLayout.tabCount) error("Cannot attach using more indices than the available tabs")

        notifyIndicesChanged()
        isAttached = true
    }

    /**
     * Calling this method will ensure to stop the synchronization between the RecyclerView and
     * the TabLayout.
     */
    fun detach() {
        clearListeners()
        isAttached = false
    }

    /**
     * This method will ensure that the synchronization is up-to-date with the data provided.
     */
    private fun reAttach() {
        detach()
        attach()
    }

    /**
     * This method will attach the listeners required to make the synchronization possible.
     */
    private fun notifyIndicesChanged() {
        tabViewCompositeClickListener.addListener { _, _ -> tabClickFlag = true }
        tabViewCompositeClickListener.build()
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
        recyclerView.addOnScrollListener(onScrollListener)
    }


    /**
     * This method will ensure that any listeners that have been added by the mediator will be
     * removed, including the one listener from
     * @see TabLayoutMediator#addOnViewOfTabClickListener((TabLayout.Tab, int) -> Unit)
     */
    private fun clearListeners() {
        recyclerView.clearOnScrollListeners()
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)!!.view.setOnClickListener(null)
        }
        for (i in tabViewCompositeClickListener.getListeners().indices) {
            tabViewCompositeClickListener.getListeners().toMutableList().removeAt(i)
        }
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
        recyclerView.removeOnScrollListener(onScrollListener)
    }
}