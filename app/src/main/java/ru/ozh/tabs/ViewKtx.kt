package ru.ozh.tabs

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import androidx.annotation.ColorInt
import com.google.android.material.tabs.TabLayout

/**
 * Экстеншен для перевода dip в пиксели
 */
val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Экстеншен для перевода dip в пиксели
 */
val Float.toPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

fun TabLayout.setTabIndicatorColor(tabCategoryIndicatorColor: Int) {

    val categorySelectorDrawable: PaintDrawable =
        PaintDrawable(Color.WHITE)
            .apply {
                setCornerRadius(12f.toPx)
            }
    setSelectedTabIndicatorColor(tabCategoryIndicatorColor)
    setSelectedTabIndicator(categorySelectorDrawable)
}

fun TabLayout.Tab.setRippleColor(
    tabCategoryIndicatorColor: Int,
    tabCategoryIndicatorRippleColor: Int
) {

    view.replaceRipple(
        backgroundCornerRadius = 12f.toPx,
        backgroundColor = tabCategoryIndicatorColor,
        rippleColor = tabCategoryIndicatorRippleColor
    )
}

private fun View.replaceRipple(
    backgroundCornerRadius: Float,
    @ColorInt backgroundColor: Int,
    @ColorInt rippleColor: Int,
) {
    val contentDrawable: Drawable = GradientDrawable()
    val maskDrawable = GradientDrawable()
        .apply {
            cornerRadius = backgroundCornerRadius
            setColor(backgroundColor)
        }

    val rippleDrawable = RippleDrawable(
        ColorStateList.valueOf(rippleColor),
        contentDrawable,
        maskDrawable
    )
    this.foreground = rippleDrawable
    this.background = null
}