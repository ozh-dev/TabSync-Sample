package ru.ozh.tabs.mediator

import com.google.android.material.tabs.TabLayout
import java.util.*

class TabViewCompositeClickListener(private val tabLayout: TabLayout) {

    private val listeners: MutableList<(tab: TabLayout.Tab, position: Int) -> Unit> = ArrayList()

    fun addListener(listener: (tab: TabLayout.Tab, position: Int) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (tab: TabLayout.Tab, position: Int) -> Unit) {
        listeners.remove(listener)
    }

    fun build() {
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)!!.view.setOnClickListener {
                for (listener in listeners) {
                    listener(tabLayout.getTabAt(i)!!, i)
                }
            }
        }
    }

    fun getListeners(): List<(tab: TabLayout.Tab, position: Int) -> Unit> {
        return listeners
    }
}