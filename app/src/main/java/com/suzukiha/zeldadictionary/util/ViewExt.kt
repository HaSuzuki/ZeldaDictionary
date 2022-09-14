package com.suzukiha.zeldadictionary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.content.res.use
import androidx.core.graphics.applyCanvas
import androidx.core.view.*
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.appbar.AppBarLayout
import com.suzukiha.zeldadictionary.R

fun View.spring(
    property: DynamicAnimation.ViewProperty,
    stiffness: Float = 200f,
    damping: Float = 0.3f,
    startVelocity: Float? = null
): SpringAnimation {
    val key = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation?
    if (springAnim == null) {
        springAnim = SpringAnimation(this, property).apply {
            spring = SpringForce().apply {
                this.dampingRatio = damping
                this.stiffness = stiffness
                startVelocity?.let { setStartVelocity(it) }
            }
        }
        setTag(key, springAnim)
    }
    return springAnim
}

@IdRes
private fun getKey(property: DynamicAnimation.ViewProperty): Int {
    return when (property) {
        SpringAnimation.TRANSLATION_X -> R.id.translation_x
        SpringAnimation.TRANSLATION_Y -> R.id.translation_y
        SpringAnimation.TRANSLATION_Z -> R.id.translation_z
        SpringAnimation.SCALE_X -> R.id.scale_x
        SpringAnimation.SCALE_Y -> R.id.scale_y
        SpringAnimation.ROTATION -> R.id.rotation
        SpringAnimation.ROTATION_X -> R.id.rotation_x
        SpringAnimation.ROTATION_Y -> R.id.rotation_y
        SpringAnimation.X -> R.id.x
        SpringAnimation.Y -> R.id.y
        SpringAnimation.Z -> R.id.z
        SpringAnimation.ALPHA -> R.id.alpha
        SpringAnimation.SCROLL_X -> R.id.scroll_x
        SpringAnimation.SCROLL_Y -> R.id.scroll_y
        else -> throw IllegalAccessException("Unknown Property: $property")
    }
}

fun View.findAncestorById(@IdRes ancestorId: Int): View {
    return when {
        id == ancestorId -> this
        parent is View -> (parent as View).findAncestorById(ancestorId)
        else -> throw IllegalArgumentException("$ancestorId not a valid ancestor")
    }
}

fun View.drawToBitmap(@Px extraPaddingBottom: Int = 0): Bitmap {
    if (!ViewCompat.isLaidOut(this)) {
        throw IllegalStateException("View must needs to be laid out before calling #drawToBitmap")
    }
    return Bitmap.createBitmap(width, height + extraPaddingBottom, Bitmap.Config.ARGB_8888).applyCanvas {
        translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(this)
    }
}

@ColorInt
fun View.descendantBackgroundColor(): Int {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = descendantBackgroundColorOrNull()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return context.themeColor(android.R.attr.colorBackground)
}

@ColorInt
private fun View.descendantBackgroundColorOrNull(): Int? {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = backgroundColor()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return null
}

@ColorInt
fun View.backgroundColor(): Int? {
    val bg = background
    if (bg is ColorDrawable) {
        return bg.color
    }
    return null
}

@ColorInt
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.GRAY)
    }
}

fun View.updateAppbarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<AppBarLayout.LayoutParams> {
            leftMargin = insets.left
            topMargin = insets.top
            bottomMargin = insets.bottom
        }
        WindowInsetsCompat.CONSUMED
    }
}

fun View.updateToolbarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = insets.left,
            top = insets.top,
            bottom = insets.bottom
        )
        WindowInsetsCompat.CONSUMED
    }
}
