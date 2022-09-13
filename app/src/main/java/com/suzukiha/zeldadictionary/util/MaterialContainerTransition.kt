package com.suzukiha.zeldadictionary.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import android.util.Property
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.res.use
import androidx.core.graphics.transform
import androidx.core.view.forEach
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable
import com.suzukiha.zeldadictionary.R

@Px
private const val BITMAP_PADDING_BOTTOM = 1
private const val PROP_BOUNDS = "materialContainerTransition:bounds"
private const val PROP_BITMAP = "materialContainerTransition:bitmap"
private const val PROP_SHAPE_APPEARANCE = "materialContainerTransition:shapeAppearance"
private const val PROP_CONTAINER_COLOR = "materialContainerTransition:containerColor"
private val TRANSITION_PROPS = arrayOf(
    PROP_BOUNDS,
    PROP_BITMAP,
    PROP_SHAPE_APPEARANCE,
    PROP_CONTAINER_COLOR
)

class MaterialContainerTransition(
    @IdRes private val drawInId: Int = android.R.id.content,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossFadeStartProgress: Float = 0.3f,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossFadeEndProgress: Float = 0.8f
) : Transition() {

    override fun getTransitionProperties() = TRANSITION_PROPS

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null || endValues.view !is ViewGroup) {
            return null
        }

        val view = endValues.view as ViewGroup
        val drawIn = view.findAncestorById(drawInId)

        val startBounds = startValues.values[PROP_BOUNDS] as RectF
        val endBounds = endValues.values[PROP_BOUNDS] as RectF
        val dr = MaterialContainerTransitionDrawable(
            startValues.values[PROP_BITMAP] as Bitmap,
            startBounds,
            (startValues.values[PROP_SHAPE_APPEARANCE] as ShapeAppearanceModel?).toCornerRounding(
                startBounds
            ),
            startValues.values[PROP_CONTAINER_COLOR] as Int,
            crossFadeStartProgress,
            endValues.values[PROP_BITMAP] as Bitmap,
            endBounds,
            (endValues.values[PROP_SHAPE_APPEARANCE] as ShapeAppearanceModel?).toCornerRounding(
                endBounds
            ),
            endValues.values[PROP_CONTAINER_COLOR] as Int,
            crossFadeEndProgress
        )

        return ObjectAnimator.ofFloat(dr, MaterialContainerTransitionDrawable.PROGRESS, 0f, 1f)
            .apply {
                doOnStart {
                    dr.setBounds(0, 0, drawIn.width, drawIn.height)
                    drawIn.overlay.add(dr)
                    view.forEach {
                        it.alpha = 0f
                    }
                }
                doOnEnd {
                    view.forEach {
                        it.alpha = 1f
                    }
                    drawIn.overlay.remove(dr)
                }
            }
    }

    @SuppressLint("Recycle")
    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view

        if (view.isLaidOut || view.width != 0 || view.height != 0) {
            val loc = IntArray(2)
            view.getLocationOnScreen(loc)
            val left = loc[0].toFloat() - view.translationX
            val top = loc[1].toFloat() - view.translationY
            transitionValues.values[PROP_BOUNDS] = RectF(
                left,
                top,
                left + view.width,
                top + view.height
            )
            view.jumpDrawablesToCurrentState()
            transitionValues.values[PROP_BITMAP] = view.drawToBitmap(BITMAP_PADDING_BOTTOM)

            if (view is Shapeable) {
                transitionValues.values[PROP_SHAPE_APPEARANCE] = view.shapeAppearanceModel
            } else {
                view.context.obtainStyledAttributes(intArrayOf(R.attr.transitionShapeAppearance))
                    .use {
                        val shapeAppId = it.getResourceId(0, -1)
                        if (shapeAppId != -1) {
                            transitionValues.values[PROP_SHAPE_APPEARANCE] = ShapeAppearanceModel
                                .builder(
                                    view.context,
                                    shapeAppId,
                                    0
                                ).build()
                        }
                    }
            }
            transitionValues.values[PROP_CONTAINER_COLOR] = view.descendantBackgroundColor()
        }
    }
}


private const val scrimAlpha = 102
private const val containerShadow = 0x1a000000
private const val containerNoShadow = 0x00000000

private class MaterialContainerTransitionDrawable(
    private val startImage: Bitmap,
    private val startBounds: RectF,
    private val startRadii: CornerRounding,
    @ColorInt private val containerStartColor: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossFadeStartProgress: Float,
    private val endImage: Bitmap,
    private val endBounds: RectF,
    private val endRadii: CornerRounding,
    @ColorInt private val containerEndColor: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossFadeEndProgress: Float,
    @ColorInt scrimColor: Int = 0xff000000.toInt()
) : Drawable() {

    private val imagePaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val startImageShader = BitmapShader(startImage, CLAMP, CLAMP)
    private val endImageShader = BitmapShader(endImage, CLAMP, CLAMP)
    private val scrimPaint = Paint().apply {
        style = Paint.Style.FILL
        color = scrimColor
    }
    private val containerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = containerStartColor
    }
    private val currentBounds = RectF(startBounds)
    private val entering = endBounds.height() > startBounds.height()

    private var progress = 0f
        set(value) {
            if (value != field) {
                field = value
                currentBounds.set(
                    lerp(startBounds.left, endBounds.left, value),
                    lerp(startBounds.top, endBounds.top, value),
                    lerp(startBounds.right, endBounds.right, value),
                    lerp(startBounds.bottom, endBounds.bottom, value)
                )

                startImageShader.transform {
                    val scale = currentBounds.width() / startImage.width.toFloat()
                    setScale(scale, scale)
                    postTranslate(currentBounds.left, currentBounds.top)
                }
                endImageShader.transform {
                    val scale = currentBounds.width() / endImage.width
                    setScale(scale, scale)
                    postTranslate(currentBounds.left, currentBounds.top)
                }
                invalidateSelf()
            }
        }

    override fun draw(canvas: Canvas) {
        scrimPaint.alpha = if (entering) {
            lerp(0, scrimAlpha, progress)
        } else {
            lerp(scrimAlpha, 0, progress)
        }
        if (scrimPaint.alpha > 0) canvas.drawRect(bounds, scrimPaint)

        val cornerRadii = lerp(
            startRadii,
            endRadii,
            crossFadeStartProgress,
            crossFadeEndProgress,
            progress
        )

        containerPaint.setShadowLayer(
            0f, 0f, 0f,
            if (entering) {
                lerpArgb(containerNoShadow, containerShadow, progress)
            } else {
                lerpArgb(containerShadow, containerNoShadow, progress)
            }
        )
        containerPaint.color = lerpArgb(
            containerStartColor,
            containerEndColor,
            crossFadeStartProgress,
            crossFadeEndProgress,
            progress
        )
        canvas.drawRoundedRect(
            currentBounds,
            cornerRadii,
            containerPaint
        )

        imagePaint.alpha = lerp(
            255,
            0,
            crossFadeStartProgress,
            crossFadeEndProgress,
            progress
        )
        if (imagePaint.alpha > 0) {
            imagePaint.shader = startImageShader
            canvas.drawRoundedRect(
                currentBounds,
                cornerRadii,
                imagePaint
            )
        }
        imagePaint.alpha = lerp(
            0,
            255,
            crossFadeStartProgress,
            crossFadeEndProgress,
            progress
        )
        if (imagePaint.alpha > 0) {
            imagePaint.shader = endImageShader
            canvas.drawRoundedRect(
                currentBounds,
                cornerRadii,
                imagePaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        imagePaint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        imagePaint.colorFilter = colorFilter
    }

    object PROGRESS : Property<MaterialContainerTransitionDrawable, Float>(
        Float::class.java,
        "progress"
    ) {
        override fun get(drawable: MaterialContainerTransitionDrawable) = drawable.progress

        override fun set(drawable: MaterialContainerTransitionDrawable, value: Float) {
            drawable.progress = value
        }
    }
}
