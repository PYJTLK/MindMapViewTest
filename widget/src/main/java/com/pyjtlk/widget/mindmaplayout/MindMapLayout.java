package com.pyjtlk.widget.mindmaplayout;

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

import com.pyjtlk.widget.Direction;
import com.pyjtlk.widget.NodeDecoratorDrawer;
import com.pyjtlk.widget.R;
import com.pyjtlk.widget.treelayout.TreeLayout;

public class MindMapLayout extends ViewGroup {
    /**
     * 思维导图方向：水平方向
     */
    public static final int ORIENTATION_HORIZONTAL = 0;

    /**
     * 思维导图方向：竖直方向
     */
    public static final int ORIENTATION_VERTICAL = 1;

    private TreeLayout mLeftLayout;
    private TreeLayout mRightLayout;
    private TreeLayout mTopLayout;
    private TreeLayout mBottomLayout;
    private View mRootNode;
    private Orientation mOrientation;
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
    private Horizontal mHorizontal;
    private Vertical mVertical;

    private static class MindMapLayoutException extends RuntimeException{
        public MindMapLayoutException(String message) {
            super(message);
        }
    }

    private static abstract class Orientation{
        protected abstract void init();

        protected abstract void onMeasure(int widthMeasureSpec, int heightMeasureSpec);

        protected abstract void onLayout(boolean changed, int l, int t, int r, int b);

        protected abstract void onDrawDecorator(Canvas canvas);

        protected abstract void onOrientationChanged();

        protected abstract void skipDrawDecorator(boolean skip);

        protected abstract void setDecorDrawer(NodeDecoratorDrawer nodeDecoratorDrawer);
    }

    private class Horizontal extends Orientation{
        @Override
        protected void init() {
            mLeftLayout = (TreeLayout) getChildAt(0);
            mRightLayout = (TreeLayout) getChildAt(2);

            mLeftLayout.getRootNode().setVisibility(GONE);
            mLeftLayout.lockTree(true);
            mRightLayout.getRootNode().setVisibility(GONE);
            mRightLayout.lockTree(true);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
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

        private int measureWrapWidthHorizontal(){
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

        private int measureWrapHeightHorizontal(){
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

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b){
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

        @Override
        protected void onDrawDecorator(Canvas canvas){
            mStartRect.left = mRootNode.getLeft();
            mStartRect.right = mRootNode.getRight();
            mStartRect.top = mRootNode.getTop();
            mStartRect.bottom = mRootNode.getBottom();

            mEndRect.left = mStartRect.right;
            mEndRect.right = mStartRect.left;
            mEndRect.top = mStartRect.top;
            mEndRect.bottom = mStartRect.bottom;
            mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,mRootNode,mRootNode, Direction.DIRECTION_RIGHT_TO_LEFT);

            mEndRect.left = mStartRect.right;
            mEndRect.right = mStartRect.left;
            mEndRect.top = mStartRect.top;
            mEndRect.bottom = mStartRect.bottom;
            mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,mRootNode,mRootNode,Direction.DIRECTION_LEFT_TO_RIGHT);
        }

        @Override
        protected void onOrientationChanged() {
            if(mLeftLayout == null && mRightLayout == null){
                mLeftLayout = mTopLayout;
                mRightLayout = mBottomLayout;
            }

            mTopLayout = null;
            mBottomLayout = null;

            mLeftLayout.setUnionTreeDirection(Direction.DIRECTION_RIGHT_TO_LEFT);
            mRightLayout.setUnionTreeDirection(Direction.DIRECTION_LEFT_TO_RIGHT);
        }

        @Override
        protected void skipDrawDecorator(boolean skip) {
            mLeftLayout.skipUnionDrawDecorator(skip);
            mRightLayout.skipUnionDrawDecorator(skip);
        }

        @Override
        protected void setDecorDrawer(NodeDecoratorDrawer nodeDecoratorDrawer) {
            mDecoratorDrawer = nodeDecoratorDrawer;
            mSkipDrawDecorator = false;
            mLeftLayout.setUnionDecorDrawer(nodeDecoratorDrawer);
            mRightLayout.setUnionDecorDrawer(nodeDecoratorDrawer);
        }
    }

    private class Vertical extends Orientation{
        @Override
        protected void init() {
            mTopLayout = (TreeLayout) getChildAt(0);
            mBottomLayout = (TreeLayout) getChildAt(2);

            mTopLayout.getRootNode().setVisibility(GONE);
            mTopLayout.lockTree(true);
            mBottomLayout.getRootNode().setVisibility(GONE);
            mBottomLayout.lockTree(true);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
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

        private int measureWrapHeightVertical(){
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

        private int measureWrapWidthVertical(){
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
        protected void onLayout(boolean changed, int l, int t, int r, int b){
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

        @Override
        protected void onDrawDecorator(Canvas canvas){
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
        protected void onOrientationChanged() {
            if(mTopLayout == null && mBottomLayout == null){
                mTopLayout = mLeftLayout;
                mBottomLayout = mRightLayout;
            }

            mLeftLayout = null;
            mRightLayout = null;

            mTopLayout.setUnionTreeDirection(Direction.DIRECTION_DOWN_TO_UP);
            mBottomLayout.setUnionTreeDirection(Direction.DIRECTION_UP_TO_DOWN);
        }

        @Override
        protected void skipDrawDecorator(boolean skip) {
            mTopLayout.skipUnionDrawDecorator(skip);
            mBottomLayout.skipUnionDrawDecorator(skip);
        }

        @Override
        protected void setDecorDrawer(NodeDecoratorDrawer nodeDecoratorDrawer) {
            mDecoratorDrawer = nodeDecoratorDrawer;
            mSkipDrawDecorator = false;
            mTopLayout.setUnionDecorDrawer(nodeDecoratorDrawer);
            mBottomLayout.setUnionDecorDrawer(nodeDecoratorDrawer);
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MindMapLayout);
        int orientation = typedArray.getInt(R.styleable.MindMapLayout_orientation,0);
        mLocked = typedArray.getBoolean(R.styleable.MindMapLayout_mapLocked,true);
        typedArray.recycle();

        mSkipDrawDecorator = true;

        mPaddingClipRect = new Rect();
        mStartRect = new Rect();
        mEndRect = new Rect();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mHorizontal = new Horizontal();
        mVertical = new Vertical();

        setOrientation(orientation);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initChildren(widthMeasureSpec,heightMeasureSpec);

        mOrientation.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mOrientation.onLayout(changed,l,t,r,b);
    }

    private void initChildren(int widthMeasureSpec,int heightMeasureSpec){
        check();

        if(getOrientation() == ORIENTATION_HORIZONTAL){
            mHorizontal.init();
        }else{
            mVertical.init();
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
     * 设置点缀绘制器，此方法会把左右所有子树都设置为同一种绘制器，详见{@link TreeLayout#setUnionDecorDrawer(NodeDecoratorDrawer)}
     * @param decortator 点缀绘制器
     */
    public void setDecorDrawer(NodeDecoratorDrawer decortator){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mOrientation.setDecorDrawer(decortator);
            }
        });
        requestLayout();
    }

    /**
     * 是否跳过绘制点缀
     * @return 是否跳过绘制点缀
     */
    public boolean isSkipDrawDecorator(){
        return mSkipDrawDecorator;
    }

    /**
     * 跳过绘制点缀，纯布局摆放，没有装饰
     * @param skip 是否跳过绘制点缀
     */
    public void skipDrawDecorator(boolean skip){
        mSkipDrawDecorator = skip;
        mOrientation.skipDrawDecorator(skip);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!mSkipDrawDecorator){
            onDrawDecorator(canvas);
        }
    }

    /**
     * 绘制点缀
     * @param canvas 此布局控件的画布
     */
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

        mOrientation.onDrawDecorator(canvas);

        canvas.restore();
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

    /**
     * 设置思维导图布局的方向
     * @param orientation 方向
     * 水平方向{@link #ORIENTATION_HORIZONTAL}
     * 竖直方向{@link #ORIENTATION_VERTICAL}
     */
    public void setOrientation(int orientation){
        if(orientation == ORIENTATION_HORIZONTAL){
            mOrientation = mHorizontal;
        }else{
            mOrientation = mVertical;
        }

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mOrientation.onOrientationChanged();
            }
        });
        requestLayout();
    }

    /**
     * 获取布局方向
     * @return 布局方向
     */
    public int getOrientation(){
        return mOrientation instanceof Horizontal ? ORIENTATION_HORIZONTAL : ORIENTATION_VERTICAL;
    }

    /**
     * 锁定布局，锁定布局后不会拦截触摸事件。解锁后布局可拖拽
     * @param locked 是否锁定
     */
    public void lockMap(boolean locked){
        mLocked = locked;
    }

    /**
     * 是否锁定布局
     * @return 是否锁定布局
     */
    public boolean isLocked(){
        return mLocked;
    }

    /**
     * 获取思维导图的左子树
     * @return 思维导图的左子树
     */
    public final TreeLayout getLeftTree(){
        return mLeftLayout;
    }

    /**
     * 获取思维导图的右子树
     * @return 思维导图的右子树
     */
    public final TreeLayout getRightTree() {
        return mRightLayout;
    }

    /**
     * 获取思维导图的上子树
     * @return 思维导图的上子树
     */
    public final TreeLayout getTopTree(){
        return mTopLayout;
    }

    /**
     * 获取思维导图的下子树
     * @return 思维导图的下子树
     */
    public final TreeLayout getBottomTree(){
        return mBottomLayout;
    }

    /**
     * 获取思维导图的根结点，思维导图的根结点不属于左右子树
     * @return 思维导图的根结点
     */
    public final View getRootNode(){
        return mRootNode;
    }
}
