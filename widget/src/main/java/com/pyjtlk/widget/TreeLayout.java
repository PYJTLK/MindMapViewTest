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

public class TreeLayout extends ViewGroup {
    /**
     * 树的方向：从左到右
     */
    public static final int DIRECTION_LEFT_TO_RIGHT = 0;

    /**
     * 树的方向：从右到左
     */
    public static final int DIRECTION_RIGHT_TO_LEFT = 1;

    /**
     * 树的方向：从上到下
     */
    public static final int DIRECTION_UP_TO_DOWN = 2;

    /**
     * 树的方向：从下到上
     */
    public static final int DIRECTION_DOWN_TO_UP = 3;

    private static final int BRENCH_INFINATE = -1;

    private int mWrapWidth;
    private int mWrapHeight;
    private NodeDecoratorDrawer mDecoratorDrawer;
    private Paint mPaint;
    private Rect mStartRect;
    private Rect mEndRect;
    private int mLevelInterval;
    private int mTreeDirection;
    private boolean mLocked;
    private float mLastX;
    private float mLastY;
    private boolean mMovePrepared;
    private int mMaxBranch;
    private Rect mPaddingClipRect;
    private float mContentScaleX;
    private float mContentScaleY;

    /**
     * TreeLayout专用的外间距参数类，用于记录布局参数
     */
    public static class LayoutParams extends MarginLayoutParams{
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /**
     * 点缀绘制器，TreeLayout通过这个类的子类来绘制点缀内容
     */
    public static abstract class NodeDecoratorDrawer {
        private NodeDecoratorDrawer mSourceDector;

        public NodeDecoratorDrawer(NodeDecoratorDrawer sourceDector){
            mSourceDector = sourceDector;
        }

        /**
         * 绘制自身树布局的点缀
         * @param canvas 绘制点缀的画布
         * @param paint 绘制点缀的画笔
         * @param start 点缀的起点控件的区域，即父结点控件所在区域
         * @param end 点缀的终点控件的区域，即子结点控件所在区域
         * @param direction 树的方向
         *                  参考{@link #DIRECTION_LEFT_TO_RIGHT,
         *                       @link #DIRECTION_RIGHT_TO_LEFT,
         *                       @link #DIRECTION_UP_TO_DOWN,
         *                       @link #DIRECTION_DOWN_TO_UP}
         */
        protected abstract void onDrawDecorator(Canvas canvas, Paint paint, Rect start, Rect end, int direction);

        /**
         * 绘制自身树布局的点缀
         * @param canvas 绘制点缀的画布
         * @param paint 绘制点缀的画笔
         * @param start 点缀的起点控件的区域，即父结点控件所在区域
         * @param end 点缀的终点控件的区域，即子结点控件所在区域
         * @param startView 点缀的起点控件
         * @param endView 点缀的终点控件
         * @param direction 树的方向
         */
        public void drawDecorator(Canvas canvas, Paint paint, Rect start, Rect end,View startView,View endView,int direction){
            if(mSourceDector != null){
                mSourceDector.drawDecorator(canvas,paint,start,end,startView,endView,direction);
            }

            if(skipThisDraw(startView,endView)){
                return;
            }

            onDrawDecorator(canvas,paint,start,end,direction);
        }

        /**
         * 跳过本树布局的点缀绘制
         * @param startView 点缀的起点控件
         * @param endView 点缀的终点控件
         * @return 是否跳过本树布局的点缀绘制
         */
        public boolean skipThisDraw(View startView,View endView){
            return false;
        }
    }

    private static class TreeException extends RuntimeException{
        public TreeException(String message) {
            super(message);
        }
    }

    public TreeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TreeView);
        mTreeDirection = typedArray.getInt(R.styleable.TreeView_treeDirection,0);
        mLevelInterval = typedArray.getDimensionPixelSize(R.styleable.TreeView_levelInterval,0);
        mLocked = typedArray.getBoolean(R.styleable.TreeView_locked,true);
        mMaxBranch = typedArray.getInt(R.styleable.TreeView_branch,BRENCH_INFINATE);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mStartRect = new Rect();
        mEndRect = new Rect();
        mPaddingClipRect = new Rect();

        mContentScaleX = 1f;
        mContentScaleY = 1f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkBranch();

        measureChildren(widthMeasureSpec,heightMeasureSpec);

       if(mTreeDirection == DIRECTION_LEFT_TO_RIGHT || mTreeDirection == DIRECTION_RIGHT_TO_LEFT){
           measureHorizontal(widthMeasureSpec,heightMeasureSpec);
       }else{
           measureVertical(widthMeasureSpec,heightMeasureSpec);
       }
    }

    /**
     * 测量水平方向树的尺寸
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
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

    /**
     * 测量水平树的默认高度
     * @return wrap_content下的布局高度
     */
    protected int measureWrapContentHeightHorizontal(){
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

    /**
     * 测量水平树的默认宽度
     * @return wrap_content下的布局宽度
     */
    protected int measureWrapContentWidthHorizontal(){
        int childCount = getChildCount();

        if(childCount <= 0){
            return 0;
        }

        View root = getChildAt(0);
        int wrapWidth = 0;

        LayoutParams rootLayoutParams = (LayoutParams) root.getLayoutParams();
        wrapWidth += (rootLayoutParams.leftMargin + rootLayoutParams.rightMargin + root.getMeasuredWidth());

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

    /**
     * 测量竖直方向树的尺寸
     */
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

    /**
     * 测量竖直方向树的默认宽度
     * @return wrap_content下的布局宽度
     */
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

    /**
     * 测量竖直方向树的默认高度
     * @return wrap_content下的布局高度
     */
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

    /**
     * 从上到下摆放子控件
     */
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

    /**
     * 从下到上摆放子控件
     */
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

    /**
     * 从左到右摆放子控件
     */
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

    /**
     * 从右到左摆放子控件
     */
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

    /**
     * 绘制结点的装饰
     * @param canvas 绘制的画布
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
            mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,root,child,mTreeDirection);
        }

        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        onDrawDecorator(canvas);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    private void checkBranch(){
        if(mMaxBranch == BRENCH_INFINATE){
            return;
        }

        int branchCount = getChildCount() - 1;

        if(branchCount > mMaxBranch){
            throw new TreeException("this is a " + mMaxBranch + " fork tree,however it's count of branch is " + branchCount);
        }
    }

    /**
     * 设置点缀绘制器
     * @param decortator 点缀绘制器
     */
    public void setDecorDrawer(NodeDecoratorDrawer decortator){
        mDecoratorDrawer = decortator;
        requestLayout();
        invalidate();
    }

    /**
     * LevelInterval是指父结点与子节点的（水平或竖直）间距
     * @return 间距值
     */
    public int getLevelInterval() {
        return mLevelInterval;
    }

    /**
     * 设置父结点与子节点的（水平或竖直）间距，改变此间距值
     * @param levelInterval 间距值
     */
    public void setLevelInterval(int levelInterval) {
        this.mLevelInterval = levelInterval;
        requestLayout();
        invalidate();
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
     * 本布局是否锁定，解锁状态下布局可以拖拽
     * @return 是否锁定
     */
    public boolean isLocked(){
       return mLocked;
    }

    /**
     * 锁定布局后无法拖拽
     * @param lock 是否锁定
     */
    public void lockTree(boolean lock){
        mLocked = lock;
    }

    public void scaleContent(float scale){
        scaleContentX(scale);
        scaleContentY(scale);
    }

    public void scaleContentX(float scaleX){
        scaleChildrenX(scaleX);
    }

    private void scaleChildrenX(float scaleX){
        mContentScaleX = scaleX;

        if(isTreeHorizontal()){
            mLevelInterval *= scaleX;
        }

        for(int i = 0;i < getChildCount();i++){
            View child = getChildAt(i);
            LayoutParams oldLayoutParams = (LayoutParams) child.getLayoutParams();
            LayoutParams newLayoutParams = new LayoutParams(oldLayoutParams);
            newLayoutParams.leftMargin = (int) (scaleX * oldLayoutParams.leftMargin);
            newLayoutParams.rightMargin = (int) (scaleX * oldLayoutParams.rightMargin);
            newLayoutParams.topMargin = oldLayoutParams.topMargin;
            newLayoutParams.bottomMargin = oldLayoutParams.bottomMargin;
            newLayoutParams.width *= scaleX;
            child.setLayoutParams(newLayoutParams);
        }
    }

    public void scaleContentY(float scaleY){
        scaleChildrenY(scaleY);
    }

    private void scaleChildrenY(float scaleY){
        mContentScaleY = scaleY;

        if(isTreeVertical()){
            mLevelInterval *= scaleY;
        }

        for(int i = 0;i < getChildCount();i++){
            View child = getChildAt(i);
            LayoutParams oldLayoutParams = (LayoutParams) child.getLayoutParams();
            LayoutParams newLayoutParams = new LayoutParams(oldLayoutParams);
            newLayoutParams.leftMargin = oldLayoutParams.leftMargin;
            newLayoutParams.rightMargin = oldLayoutParams.rightMargin;
            newLayoutParams.topMargin = (int) (scaleY * oldLayoutParams.topMargin);
            newLayoutParams.bottomMargin = (int) (scaleY * oldLayoutParams.bottomMargin);
            newLayoutParams.height *= scaleY;
            child.setLayoutParams(newLayoutParams);
        }
    }

    public boolean isTreeHorizontal(){
        return getTreeDirection() == DIRECTION_RIGHT_TO_LEFT || mTreeDirection == DIRECTION_LEFT_TO_RIGHT;
    }

    public boolean isTreeVertical(){
        return !isTreeHorizontal();
    }

    public int getTreeDirection(){
        return mTreeDirection;
    }
}
