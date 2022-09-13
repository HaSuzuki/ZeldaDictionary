package com.suzukiha.zeldadictionary.util

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import com.google.android.material.animation.ArgbEvaluatorCompat
import kotlin.math.roundToInt

class MultipleSpringEndListener(
    onEnd: (Boolean) -> Unit,
    vararg springs: SpringAnimation
) {
    private val listeners = ArrayList<DynamicAnimation.OnAnimationEndListener>(springs.size)

    private var wasCancelled = false

    init {
        springs.forEach {
            val listener = object : DynamicAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(
                    animation: DynamicAnimation<out DynamicAnimation<*>>?,
                    canceled: Boolean,
                    value: Float,
                    velocity: Float
                ) {
                    animation?.removeEndListener(this)
                    wasCancelled = wasCancelled or canceled
                    listeners.remove(this)
                    if (listeners.isNullOrEmpty()) {
                        onEnd(wasCancelled)
                    }
                }
            }
            it.addEndListener(listener)
            listeners.add(listener)
        }
    }
}

fun forAllSpringsEnd(
    onEnd: (Boolean) -> Unit,
    vararg springs: SpringAnimation
): MultipleSpringEndListener {
    return MultipleSpringEndListener(onEnd, *springs)
}

fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}

fun lerp(
    startValue: Int,
    endValue: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    return (startValue + fraction * (endValue - startValue)).roundToInt()
}

fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = true
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Float {
    if (fraction < startFraction) {
        return startValue
    }
    if (fraction > endFraction) {
        return endValue
    }

    return lerp(startValue, endValue, (fraction - startFraction) / (endFraction - startFraction))
}


fun lerp(
    startValue: Int,
    endValue: Int,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = true
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    if (fraction < startFraction) {
        return startValue
    }
    if (fraction > endFraction) {
        return endValue
    }

    return lerp(startValue, endValue, (fraction - startFraction) / (endFraction - startFraction))
}

fun lerp(
    startValue: CornerRounding,
    endValue: CornerRounding,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = true
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): CornerRounding {
    if (fraction < startFraction) {
        return startValue
    }
    if (fraction > endFraction) {
        return endValue
    }

    return CornerRounding(
        lerp(
            startValue.topLeftRadius,
            endValue.topLeftRadius,
            startFraction,
            endFraction,
            fraction
        ),
        lerp(
            startValue.topRightRadius,
            endValue.topRightRadius,
            startFraction,
            endFraction,
            fraction
        ),
        lerp(
            startValue.bottomRightRadius,
            endValue.bottomRightRadius,
            startFraction,
            endFraction,
            fraction
        ),
        lerp(
            startValue.bottomLeftRadius,
            endValue.bottomLeftRadius,
            startFraction,
            endFraction,
            fraction
        )
    )
}

@ColorInt
fun lerpArgb(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    return ArgbEvaluatorCompat.getInstance().evaluate(
        fraction,
        startColor,
        endColor
    )
}

@ColorInt
fun lerpArgb(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = true
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    if (fraction < startFraction) {
        return startColor
    }
    if (fraction > endFraction) {
        return endColor
    }

    return lerpArgb(
        startColor,
        endColor,
        (fraction - startFraction) / (endFraction - startFraction)
    )
}
