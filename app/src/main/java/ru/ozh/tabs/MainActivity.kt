package ru.ozh.tabs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.color.MaterialColors
import ru.ozh.tabs.databinding.ActivityMainBinding
import ru.ozh.tabs.databinding.TabCategoryItemBinding
import ru.ozh.tabs.list.CategoryController
import ru.ozh.tabs.list.ItemController
import ru.ozh.tabs.model.Category
import ru.ozh.tabs.model.Item
import ru.ozh.tabs.mediator.TabLayoutMediator
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import com.google.android.material.R as materialR

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    //    private val pictureController = PictureController()
    private val categoryController = CategoryController()
    private val itemController = ItemController()
    private val easyAdapter = EasyAdapter()

    private val categories = mutableListOf(
        Category(
            "Category 1",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 3"),
            Item("Item 4"),
            Item("Item 5"),
            Item("Item 6")
        ),
        Category(
            "Category 2",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 3"),
            Item("Item 4"),
        ),
        Category(
            "Category 3",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 3"),
            Item("Item 4"),
            Item("Item 5"),
            Item("Item 6"),
            Item("Item 7"),
            Item("Item 8"),
        ),
        Category(
            "Category 4",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 3"),
            Item("Item 4"),
            Item("Item 5"),
            Item("Item 6")
        ),
        Category(
            "Category 5",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 4"),
            Item("Item 5"),
        ),
        Category(
            "Category 6",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 4"),
            Item("Item 5"),
        ),
        Category(
            "Category 7",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 4"),
            Item("Item 5"),

            Item("Item 6"),
            Item("Item 7"),
            Item("Item 8"),
        ),
        Category(
            "Category  8",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 4"),
            Item("Item 5"),

            Item("Item 6"),
            Item("Item 7"),
            Item("Item 8"),
        ),
        Category(
            "Category 9",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 4"),
            Item("Item 5"),

            Item("Item 6"),
            Item("Item 7"),
            Item("Item 8"),
        ),
        Category(
            "Category 10",
            Item("Item 1"),
            Item("Item 2"),
            Item("Item 4"),
            Item("Item 5"),

            Item("Item 6"),
            Item("Item 7"),
            Item("Item 8"),
        ),
    )

    private var tabbedListMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTabAdapter()
        initRecyclerView()
        initMediator()
        inflateItems()
    }

    private fun inflateItems() {
        val items = ItemList.create().apply {
            categories.forEach {
                add(it, categoryController)
                addAll(it.listOfItems, itemController)
            }
        }
            .also(easyAdapter::setItems)

        items.withIndex()
            .filter { (_, value) -> value.itemController.viewType() == categoryController.viewType() }
            .map { it.index }
            .also {
                Log.d("MainActivity", "CategoryController positions: $it")
                tabbedListMediator?.setIndices(it)
            }
    }

    private fun initRecyclerView() {
        with(viewBinding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = easyAdapter
        }
    }

    private fun initTabAdapter() {
        val tabCategoryIndicatorColor =
            MaterialColors.getColor(this, materialR.attr.colorTertiaryContainer, Color.BLACK)
        val tabCategoryIndicatorRippleColor =
            ColorUtils.setAlphaComponent(tabCategoryIndicatorColor, 0xB3) // 70% of alpha
        viewBinding.categoriesLayout.setTabIndicatorColor(tabCategoryIndicatorColor)

        for (category in categories) {
            viewBinding.categoriesLayout.addTab(
                viewBinding.categoriesLayout.newTab()
                    .apply {
                        customView =
                            TabCategoryItemBinding.inflate(LayoutInflater.from(this@MainActivity))
                                .apply { this.tabIcon.text = category.name }
                                .root
                        setRippleColor(
                            tabCategoryIndicatorColor,
                            tabCategoryIndicatorRippleColor
                        )
                    }
            )
        }
    }

    private fun initMediator() = with(viewBinding) {
        tabbedListMediator = TabLayoutMediator(
            recyclerView,
            categoriesLayout
        ).also {
            it.isSmoothScroll = true
            it.attach()
        }
    }
}