package com.ondigo.bandheaven;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.os.Bundle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ondigo.bandheaven.R;

public class AspectGrid extends ViewGroup {

    private int mNumColumns = 1;
    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;
    private float mChildAspectRatio = 1.0f;



    public AspectGrid(Context context) {
        super(context);
    }

    public AspectGrid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectGrid);

            setNumColumns(a.getInt(R.styleable.AspectGrid_numColumns, mNumColumns));
            setHorizontalSpacing(a.getDimensionPixelSize(R.styleable.AspectGrid_horizontalSpacing, mHorizontalSpacing));
            setVerticalSpacing(a.getDimensionPixelSize(R.styleable.AspectGrid_verticalSpacing, mVerticalSpacing));
            setChildAspectRatio(a.getFloat(R.styleable.AspectGrid_childAspectRatio, mChildAspectRatio));

            a.recycle();
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setNumColumns(int numColumns) {
        if (numColumns < 1)
            throw new IllegalArgumentException("numColumns must be at least 1");
        if (numColumns != mNumColumns) {
            mNumColumns = numColumns;
            requestLayout();
        }
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
    }

    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        mVerticalSpacing = verticalSpacing;
    }

    public float getChildAspectRatio() {
        return mChildAspectRatio;
    }

    public void setChildAspectRatio(float childAspectRatio) {
        if (childAspectRatio <= 0)
            throw new IllegalArgumentException("childAspectRatio must be positive");
        if (childAspectRatio != mChildAspectRatio) {
            mChildAspectRatio = childAspectRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth = widthSize;
        int measuredHeight = heightSize;
        int width = Math.max(measuredWidth, getSuggestedMinimumWidth());
        int height = Math.max(measuredHeight, getSuggestedMinimumHeight());
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount <= 0)
            return;

        int innerWidth = r - l - getPaddingLeft() - getPaddingRight();
        int innerHeight = b - t - getPaddingBottom() - getPaddingTop();
        int numRows = (childCount + mNumColumns - 1) / mNumColumns;

        int leftEdge = getPaddingLeft();
        int topEdge = getPaddingTop();
        int horizontalStride = (innerWidth + mHorizontalSpacing) / mNumColumns;
        int verticalStride = (innerHeight + mVerticalSpacing) / numRows;
        int childWidth = horizontalStride - mHorizontalSpacing;
        int childHeight = verticalStride - mVerticalSpacing;

        if (childHeight * mChildAspectRatio > childWidth) {
            childHeight = (int)(childWidth / mChildAspectRatio);
            verticalStride = childHeight + mVerticalSpacing;
            topEdge = (innerHeight + mVerticalSpacing - numRows * verticalStride) / 2;
        } else {
            childWidth = (int)(childHeight * mChildAspectRatio);
            horizontalStride = childHeight + mHorizontalSpacing;
            leftEdge = (innerWidth + mHorizontalSpacing - mNumColumns * horizontalStride) / 2;
        }

        for (int i = 0; i < childCount; ++i) {
            View child = getChildAt(i);
            int row = i / mNumColumns;
            int column = i % mNumColumns;
            int left = leftEdge + column * horizontalStride;
            int top = topEdge + row * verticalStride;
            child.layout(
                    left,
                    top,
                    left + childWidth,
                    top + childHeight);
        }
    }

}