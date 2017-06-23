package com.ggdsn.pagenavigationbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Scroller

/**
 * Created by LiaoXingyu on 21/05/2017.
 */

class PageNavigationBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    companion object {
        private const val STATE_INIT = 0
        private const val STATE_MIDDLE = 1
    }

    private val textView = AppCompatTextView(context)
    private val backArrowView = AppCompatImageView(context)
    private val forwardArrowView = AppCompatImageView(context)

    private val forwardArrow: Drawable
    private val displayMetrics: DisplayMetrics
    private val backArrow: Drawable
    private val minPaddingDp = 8
    private val steps: MutableList<String> = mutableListOf()
    private var currentStepIdx = 0
    private var state = STATE_INIT
    private val lineScroller = Scroller(context)
    private val paint = Paint()
    private val lineRect = Rect()
    private val lineWidthDp = 4
    private var lineWidth: Int = 0
    private val marginDp = 10
    /**
     * 用于标记那条线偏移到的位置
     */
    private var linex: Int = 0

    init {
        val res = context.resources
        displayMetrics = res.displayMetrics
        lineWidth = (displayMetrics.density * lineWidthDp).toInt()
        paint.color = Color.WHITE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            forwardArrow = context.getDrawable(R.drawable.selector_pagenavigationbar_forward_arrow)
            backArrow = context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        } else {
            forwardArrow = res.getDrawable(R.drawable.selector_pagenavigationbar_forward_arrow)
            backArrow = res.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        }

        backArrowView.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        setBackgroundResource(R.drawable.shape_page_navigation_bar_background)

        backArrowView.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        forwardArrowView.setImageResource(R.drawable.selector_pagenavigationbar_forward_arrow)

        textView.gravity = Gravity.CENTER

        if (isInEditMode) {
            textView.text = "选择订单"
        } else {
            textView.setTextColor(res.getColorStateList(R.color.selector_pagenavigationbar_text))

            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PageNavigationBar, defStyleAttr, 0)
            textView.text = typedArray.getString(R.styleable.PageNavigationBar_text)
            typedArray.recycle()
        }

        addView(backArrowView)
        addView(textView)
        addView(forwardArrowView)
    }

    private val maxHeightDp: Int = 64

    private var maxTextWidth: Int? = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val density = displayMetrics.density

        if (heightMode == MeasureSpec.AT_MOST) {
            val maxHeight = (density * maxHeightDp - 2 * density * marginDp).toInt()
            height = if (height > maxHeight) maxHeight else height
        }
        setMeasuredDimension(width, height)
        linex = measuredWidth / 4

        val minPadding = (minPaddingDp * density).toInt()
        var paddingTop = paddingTop
        if (paddingTop < minPadding) paddingTop = minPadding
        var paddingBottom = paddingBottom
        if (paddingBottom < minPadding) paddingBottom = minPadding

        val contentHeight = height - minPadding * 2
        val iconWidthSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)
        val iconHeightSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)

        backArrowView.measure(iconWidthSpec, iconHeightSpec)
        forwardArrowView.measure(iconWidthSpec, iconHeightSpec)

        backArrowView.translationX = -linex.toFloat()
//        因为右边有图案而左边没有，要修正标题栏的位置，让其居中。
        setPadding(minPadding, paddingTop, minPadding, paddingBottom)

        maxTextWidth = width - paddingLeft - paddingRight - contentHeight
        textView.measure(MeasureSpec.makeMeasureSpec(maxTextWidth!!, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.AT_MOST))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val height = b - t
        backArrowView.layout(paddingLeft, paddingTop, paddingLeft + backArrowView.measuredWidth, height - paddingBottom)
        textView.layout(paddingLeft, paddingTop, paddingLeft + textView.measuredWidth, height - paddingBottom)
        forwardArrowView.layout(width - paddingRight - forwardArrowView.measuredWidth, paddingTop, width - paddingRight, height - paddingBottom)
    }

    fun setTitles(titles: List<String>) {
        setTitles(titles, 0)
    }

    fun setTitles(titles: List<String>, initStep: Int) {
        steps.clear()
        steps.addAll(titles)
        setStep(initStep)
    }

    fun nextStep() {
        setStep(if (currentStepIdx < steps.size - 1) currentStepIdx + 1 else steps.size - 1)
    }

    fun previousStep() {
        setStep(if (currentStepIdx > 0) currentStepIdx - 1 else 0)
    }

    fun setStep(step: Int) {
        if (step < 0 || step > steps.size) {
            throw IllegalArgumentException("step $step is out of bounds, steps size is ${steps.size}")
        }
        currentStepIdx = step
        val s = steps[step]
        textView.text = s
        when (step) {
            0 -> {
                if (state != STATE_INIT) showState(STATE_INIT)
            }
            else -> {
                if (state != STATE_MIDDLE) showState(STATE_MIDDLE)
            }
        }
    }

    private fun showState(state: Int) {
        when (state) {
            STATE_INIT -> {
                post {
                    lineScroller.startScroll(linex, 0, -linex, 0)
                    invalidate()
                }
            }
            STATE_MIDDLE -> {
                post {
                    lineScroller.startScroll(0, 0, linex, 0)
                    invalidate()
                }
            }
        }
        this.state = state
    }

    override fun computeScroll() {
        if (lineScroller.computeScrollOffset()) {
            var left = lineScroller.currX - lineWidth
            left = if (left < 0) 0 else left
            textView.translationX = left.toFloat()
            backArrowView.translationX = left.toFloat() - linex
            lineRect.set(left, 0, lineScroller.currX, height)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(lineRect, paint)
    }

}