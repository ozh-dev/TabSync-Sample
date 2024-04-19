package ru.ozh.tabs.mediator

import com.google.android.material.tabs.TabLayout

class TabSelectListener(
    private val onSelected: (tab: TabLayout.Tab) -> Unit = {},
    private val onUnselected: (tab: TabLayout.Tab) -> Unit = {},
    private val onReselected: (tab: TabLayout.Tab) -> Unit = {},
) : TabLayout.OnTabSelectedListener {

    override fun onTabSelected(tab: TabLayout.Tab) {
        onSelected(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        onUnselected(tab)
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        onReselected(tab)
    }
}