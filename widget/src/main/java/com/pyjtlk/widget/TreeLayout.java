package com.pyjtlk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TreeLayout extends ViewGroup {
    public final static String TAG = "TAG_TreeLayout";

    public static final int DIRECTION_LEFT_TO_RIGHT = 0;
    public static final int DIRECTION_RIGHT_TO_LEFT = 1;
    public static final int DIRECTION_UP_TO_DOWN = 2;
    public static final int DIRECTION_DOWN_TO_UP = 3;

    private int mWrapWidth;
    private int mWrapHeight;
    private LineDrawer mLineDrawer;
    private Paint mPaint;
    private Rect mStartRect;
    private Rect mEndRect;
    private int mLevelInterval;
    private int mTreeDirection;
    private boolean mLocked;
    private float mLastX;
    private float mLastY;
    private boolean mMovePrepared;

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

    private void init(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TreeView);
        mTreeDirection = typedArray.getInt(R.styleable.TreeView_treeDirection,0);
        mLevelInterval = typedArray.getDimensionPixelSize(R.styleable.TreeView_levelInterval,0);
        mLocked = typedArray.getBoolean(R.styleable.TreeView_locked,true);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mStartRect = new Rect();
        mEndRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        mWrapWidth = measureWrapContentWidthHorizontal();
        mWrapHeight = measureWrapContentHeightHorizontal();

       if(mTreeDirection == DIRECTION_LEFT_TO_RIGHT || mTreeDirection == DIRECTION_RIGHT_TO_LEFT){
           measureHorizontal(widthMeasureSpec,heightMeasureSpec);
       }else{
           measureVertical(widthMeasureSpec,heightMeasureSpec);
       }
    }

    protected void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mWrapWidth = measureWrapContentWidthHorizontal();
        mWrapHeight = measureWrapContentHeightHorizontal();

        if(widthMode == MeasureSpec.AT_MOST){
            width = mWrapWidth;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            height = mWrapHeight;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
    }

    private int measureWrapContentHeightHorizontal(){
        int childCount = getChildCount();

        if(childCount <= 0){
            return 0;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();
        int rootHeightNeed = rootLayoutParams.topMargin + rootLayoutParams.bottomMargin + root.getMeasuredHeight();

        int childrenHeightNeeded = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            childrenHeightNeeded += (layoutParams.topMargin + layoutParams.bottomMargin + child.getMeasuredHeight());
        }

        return Math.max(rootHeightNeed,childrenHeightNeeded);
    }

    private int measureWrapContentWidthHorizontal(){
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
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int widthNeeded = layoutParams.leftMargin + layoutParams.rightMargin + child.getMeasuredWidth();
            maxWidth = Math.max(maxWidth,widthNeeded);
        }

        wrapWidth += maxWidth;
        wrapWidth += mLevelInterval;

        return wrapWidth;
    }

    protected void measureVertical(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mWrapWidth = measureWrapContentWidthVertical();
        mWrapHeight = measureWrapContentHeightVertical();

        if(widthMode == MeasureSpec.AT_MOST){
            width = mWrapWidth;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            height = mWrapHeight;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
    }

    protected int measureWrapContentWidthVertical(){
        int childCount = getChildCount();

        if(childCount <= 0){
            return 0;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();
        int rootWidthNeed = rootLayoutParams.leftMargin + rootLayoutParams.rightMargin + root.getMeasuredWidth();

        int childrenWidthNeed = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            childrenWidthNeed += (layoutParams.leftMargin + layoutParams.rightMargin + child.getMeasuredWidth());
        }

        return Math.max(rootWidthNeed,childrenWidthNeed);
    }

    protected int measureWrapContentHeightVertical(){
        int childCount = getChildCount();

        if(childCount <= 0){
            return 0;
        }

        View root = getChildAt(0);
        int wrapHeight = 0;

        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();
        wrapHeight += (rootLayoutParams.topMargin + rootLayoutParams.bottomMargin + root.getMeasuredHeight());

        if(childCount == 1){
            return wrapHeight;
        }

        int maxHeight = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int heightNeeded = layoutParams.topMargin + layoutParams.bottomMargin + child.getMeasuredHeight();
            maxHeight = Math.max(maxHeight,heightNeeded);
        }

        wrapHeight += maxHeight;
        wrapHeight += mLevelInterval;

        return wrapHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch(mTreeDirection){
            case DIRECTION_LEFT_TO_RIGHT:
                onLayoutLeftToRight(changed,l,t,r,b);
                break;

            case DIRECTION_RIGHT_TO_LEFT:
                onLayoutRightToLeft(changed,l,t,r,b);
                break;

            case DIRECTION_UP_TO_DOWN:
                onLayoutUpToDown(changed,l,t,r,b);
                break;

            case DIRECTION_DOWN_TO_UP:
                onLayoutDownToUp(changed,l,t,r,b);
                break;
        }
    }

    protected void onLayoutUpToDown(boolean changed, int l, int t, int r, int b){
        int childCount = getChildCount();

        if(childCount <= 0){
            return;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();

        int rootLeft = (mWrapWidth - root.getMeasuredWidth()) / 2 + rootLayoutParams.leftMargin;
        int rootTop = rootLayoutParams.topMargin;

        root.layout(rootLeft,
                rootTop,
                rootLeft + root.getMeasuredWidth(),
                rootTop + root.getMeasuredHeight());

        int childTopWithoutMargin = rootTop + root.getMeasuredHeight() + rootLayoutParams.bottomMargin + mLevelInterval;
        int childTop;
        int childLeft = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            childTop = childTopWithoutMargin + layoutParams.topMargin;
            childLeft += layoutParams.leftMargin;
            child.layout(childLeft,
                    childTop,
                    childLeft + child.getMeasuredWidth(),
                    childTop + child.getMeasuredHeight());
            childLeft += (child.getMeasuredWidth() + layoutParams.rightMargin);
        }
    }

    protected void onLayoutDownToUp(boolean changed, int l, int t, int r, int b){
        int childCount = getChildCount();

        if(childCount <= 0){
            return;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();

        int rootLeft = (mWrapWidth - root.getMeasuredWidth()) / 2 + rootLayoutParams.leftMargin;
        int rootBottom = getMeasuredHeight() - rootLayoutParams.bottomMargin;

        root.layout(rootLeft,
                rootBottom - root.getMeasuredHeight(),
                rootLeft + root.getMeasuredWidth(),
                rootBottom);

        int childBottomWithoutMargin = rootBottom - root.getMeasuredHeight() - rootLayoutParams.topMargin - mLevelInterval;
        int childBottom;
        int childLeft = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            childBottom = childBottomWithoutMargin - layoutParams.bottomMargin;
            childLeft += layoutParams.leftMargin;
            child.layout(childLeft,
                    childBottom - child.getMeasuredHeight(),
                    childLeft + child.getMeasuredWidth(),
                    childBottom);
            childLeft += (child.getMeasuredWidth() + layoutParams.rightMargin);
        }
    }

    protected void onLayoutLeftToRight(boolean changed, int l, int t, int r, int b){
        int childCount = getChildCount();

        if(childCount <= 0){
            return;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();

        int rootLeft = rootLayoutParams.leftMargin;
        int rootTop = (mWrapHeight - root.getMeasuredHeight()) / 2 + rootLayoutParams.topMargin;

        root.layout(rootLeft,
                rootTop,
                rootLeft + root.getMeasuredWidth(),
                rootTop + root.getMeasuredHeight());

        int childLeftWithoutMargin = rootLeft + root.getMeasuredWidth() + rootLayoutParams.rightMargin + mLevelInterval;
        int childLeft;
        int childTop = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
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

    protected void onLayoutRightToLeft(boolean changed, int l, int t, int r, int b){
        int childCount = getChildCount();

        if(childCount <= 0){
            return;
        }

        View root = getChildAt(0);
        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();

        int rootRight = getMeasuredWidth() - rootLayoutParams.rightMargin;
        int rootTop = (mWrapHeight - root.getMeasuredHeight()) / 2 + rootLayoutParams.topMargin;

        root.layout(rootRight - root.getMeasuredWidth(),
                rootTop,
                rootRight,
                rootTop + root.getMeasuredHeight());

        int childRightWithoutMargin = rootRight - root.getMeasuredWidth() - rootLayoutParams.leftMargin - mLevelInterval;
        int childRight;
        int childTop = 0;

        for(int i = 1;i < childCount;i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            childRight = childRightWithoutMargin - layoutParams.rightMargin;
            childTop += layoutParams.topMargin;
            child.layout(childRight - child.getMeasuredWidth(),
                    childTop,
                    childRight,
                    childTop + child.getMeasuredHeight());
            childTop += (child.getMeasuredHeight() + layoutParams.bottomMargin);
        }
    }

    protected void onDrawOnToppest(Canvas canvas){
        onDrawLine(canvas);
    }

    protected void onDrawLine(Canvas canvas){
        View root = getChildAt(0);
        mStartRect.left = root.getLeft();
        mStartRect.right = root.getRight();
        mStartRect.top = root.getTop();
        mStartRect.bottom = root.getBottom();

        for(int i = 1;i < getChildCount();i++){
            View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            mEndRect.left = child.getLeft();
            mEndRect.right = child.getRight();
            mEndRect.top = child.getTop();
            mEndRect.bottom = child.getBottom();
            mLineDrawer.onDrawLine(canvas,mPaint,mStartRect,mEndRect,mTreeDirection);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        onDrawOnToppest(canvas);
    }

    public void setLineDrawer(LineDrawer lineDrawer){
        mLineDrawer = lineDrawer;
        requestLayout();
        invalidate();
    }

    public int getLevelInterval() {
        return mLevelInterval;
    }

    public void setLevelInterval(int levelInterval) {
        this.mLevelInterval = levelInterval;
        requestLayout();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float currentX = event.getRawX();
        float currentY = event.getRawY();
        float deltaX;
        float deltaY;

        switch(action){
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_MOVE:
                if(mLocked){
                    return false;
                }

                if(!mMovePrepared){
                    mMovePrepared = true;
                    mLastX = currentX;
                    mLastY = currentY;
                }

                deltaX = currentX - mLastX;
                deltaY = currentY - mLastY;

                scrollBy((int)-deltaX,(int)-deltaY);

                mLastX = currentX;
                mLastY = currentY;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mMovePrepared = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            return super.onInterceptTouchEvent(event);
        }

        return !mLocked;
    }

    public boolean isLocked(){
       return mLocked;
    }

    public void lockTree(boolean lock){
        mLocked = lock;
    }

    private void log(String log){
        Log.d(TAG,log);
    }
}
