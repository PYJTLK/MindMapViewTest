package com.pyjtlk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TreeView extends LinearLayout {
    public final static String TAG = "TAG_TreeView";

    public static final int DIRECTION_LEFT_TO_RIGHT = 0;
    public static final int DIRECTION_RIGHT_TO_LEFT = 1;
    public static final int DIRECTION_UP_TO_DOWN = 2;
    public static final int DIRECTION_DOWN_TO_UP = 3;

    private LinearLayout mChildrenContainer;
    private View mRootView;
    private int mTreeDirection;
    private int mLevelInterval;
    private LineDrawer mLineDrawer;
    private Paint mPaint;
    private Rect mStartRect;
    private Rect mEndRect;

    private static class TreeViewException extends RuntimeException{
        public TreeViewException(String message) {
            super(message);
        }
    }

    public static abstract class LineDrawer {
        protected abstract void onDrawLine(Canvas canvas, Paint paint,Rect start,Rect end,int direction);
    }

    public TreeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public TreeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public TreeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TreeView);
        mTreeDirection = typedArray.getInt(R.styleable.TreeView_treeDirection,0);
        mLevelInterval = typedArray.getDimensionPixelSize(R.styleable.TreeView_levelInterval,0);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mStartRect = new Rect();
        mEndRect = new Rect();
    }

    private void reputChildren(){
        if(getChildCount() <= 0){
            return;
        }

        mChildrenContainer = new LinearLayout(getContext());

        LayoutParams layoutParams;
        if(getOrientation() == HORIZONTAL){
            layoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mChildrenContainer.setOrientation(VERTICAL);
        }else{
            layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            mChildrenContainer.setOrientation(HORIZONTAL);
        }

        mChildrenContainer.setLayoutParams(layoutParams);

        List<View> children = new ArrayList<>();

        for(int i = 0;i < getChildCount();i++){
            children.add(getChildAt(i));
        }

        removeAllViews();

        mRootView = children.remove(0);

        for(int i = 0;i < children.size();i++) {
            View view = children.get(i);

            mChildrenContainer.addView(view);

            LayoutParams params = (LayoutParams) view.getLayoutParams();

            switch(mTreeDirection){
                case DIRECTION_LEFT_TO_RIGHT:
                    params.gravity = Gravity.LEFT;
                    break;

                case DIRECTION_RIGHT_TO_LEFT:
                    params.gravity = Gravity.RIGHT;
                    break;

                case DIRECTION_UP_TO_DOWN:
                    params.gravity = Gravity.TOP;
                    break;

                case DIRECTION_DOWN_TO_UP:
                    params.gravity = Gravity.BOTTOM;
                    break;
            }
        }

        addView(mRootView);
        addView(mChildrenContainer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        reputChildren();
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        if(getOrientation() == HORIZONTAL){
            onMeasureHorizontal(widthMeasureSpec,heightMeasureSpec);
        }else{
            onMeasureVertical(widthMeasureSpec,heightMeasureSpec);
        }
    }

    private void onMeasureHorizontal(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        MarginLayoutParams rootMarginParams = (MarginLayoutParams) mRootView.getLayoutParams();

        int wrapWidth = mRootView.getMeasuredWidth() +
                mChildrenContainer.getMeasuredWidth() +
                rootMarginParams.leftMargin + rootMarginParams.rightMargin + mLevelInterval;

        int wrapHeight = Math.max(mRootView.getMeasuredHeight() + rootMarginParams.topMargin + rootMarginParams.bottomMargin,
                mChildrenContainer.getMeasuredHeight());

        if(heightMode == MeasureSpec.AT_MOST){
            height = wrapHeight;
        }

        if(widthMode == MeasureSpec.AT_MOST){
            width = wrapWidth;
        }

        Log.d(TAG,this.toString() + "height" + height);
        Log.d(TAG,this.toString() + "contentheight" + mChildrenContainer.getMeasuredHeight());
        Log.d(TAG,this.toString() + "width" + width);

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
    }

    private void onMeasureVertical(int widthMeasureSpec, int heightMeasureSpec){

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        if(getOrientation() == HORIZONTAL){
            onLayoutHorizontal(changed, l, t, r, b);
        }else{
            onLayoutVertical(changed, l, t, r, b);
        }
    }

    private void onLayoutHorizontal(boolean changed, int l, int t, int r, int b){
        switch(mTreeDirection){
            case DIRECTION_LEFT_TO_RIGHT:
                 onLayoutLR(changed, l, t, r, b);
                 break;

            case DIRECTION_RIGHT_TO_LEFT:
                 onLayoutRL(changed, l, t, r, b);
                 break;

             default:
                 throw new TreeViewException("direction is incorrect");
        }
    }

    private void onLayoutLR(boolean changed, int l, int t, int r, int b){
        MarginLayoutParams rootMarginParams = (MarginLayoutParams) mRootView.getLayoutParams();

        int height = getMeasuredHeight();
        int rootHeight = mRootView.getMeasuredHeight();
        int rootTop = height / 2 - rootHeight / 2;
        int rootLeft = rootMarginParams.leftMargin;
        int rootRight = rootLeft + mRootView.getMeasuredWidth();

        int containerHeight = mChildrenContainer.getMeasuredHeight();
        int containerLeft = rootRight + rootMarginParams.rightMargin + mLevelInterval;
        int containerTop = height / 2 - containerHeight / 2;
        int containerRight = containerLeft + mChildrenContainer.getMeasuredWidth();

        mRootView.layout(rootLeft,
                rootTop,
                rootRight,
                rootTop + rootHeight);



        mChildrenContainer.layout(containerLeft,
                containerTop,
                containerRight,
                containerTop + mChildrenContainer.getMeasuredHeight());
    }

    private void onLayoutRL(boolean changed, int l, int t, int r, int b){
        MarginLayoutParams rootMarginParams = (MarginLayoutParams) mRootView.getLayoutParams();

        int height = getMeasuredHeight();
        int containerHeight = mChildrenContainer.getMeasuredHeight();
        int containerTop = t + height / 2 - containerHeight / 2;
        int containerRight = l + mChildrenContainer.getMeasuredWidth();

        mChildrenContainer.layout(l,
                containerTop,
                containerRight,
                containerTop + mChildrenContainer.getMeasuredHeight());

        int rootHeight = mRootView.getMeasuredHeight();
        int rootTop = t + height / 2 - rootHeight / 2;
        int rootLeft = containerRight + rootMarginParams.leftMargin + mLevelInterval;

        mRootView.layout(rootLeft,
                rootTop,
                rootLeft + mRootView.getMeasuredWidth(),
                rootTop + mRootView.getMeasuredHeight());
    }

    private void onLayoutVertical(boolean changed, int l, int t, int r, int b){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mLineDrawer != null){
            onDrawLine(canvas);
        }
    }

    private void onDrawLine(Canvas canvas) {
        if(getOrientation() == HORIZONTAL){
            onDrawLineHorizontal(canvas);
        }else{
            onDrawLineVertical(canvas);
        }
    }

    private void onDrawLineHorizontal(Canvas canvas){
        mStartRect.left = mRootView.getLeft();
        mStartRect.right = mRootView.getRight();
        mStartRect.top = mRootView.getTop();
        mStartRect.bottom = mRootView.getBottom();

        LinearLayout childContainer = (LinearLayout) mChildrenContainer.getChildAt(0);

        int containerLeft = mChildrenContainer.getLeft();
        int containerTop = mChildrenContainer.getTop();

        for(int i = 0;i < childContainer.getChildCount();i++){
            View child = childContainer.getChildAt(i);

            mEndRect.left = containerLeft + child.getLeft();
            mEndRect.right = containerLeft + child.getRight();
            mEndRect.top = containerTop + child.getTop();
            mEndRect.bottom = containerTop + child.getBottom();

            mLineDrawer.onDrawLine(canvas,mPaint,mStartRect,mEndRect,mTreeDirection);
        }
    }

    private void onDrawLineVertical(Canvas canvas){

    }

    public void setLineDrawer(LineDrawer lineDrawer){
        mLineDrawer = lineDrawer;
        invalidate();
    }
}
