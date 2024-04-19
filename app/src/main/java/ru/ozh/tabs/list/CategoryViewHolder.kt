package ru.ozh.tabs.list

import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ozh.tabs.R
import ru.ozh.tabs.databinding.ItemCategoryBinding
import ru.ozh.tabs.databinding.ItemItemBinding
import ru.ozh.tabs.model.Category
import ru.ozh.tabs.model.Item
import ru.surfstudio.android.easyadapter.controller.BindableItemController
import ru.surfstudio.android.easyadapter.holder.BindableViewHolder

class CategoryController : BindableItemController<Category, CategoryController.Holder>() {

    override fun createViewHolder(parent: ViewGroup): Holder {
        return Holder(parent)
    }

    override fun getItemId(data: Category): Any {
        return data.name
    }

    override fun getItemHash(data: Category?): Any {
        return data.hashCode()
    }

    class Holder(parent: ViewGroup) : BindableViewHolder<Category>(parent, R.layout.item_category) {

        private val binding: ItemCategoryBinding by viewBinding(ItemCategoryBinding::bind)

        override fun bind(category: Category) {
            binding.categoryName.text = category.name
        }
    }
}

class ItemController : BindableItemController<Item, ItemController.Holder>() {

    override fun createViewHolder(parent: ViewGroup): Holder {
        return Holder(parent)
    }

    override fun getItemId(data: Item): Any {
        return data.content
    }

    override fun getItemHash(data: Item?): Any {
        return data.hashCode()
    }

    class Holder(parent: ViewGroup) : BindableViewHolder<Item>(parent, R.layout.item_item) {

        private val binding: ItemItemBinding by viewBinding(ItemItemBinding::bind)

        override fun bind(category: Item) {
            binding.itemNameTv.text = category.content
        }
    }
}

