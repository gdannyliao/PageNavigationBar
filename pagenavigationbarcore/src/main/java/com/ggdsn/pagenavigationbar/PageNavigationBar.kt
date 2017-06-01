package com.ggdsn.pagenavigationbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Scroller

/**
 * Created by LiaoXingyu on 21/05/2017.
 */

class PageNavigationBar : AppCompatTextView {
    companion object {
        private const val STATE_INIT = 0
        private const val STATE_MIDDLE = 1
    }

    private var forwardArrow: Drawable? = null
    private var displayMetrics: DisplayMetrics? = null
    private var backArrow: Drawable? = null
    private val minPaddingDp = 8
    private val steps: MutableList<String> = mutableListOf()
    private var currentStepIdx = 0
    private var state = STATE_INIT
    private var lineScroller: Scroller
    private val paint = Paint()
    private val lineRect = Rect()
    private val lineWidthDp = 4
    private var lineWidth: Int = 0
    private var linex: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0)
        lineScroller = Scroller(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, android.R.attr.textViewStyle)
        lineScroller = Scroller(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
        lineScroller = Scroller(context)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val res = context.resources
        displayMetrics = res.displayMetrics
        lineWidth = (displayMetrics!!.density * lineWidthDp).toInt()

        gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            forwardArrow = context.getDrawable(R.drawable.selector_pagenavigationbar_forward_arrow)
            backArrow = context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        } else {
            forwardArrow = res.getDrawable(R.drawable.selector_pagenavigationbar_forward_arrow)
            backArrow = res.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        }

        setBackgroundResource(R.drawable.shape_page_navigation_bar_background)
        setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrow, null)

        if (isInEditMode) {
            text = "选择订单"
        } else {
            setTextColor(res.getColorStateList(R.color.selector_pagenavigationbar_text))
        }
        paint.color = Color.WHITE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("onMeasure")
        val minPadding = (minPaddingDp * displayMetrics!!.density).toInt()
        var paddingTop = paddingTop
        if (paddingTop < minPadding) paddingTop = minPadding
        var paddingBottom = paddingBottom
        if (paddingBottom < minPadding) paddingBottom = minPadding

        //因为右边有图案而左边没有，要修正标题栏的位置，让其居中。
        val bounds = forwardArrow!!.bounds
        setPadding(minPadding + bounds.right - bounds.left, paddingTop, minPadding, paddingBottom)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        linex = measuredWidth / 4
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
        text = s
        when (step) {
            0 -> showState(STATE_INIT)
            else -> {
                if (state != STATE_MIDDLE)
                    showState(STATE_MIDDLE)
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
            val left = lineScroller.currX - lineWidth
            lineRect.set(if (left < 0) 0 else left, 0, lineScroller.currX, height)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(lineRect, paint)
    }

}