package com.qcwl.debo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * creatBy:Lajos
 * 2016/11/25 
 */
public class FlowLayout extends ViewGroup {
	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlowLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMax = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMax = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthNeed = 0;//需要的宽度
		int heightNeed = 0;//需要的高度
		int x_axle = 0;//位置坐标
		int y_axle = 0;
		int lineHeight = 0;//一行的高度
		View child;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			
			if (child.getVisibility() == View.GONE) {
				continue;
			}

			child.measure(widthMeasureSpec, heightMeasureSpec);//通知child计算自己的宽高度
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();//获取child的margin值
			int childHeight = child.getMeasuredHeight() + lp.topMargin+ lp.bottomMargin;
			
			//measure 时考虑把 margin 及  padding 也作为子视图大小的一部分
			measureChildWithMargins(child, widthMeasureSpec, 0,heightMeasureSpec, 0);
			
			int childWidth = child.getMeasuredWidth() + lp.leftMargin+ lp.rightMargin;
			if (x_axle + childWidth > widthMax) {//当一行的宽度不够时，本行高度和x轴都清零，y轴下移本行的高度
				y_axle += lineHeight;
				lineHeight = 0;
				x_axle = 0;
			}
			x_axle += childWidth;
			lineHeight = Math.max(lineHeight, childHeight);

			widthNeed = Math.max(widthNeed, x_axle);
			heightNeed = Math.max(heightNeed, y_axle + lineHeight);
		}
		setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthMax
				: widthNeed, heightMode == MeasureSpec.EXACTLY ? heightMax
				: heightNeed);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int widthMax = getWidth();
		int x_axle, y_axle;
		x_axle = 0;//位置坐标
		y_axle = 0;
		View child;
		int left = 0;//child左上角坐标点
		int top = 0;
		int lineHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			int childWidth = child.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.topMargin
					+ lp.bottomMargin;
			if (x_axle + childWidth > widthMax) {// 换行处理
				y_axle += lineHeight;
				x_axle = 0;
				lineHeight = 0;
			}
			left = x_axle + lp.leftMargin;
			top = y_axle + lp.topMargin;
			//在合适的位置摆放child
			child.layout(left, top, left + child.getMeasuredWidth(), top
					+ child.getMeasuredHeight());

			x_axle += childWidth;
			lineHeight = Math.max(lineHeight, childHeight);
		}
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT,
				MarginLayoutParams.WRAP_CONTENT);
	}
}
