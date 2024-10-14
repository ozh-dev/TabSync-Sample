package ru.ozh.tabs

import ru.ozh.tabs.model.Category
import ru.ozh.tabs.model.Item
import kotlin.random.Random

// меню бургер кинга
val categories = mutableListOf(
    Category(
        name = "Бургеры",
        subCategories = listOf(
            Category("Говядина"),
            Category("Курица"),
            Category("Рыба"),
        )
    ),
    Category("Комбо"),
    Category("Острое меню",
        subCategories = listOf(
            Category("Острые бургеры"),
            Category("Острые закуски"),
            Category("Острый картофель"),
        )
    ),
    Category("Премиум Бургеры"),
    Category(
        "Закуски",
        subCategories = listOf(
            Category("Классические"),
            Category("Острые"),
            Category("Сметана и лук"),
        )
    ),
    Category("Десерты"),
    Category(
        "Напитки",
        subCategories = listOf(
            Category("Холодные напитки"),
            Category("Пиво"),
            Category("Горячие напитки"),
        )
    ),
    Category(
        "Соусы",
        subCategories = listOf(
            Category("Чесночные")
        )
    )
)

fun generateItems(): List<Item> {
    return List(Random.nextInt(5, 8)) {
        Item(
            itemName = "Item: $it"
        )
    }
}