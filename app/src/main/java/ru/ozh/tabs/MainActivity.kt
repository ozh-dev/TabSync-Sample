package ru.ozh.tabs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.color.MaterialColors
import ru.ozh.tabs.databinding.ActivityMainBinding
import ru.ozh.tabs.databinding.TabCategoryItemBinding
import ru.ozh.tabs.databinding.TabSubCategoryItemBinding
import ru.ozh.tabs.list.CategoryController
import ru.ozh.tabs.list.ItemController
import ru.ozh.tabs.mediator.TabLayoutMediator
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import com.google.android.material.R as materialR

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    private val tabCategoryIndicatorColor by lazy {
        MaterialColors.getColor(
            this,
            materialR.attr.colorTertiaryContainer,
            Color.BLACK
        )
    }

    private val tabCategoryIndicatorRippleColor by lazy {
        ColorUtils.setAlphaComponent(tabCategoryIndicatorColor, 0xB3) // 70% of alpha
    }

    private val categoryController = CategoryController(
        viewType = 100000
    )
    private val subCategoryController = CategoryController(
        viewType = 100001
    )
    private val itemController = ItemController()
    private val easyAdapter = EasyAdapter()

    private val indicesMap: MutableMap<Int, List<Int>> = mutableMapOf()
    private var itemsList: ItemList? = null
    private var childTabLayoutMediator: TabLayoutMediator? = null
    private val parentTabLayoutMediator: TabLayoutMediator by lazy {
        TabLayoutMediator(
            recyclerView = viewBinding.recyclerView,
            tabLayout = viewBinding.categoriesParentLayout,
            tabFactory = { tab, position ->
                tab.apply {
                    customView =
                        TabCategoryItemBinding.inflate(LayoutInflater.from(this@MainActivity))
                            .apply { this.tabName.text = categories[position].name }
                            .root

                    view.replaceRipple(
                        backgroundCornerRadius = 12f.toPx,
                        backgroundColor = tabCategoryIndicatorColor,
                        rippleColor = tabCategoryIndicatorRippleColor
                    )
                }
            },
            indicesProvider = {
                indicesMap.keys.toList()
            },
            tabSelectListener = { tabIndex, categoryIndex ->
                createChildTabLayout(tabIndex, categoryIndex)
            }
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        inflateItems()
        inflateIndexesMap()
        viewBinding.categoriesParentLayout.setTabIndicatorColor(tabCategoryIndicatorColor)
        viewBinding.categoriesChildLayout.setTabIndicatorColor(tabCategoryIndicatorColor)
        parentTabLayoutMediator.attach()
    }


    private fun initRecyclerView() {
        with(viewBinding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = easyAdapter
        }
    }

    private fun inflateItems() {
        itemsList = ItemList.create()
            .apply {
                categories.forEach { category ->
                    add(category, categoryController)
                    if (category.subCategories.isEmpty()) {
                        addAll(generateItems(), itemController)
                    }
                    category.subCategories.forEach { subCategory ->
                        add(subCategory, subCategoryController)
                        addAll(generateItems(), itemController)
                    }
                }
            }
            .also(easyAdapter::setItems)
    }

    private fun inflateIndexesMap() {
        val parentIndices = itemsList?.getIndicesByViewType(categoryController.viewType()) ?: emptyList()
        val childIndices = itemsList?.getIndicesByViewType(subCategoryController.viewType()) ?: emptyList()

        val indexPairs = parentIndices.zipWithNext { current, next ->
            current to childIndices.filter { it in current until next }
        } + (parentIndices.last() to childIndices.filter { it in parentIndices.last() until Int.MAX_VALUE })

        indicesMap.clear()
        indicesMap.putAll(indexPairs)
    }

    private fun ItemList?.getIndicesByViewType(viewType: Int): List<Int> {
        return this?.withIndex()
            ?.filter { (_, value) -> value.itemController.viewType() == viewType }
            ?.map { it.index } ?: emptyList()
    }

    private fun createChildTabLayout(categoryTabIndex: Int, categoryListIndex: Int) {
        childTabLayoutMediator?.detach()
        childTabLayoutMediator = null
        val childIndices = indicesMap[categoryListIndex]
        if (childIndices.isNullOrEmpty()) {
            return
        }
        childTabLayoutMediator = TabLayoutMediator(
            recyclerView = viewBinding.recyclerView,
            tabLayout = viewBinding.categoriesChildLayout,
            tabFactory = { tab, position ->
                tab.apply {
                    customView =
                        TabSubCategoryItemBinding.inflate(LayoutInflater.from(this@MainActivity))
                            .apply {
                                this.tabName.text =
                                    categories[categoryTabIndex].subCategories[position].name
                            }
                            .root

                    view.replaceRipple(
                        backgroundCornerRadius = 36f.toPx,
                        backgroundColor = tabCategoryIndicatorColor,
                        rippleColor = tabCategoryIndicatorRippleColor
                    )
                }
            },
            indicesProvider = { childIndices }
        ).apply { attach() }
    }
}