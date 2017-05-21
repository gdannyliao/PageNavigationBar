package com.ggdsn.pagenavigationbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;

/**
 * Created by LiaoXingyu on 21/05/2017.
 */

public class PageNavigationBar extends AppCompatTextView {
	private Drawable forwardArrow;
	private DisplayMetrics displayMetrics;
	private int minPaddingDp = 8;

	public PageNavigationBar(Context context) {
		super(context);
		init(context, null, 0);
	}

	public PageNavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, android.R.attr.textViewStyle);
	}

	public PageNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int minPadding = (int) (minPaddingDp * displayMetrics.density);
		int paddingTop = getPaddingTop();
		if (paddingTop < minPadding) paddingTop = minPadding;
		int paddingBottom = getPaddingBottom();
		if (paddingBottom < minPadding) paddingBottom = minPadding;

		//因为右边有图案而左边没有，要修正标题栏的位置，让其居中。
		Rect bounds = forwardArrow.getBounds();
		setPadding(minPadding + bounds.right - bounds.left, paddingTop, minPadding, paddingBottom);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		Resources res = context.getResources();
		displayMetrics = res.getDisplayMetrics();
		setGravity(Gravity.CENTER);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			forwardArrow = context.getDrawable(R.drawable.selector_pagenavigationbar_forward_arrow);
		} else {
			forwardArrow = res.getDrawable(R.drawable.selector_pagenavigationbar_forward_arrow);
		}

		setBackgroundResource(R.drawable.shape_page_navigation_bar_background);
		setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrow, null);

		if (isInEditMode()) {
			setText("选择订单");
		} else {
			setTextColor(res.getColorStateList(R.color.selector_pagenavigationbar_text));
		}
	}
}
