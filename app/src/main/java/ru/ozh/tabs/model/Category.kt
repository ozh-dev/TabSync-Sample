package ru.ozh.tabs.model

class Category(
    val name: String,
    val subCategories: List<Category> = emptyList()
)