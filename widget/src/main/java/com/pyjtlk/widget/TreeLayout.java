package com.pyjtlk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class TreeLayout extends ViewGroup {
    public final static String TAG = "TAG_TreeLayout";

    private int mWrapWidth;
    private int mWrapHeight;
    private LineDrawer mLineDrawer;
    private Paint mPaint;
    private Rect mStartRect;
    private Rect mEndRect;
    private int mLevelInterval;

    public static class LayoutParams extends MarginLayoutParams{
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private static class TreeViewException extends RuntimeException{
        public TreeViewException(String message) {
            super(message);
        }
    }

    public static abstract class LineDrawer {
        protected abstract void onDrawLine(Canvas canvas, Paint paint, Rect start, Rect end, int direction);
    }

    public TreeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mStartRect = new Rect();
        mEndRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        mWrapWidth = measureWrapContentWidth();
        mWrapHeight = measureWrapContentHeight();

        if(widthMode == MeasureSpec.AT_MOST){
            width = mWrapWidth;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            height = mWrapHeight;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
    }

    private int measureWrapContentWidth(){
        int childCount = getChildCount();

        if(childCount <= 0){
            return 0;
        }

        View root = getChildAt(0);
        int wrapWidth = 0;

        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();
        wrapWidth += (rootLayoutParams.leftMargin + rootLayoutParams.rightMargin + root.getMeasuredWidth());
        log("rootWidth:" + wrapWidth);

        if(childCount == 1){
            return wrapWidth;
        }

        int maxWidth = 0;

        for(int i = 1;i < childCount;i++){
            View view = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int widthNeeded = layoutParams.leftMargin + layoutParams.rightMargin + view.getMeasuredWidth();
            maxWidth = Math.max(maxWidth,widthNeeded);
        }

        wrapWidth += maxWidth;

        return wrapWidth;
    }

    private int measureWrapContentHeight(){
        int childCount = getChildCount();

        if(childCount <= 0){
            return 0;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();
        int rootHeightNeed = rootLayoutParams.topMargin + rootLayoutParams.bottomMargin + root.getMeasuredHeight();

        int childrenHeightNeeded = 0;

        for(int i = 1;i < childCount;i++){
            View view = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            childrenHeightNeeded += (layoutParams.topMargin + layoutParams.bottomMargin + view.getMeasuredHeight());
        }

        return Math.max(rootHeightNeed,childrenHeightNeeded);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        if(childCount <= 0){
            return;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();

        int height = getMeasuredHeight();

        int rootLeft = rootLayoutParams.leftMargin;
        int rootTop = (mWrapHeight - root.getMeasuredHeight()) / 2 + rootLayoutParams.topMargin;

        root.layout(rootLeft,
                rootTop,
                rootLeft + root.getMeasuredWidth(),
                rootTop + root.getMeasuredHeight());

        int childLeftWithoutMargin = rootLeft + root.getMeasuredWidth() + rootLayoutParams.rightMargin;
        int childLeft;
        int childTop = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            childLeft = childLeftWithoutMargin + layoutParams.leftMargin;
            childTop += layoutParams.topMargin;
            child.layout(childLeft,
                    childTop,
                    childLeft + child.getMeasuredWidth(),
                    childTop + child.getMeasuredHeight());
            childTop += (child.getMeasuredHeight() + layoutParams.bottomMargin);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //super.onDrawForeground(canvas);
        View root = getChildAt(0);
        mStartRect.left = root.getLeft();
        mStartRect.right = root.getRight();
        mStartRect.top = root.getTop();
        mStartRect.bottom = root.getBottom();

        for(int i = 1;i < getChildCount();i++){
            View child = getChildAt(i);
            mEndRect.left = child.getLeft();
            mEndRect.right = child.getRight();
            mEndRect.top = child.getTop();
            mEndRect.bottom = child.getBottom();
            mLineDrawer.onDrawLine(canvas,mPaint,mStartRect,mEndRect,0);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    public void setLineDrawer(LineDrawer lineDrawer){
        mLineDrawer = lineDrawer;
        invalidate();
    }

    private void log(String log){
        Log.d(TAG,log);
    }
}
