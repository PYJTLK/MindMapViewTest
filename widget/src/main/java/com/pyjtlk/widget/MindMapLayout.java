package com.pyjtlk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class MindMapLayout extends ViewGroup {
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    private TreeLayout mLeftLayout;
    private TreeLayout mRightLayout;
    private TreeLayout mTopLayout;
    private TreeLayout mBottomLayout;
    private View mRootNode;
    private int mOrientation;
    private boolean mLocked;
    private int mWrapWidth;
    private int mWrapHeight;
    private boolean mSkipDrawDecorator;
    private NodeDecoratorDrawer mDecoratorDrawer;
    private Rect mPaddingClipRect;
    private Rect mStartRect;
    private Rect mEndRect;
    private Paint mPaint;
    private float mLastX;
    private float mLastY;
    private boolean mMovePrepared;

    private static class MindMapLayoutException extends RuntimeException{
        public MindMapLayoutException(String message) {
            super(message);
        }
    }

    public MindMapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public MindMapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.MindMapLayout);
        mOrientation = typedArray.getInt(R.styleable.MindMapLayout_orientation,0);
        mLocked = typedArray.getBoolean(R.styleable.MindMapLayout_mapLocked,true);
        typedArray.recycle();

        mSkipDrawDecorator = true;

        setClipChildren(false);

        mPaddingClipRect = new Rect();
        mStartRect = new Rect();
        mEndRect = new Rect();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initChildren(widthMeasureSpec,heightMeasureSpec);

        if(mOrientation == ORIENTATION_HORIZONTAL){
            onMeasureHorizontal(widthMeasureSpec,heightMeasureSpec);
        }else{
            onMeasureVertical(widthMeasureSpec,heightMeasureSpec);
        }
    }

    protected void onMeasureHorizontal(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mWrapWidth = measureWrapWidthHorizontal();
        mWrapHeight = measureWrapHeightHorizontal();

        if(widthMode == MeasureSpec.AT_MOST){
            width = mWrapWidth;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            height = mWrapHeight;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
    }

    protected int measureWrapWidthHorizontal(){
        int wrapWidth = 0;

        for(int i = 0;i < getChildCount();i++){
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            wrapWidth += child.getMeasuredWidth();
        }

        return wrapWidth;
    }

    protected int measureWrapHeightHorizontal(){
        int wrapHeight = 0;
        int height;

        for(int i = 0;i < getChildCount();i++){
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            height = child.getMeasuredHeight();
            wrapHeight = Math.max(wrapHeight,height);
        }

        return wrapHeight;
    }

    protected void onMeasureVertical(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mWrapWidth = measureWrapWidthVertical();
        mWrapHeight = measureWrapHeightVertical();

        if(widthMode == MeasureSpec.AT_MOST){
            width = mWrapWidth;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            height = mWrapHeight;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,widthMode),MeasureSpec.makeMeasureSpec(height,heightMode));
    }

    protected int measureWrapHeightVertical(){
        int wrapHeight = 0;

        for(int i = 0;i < getChildCount();i++){
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            wrapHeight += child.getMeasuredHeight();
        }

        return wrapHeight;
    }

    protected int measureWrapWidthVertical(){
        int wrapWidth = 0;
        int width;

        for(int i = 0;i < getChildCount();i++){
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            width = child.getMeasuredWidth();
            wrapWidth = Math.max(wrapWidth,width);
        }

        return wrapWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mOrientation == ORIENTATION_HORIZONTAL){
            onLayoutHorizontal(changed,l,t,r,b);
        }else{
            onLayoutVertical(changed,l,t,r,b);
        }
    }

    protected void onLayoutHorizontal(boolean changed, int l, int t, int r, int b){
        int left = 0;
        int right = mLeftLayout.getMeasuredWidth();
        int top = (mWrapHeight - mLeftLayout.getMeasuredHeight()) / 2;
        mLeftLayout.layout(left,
                top,
                right,
                top + mLeftLayout.getMeasuredHeight());

        left = right;
        right += mRootNode.getMeasuredWidth();
        top = (mWrapHeight - mRootNode.getMeasuredHeight()) / 2;
        mRootNode.layout(left,
                top,
                right,
                top + mRootNode.getMeasuredHeight());

        left = right;
        right += mRightLayout.getMeasuredWidth();
        top = (mWrapHeight - mRightLayout.getMeasuredHeight()) / 2;
        mRightLayout.layout(left,
                top,
                right,
                top + mRightLayout.getMeasuredHeight());
    }

    protected void onLayoutVertical(boolean changed, int l, int t, int r, int b){
        int left = (mWrapWidth - mTopLayout.getMeasuredWidth()) / 2;
        int top = 0;
        int bottom = top + mTopLayout.getMeasuredHeight();
        mTopLayout.layout(left,
                top,
                left + mTopLayout.getMeasuredWidth(),
                bottom);

        top = bottom;
        bottom += mRootNode.getMeasuredHeight();
        left = (mWrapWidth - mRootNode.getMeasuredWidth()) / 2;
        mRootNode.layout(left,
                top,
                left + mRootNode.getMeasuredWidth(),
                bottom);

        top = bottom;
        bottom += mBottomLayout.getMeasuredHeight();
        left = (mWrapWidth - mBottomLayout.getMeasuredWidth()) / 2;
        mBottomLayout.layout(left,
                top,
                left + mBottomLayout.getMeasuredWidth(),
                bottom);
    }

    private void initChildren(int widthMeasureSpec,int heightMeasureSpec){
        check();

        if(mOrientation == ORIENTATION_HORIZONTAL){
            mLeftLayout = (TreeLayout) getChildAt(0);
            mRightLayout = (TreeLayout) getChildAt(2);

            mLeftLayout.getRootNode().setVisibility(GONE);
            mLeftLayout.lockTree(true);
            mRightLayout.getRootNode().setVisibility(GONE);
            mRightLayout.lockTree(true);
        }else{
            mTopLayout = (TreeLayout) getChildAt(0);
            mBottomLayout = (TreeLayout) getChildAt(2);

            mTopLayout.getRootNode().setVisibility(GONE);
            mTopLayout.lockTree(true);
            mBottomLayout.getRootNode().setVisibility(GONE);
            mBottomLayout.lockTree(true);
        }

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        mRootNode = getChildAt(1);
    }

    private void check(){
        int childCount = getChildCount();
        if(childCount > 3){
            throw new MindMapLayoutException("invalid child View count:" + childCount + " ,the valid child count is 3");
        }

        checkHorizontal();

        checkVertical();
    }

    private void checkHorizontal(){
        View leftTree = getChildAt(0);
        View rightTree = getChildAt(2);

        if(!(leftTree instanceof TreeLayout)){
            throw new MindMapLayoutException("the left branch must be a " + TreeLayout.class.getCanonicalName());
        }

        if(!(rightTree instanceof TreeLayout)){
            throw new MindMapLayoutException("the right branch must be a " + TreeLayout.class.getCanonicalName());
        }
    }

    private void checkVertical(){
        View topTree = getChildAt(0);
        View bottomTree = getChildAt(2);

        if(!(topTree instanceof TreeLayout)){
            throw new MindMapLayoutException("the top branch must be a " + TreeLayout.class.getCanonicalName());
        }

        if(!(bottomTree instanceof TreeLayout)){
            throw new MindMapLayoutException("the bottom branch must be a " + TreeLayout.class.getCanonicalName());
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    /**
     * 设置点缀绘制器
     * @param decortator 点缀绘制器
     */
    public void setDecorDrawer(NodeDecoratorDrawer decortator){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(mOrientation == ORIENTATION_HORIZONTAL){
                    setDecorDrawerHorizontal(decortator);
                }else{
                    setDecorDrawerVertical(decortator);
                }
            }
        });
        requestLayout();
    }

    protected void setDecorDrawerHorizontal(NodeDecoratorDrawer decortator){
        mDecoratorDrawer = decortator;
        mSkipDrawDecorator = false;
        mLeftLayout.setUnionDecorDrawer(decortator);
        mRightLayout.setUnionDecorDrawer(decortator);
    }

    protected void setDecorDrawerVertical(NodeDecoratorDrawer decortator){
        mDecoratorDrawer = decortator;
        mSkipDrawDecorator = false;
        mTopLayout.setUnionDecorDrawer(decortator);
        mBottomLayout.setUnionDecorDrawer(decortator);
    }

    public void skipDrawDecorator(boolean skip){
        mSkipDrawDecorator = skip;
        mLeftLayout.skipDrawDecorator(skip);
        mRightLayout.skipDrawDecorator(skip);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!mSkipDrawDecorator){
            onDrawDecorator(canvas);
        }
    }

    protected void onDrawDecorator(Canvas canvas){
        if(mDecoratorDrawer == null){
            return;
        }

        canvas.save();

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int scrollX = getScrollX();
        int scrollY = getScrollY();

        mPaddingClipRect.left = scrollX + paddingLeft;
        mPaddingClipRect.top = scrollY + paddingTop;
        mPaddingClipRect.right = scrollX + getWidth() - paddingRight;
        mPaddingClipRect.bottom = scrollY + getHeight() - paddingBottom;

        canvas.clipRect(mPaddingClipRect);

        if(mOrientation == ORIENTATION_HORIZONTAL){
            onDrawDecoratorHorizontal(canvas);
        }else{
            onDrawDecoratorVertical(canvas);
        }

        canvas.restore();
    }

    protected void onDrawDecoratorHorizontal(Canvas canvas){
        mStartRect.left = mRootNode.getLeft();
        mStartRect.right = mRootNode.getRight();
        mStartRect.top = mRootNode.getTop();
        mStartRect.bottom = mRootNode.getBottom();

        mEndRect.left = mStartRect.right;
        mEndRect.right = mStartRect.left;
        mEndRect.top = mStartRect.top;
        mEndRect.bottom = mStartRect.bottom;
        mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,mRootNode,mRootNode,Direction.DIRECTION_RIGHT_TO_LEFT);

        mEndRect.left = mStartRect.right;
        mEndRect.right = mStartRect.left;
        mEndRect.top = mStartRect.top;
        mEndRect.bottom = mStartRect.bottom;
        mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,mRootNode,mRootNode,Direction.DIRECTION_LEFT_TO_RIGHT);
    }

    protected void onDrawDecoratorVertical(Canvas canvas){
        mStartRect.left = mRootNode.getLeft();
        mStartRect.right = mRootNode.getRight();
        mStartRect.top = mRootNode.getTop();
        mStartRect.bottom = mRootNode.getBottom();

        mEndRect.left = mStartRect.left;
        mEndRect.right = mStartRect.right;
        mEndRect.top = mStartRect.top;
        mEndRect.bottom = mStartRect.top;
        mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,mRootNode,mRootNode,Direction.DIRECTION_DOWN_TO_UP);

        mEndRect.left = mStartRect.left;
        mEndRect.right = mStartRect.right;
        mEndRect.top = mStartRect.bottom;
        mEndRect.bottom = mStartRect.bottom;
        mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,mRootNode,mRootNode,Direction.DIRECTION_UP_TO_DOWN);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float currentX = event.getX();
        float currentY = event.getY();
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

    public void setOrientation(int orientation){
        if(mOrientation == orientation) {
            return;
        }

        if(mOrientation == ORIENTATION_HORIZONTAL){
            mOrientation = ORIENTATION_VERTICAL;
            mTopLayout = mLeftLayout;
            mBottomLayout = mRightLayout;
            mTopLayout.setUnionTreeDirection(Direction.DIRECTION_DOWN_TO_UP);
            mBottomLayout.setUnionTreeDirection(Direction.DIRECTION_UP_TO_DOWN);
        }else{
            mOrientation = ORIENTATION_HORIZONTAL;
            mLeftLayout = mTopLayout;
            mRightLayout = mBottomLayout;
            mLeftLayout.setUnionTreeDirection(Direction.DIRECTION_RIGHT_TO_LEFT);
            mRightLayout.setUnionTreeDirection(Direction.DIRECTION_LEFT_TO_RIGHT);
        }
        requestLayout();
    }

    public int getOrientation(){
        return mOrientation;
    }

    public void lockMap(boolean locked){
        mLocked = locked;
    }

    public boolean isLocked(){
        return mLocked;
    }

    public final TreeLayout getLeftTree(){
        return mLeftLayout;
    }

    public final TreeLayout getRightTree() {
        return mRightLayout;
    }

    public final TreeLayout getTopTree(){
        return mTopLayout;
    }

    public final TreeLayout getBottomTree(){
        return mBottomLayout;
    }

    public final View getRootNode(){
        return mRootNode;
    }
}
