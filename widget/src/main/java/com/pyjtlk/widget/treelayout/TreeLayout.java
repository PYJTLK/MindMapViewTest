package com.pyjtlk.widget.treelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.pyjtlk.container.tree.Node;
import com.pyjtlk.container.tree.NodeSearcher;
import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.widget.Direction;
import com.pyjtlk.widget.NodeDecoratorDrawer;
import com.pyjtlk.widget.R;
import com.pyjtlk.widget.SearchListener;

import java.util.LinkedList;
import java.util.List;

public class TreeLayout extends ViewGroup {
    private static final int BRENCH_INFINATE = -1;

    private int mWrapWidth;
    private int mWrapHeight;
    private NodeDecoratorDrawer mDecoratorDrawer;
    private Paint mPaint;
    private Rect mStartRect;
    private Rect mEndRect;
    private int mLevelInterval;
    private Direction mTreeDirection;
    private boolean mLocked;
    private float mLastX;
    private float mLastY;
    private boolean mMovePrepared;
    private int mMaxBranch;
    private Rect mPaddingClipRect;
    private boolean mSkipDrawDecorator;

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

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }

    private static class TreeException extends RuntimeException{
        public TreeException(String message) {
            super(message);
        }
    }

    /**
     * 树的全局参数设置
     */
    public static class TreeGlobalParams{
        public int levelInterval;
        public Direction direction;
        public boolean locked;
        public NodeDecoratorDrawer nodeDecoratorDrawer;
        public boolean skipDrawDecor;

        public TreeGlobalParams(){
        }

        public TreeGlobalParams(TreeLayout treeLayout){
            levelInterval = treeLayout.getLevelInterval();
            direction = new Direction();
            direction.direction = treeLayout.getTreeDirection().direction;
            locked = treeLayout.isLocked();
            nodeDecoratorDrawer = treeLayout.getDecoratorDrawer();
            skipDrawDecor = treeLayout.isSkipDrawDecor();
        }

        public TreeGlobalParams(TreeGlobalParams sourceParams){
            levelInterval = sourceParams.levelInterval;
            direction = new Direction();
            direction.direction = sourceParams.direction.direction;
            locked = sourceParams.locked;
            nodeDecoratorDrawer = sourceParams.nodeDecoratorDrawer;
            skipDrawDecor = sourceParams.skipDrawDecor;
        }
    }

    /**
     * 子控件加载器
     * @param <D> 数据类型
     */
    public abstract static class TreeContentLoader<D>{
        /**
         * 初始化子控件
         * @param contentView 子控件
         * @param data 结点数据
         */
        public abstract void onInitContent(View contentView, D data);

        /**
         * 获取子控件所对应的布局id
         * @return
         */
        public abstract int getLayoutId();
    }

    private static class InnerTreeContentLoader<D>{
        protected Tree<D> mDataTree;
        protected TreeLayout mTreeLayout;
        private TreeContentLoader<D> mTreeContentLoader;

        InnerTreeContentLoader(TreeLayout treeLayout, Tree<D> dataTree, TreeContentLoader treeContentLoader) {
            mDataTree = dataTree;
            mTreeLayout = treeLayout;
            mTreeContentLoader = treeContentLoader;
        }

        View getContentView(LayoutInflater layoutInflater, ViewGroup parentView){
            return layoutInflater.inflate(mTreeContentLoader.getLayoutId(),parentView,false);
        }

        void loadViewsFromData(){
            List<TreeLayout> queue = new LinkedList<>();

            mDataTree.bfs(new NodeSearcher<D>() {
                @Override
                public boolean onNode(Tree<D> parent, Tree<D> thisTree) {
                    if(!thisTree.isLeaf()){
                        TreeLayout treeLayout;

                        if(queue.isEmpty()){
                            treeLayout = mTreeLayout;
                        }else{
                            treeLayout = queue.remove(0);
                        }

                        Context context = mTreeLayout.getContext();

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View rootNodeView = getContentView(layoutInflater,treeLayout);
                        TreeLayout.LayoutParams noMarginLayoutParams = (TreeLayout.LayoutParams) rootNodeView.getLayoutParams();
                        noMarginLayoutParams.setMargins(0,0,0,0);
                        rootNodeView.setLayoutParams(noMarginLayoutParams);
                        mTreeContentLoader.onInitContent(rootNodeView,thisTree.getRootData());
                        treeLayout.addView(rootNodeView);

                        List<Tree<D>> childrenNode = thisTree.getChildren();

                        for(int i = 0;i < childrenNode.size();i++){
                            Tree<D> childNode = childrenNode.get(i);
                            if(childNode.isLeaf()){
                                View childNodeView = getContentView(layoutInflater,treeLayout);
                                mTreeContentLoader.onInitContent(childNodeView,childNode.getRootData());
                                treeLayout.addView(childNodeView);
                            }else{
                                TreeLayout subTreeLayout = new TreeLayout(context);
                                loadWrapLayoutParams(subTreeLayout);
                                treeLayout.addView(subTreeLayout);
                                queue.add(subTreeLayout);
                            }
                        }
                    }
                    return true;
                }
            });

            notifyAllTreeChanged();
        }

        private void notifyAllTreeChanged(){
            notifyAllTreeChanged(mTreeLayout.getGlobalParams());
        }

        private void notifyAllTreeChanged(TreeLayout.TreeGlobalParams treeGlobalParams){
            SearchListener searchListener = new SearchListener() {
                @Override
                public boolean onLeafNode(View thisNode, View parentNode) {
                    return true;
                }

                @Override
                public boolean onRootNode(View thisNode, View parentNode) {
                    return true;
                }

                @Override
                public void onTreeStart(TreeLayout treeLayout) {
                    treeLayout.setTreeDirection(treeGlobalParams.direction.direction);
                    treeLayout.setLevelInterval(treeGlobalParams.levelInterval);
                    treeLayout.setDecorDrawer(treeGlobalParams.nodeDecoratorDrawer);
                    treeLayout.lockTree(true);
                }
            };

            mTreeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mTreeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTreeLayout.bfs(searchListener);
                }
            });
        }

        void loadWrapLayoutParams(View view){
            TreeLayout.LayoutParams layoutParams = new TreeLayout.LayoutParams(TreeLayout.LayoutParams.WRAP_CONTENT,
                    TreeLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }
    }

    public TreeLayout(Context context){
        super(context);
        init(context,null);
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
        int treeDirection;

        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TreeLayout);
            treeDirection = typedArray.getInt(R.styleable.TreeLayout_treeDirection, 0);
            mLevelInterval = typedArray.getDimensionPixelSize(R.styleable.TreeLayout_levelInterval, 0);
            mLocked = typedArray.getBoolean(R.styleable.TreeLayout_locked, true);
            mMaxBranch = typedArray.getInt(R.styleable.TreeLayout_branch, BRENCH_INFINATE);
            typedArray.recycle();
        }else{
            treeDirection = 0;
            mLevelInterval = 0;
            mLocked = true;
            mMaxBranch = BRENCH_INFINATE;
        }

        mTreeDirection = new Direction();
        mTreeDirection.direction = treeDirection;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mStartRect = new Rect();
        mEndRect = new Rect();
        mPaddingClipRect = new Rect();

        mSkipDrawDecorator = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkBranch();

        measureChildren(widthMeasureSpec,heightMeasureSpec);

       if(Direction.isHorizontal(mTreeDirection)){
           measureHorizontal(widthMeasureSpec,heightMeasureSpec);
       }else{
           measureVertical(widthMeasureSpec,heightMeasureSpec);
       }
    }

    /**
     * 测量水平方向树的尺寸
     * @param widthMeasureSpec 宽度尺寸
     * @param heightMeasureSpec 高度尺寸
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
        switch(mTreeDirection.direction){
            case Direction.DIRECTION_LEFT_TO_RIGHT:
                onLayoutLeftToRight(changed,l,t,r,b);
                break;

            case Direction.DIRECTION_RIGHT_TO_LEFT:
                onLayoutRightToLeft(changed,l,t,r,b);
                break;

            case Direction.DIRECTION_UP_TO_DOWN:
                onLayoutUpToDown(changed,l,t,r,b);
                break;

            case Direction.DIRECTION_DOWN_TO_UP:
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


        try{
            canvas.save();

            View root = getChildAt(0);
            if(root == null){
                return;
            }

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
                mDecoratorDrawer.drawDecorator(canvas,mPaint,mStartRect,mEndRect,root,child,mTreeDirection.direction);
            }
        }finally {
            canvas.restore();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!mSkipDrawDecorator) {
            onDrawDecorator(canvas);
        }
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
     * 获取树的点缀绘制器
     * @return 树的点缀绘制器
     */
    public NodeDecoratorDrawer getDecoratorDrawer(){
        return mDecoratorDrawer;
    }

    /**
     * 设置点缀绘制器
     * @param decortator 点缀绘制器
     */
    public void setDecorDrawer(NodeDecoratorDrawer decortator){
        mDecoratorDrawer = decortator;
        mSkipDrawDecorator = false;
        invalidate();
    }

    /**
     * 给所有的子树设置统一的点缀绘制器
     * @param decortator 点缀绘制器
     */
    public void setUnionDecorDrawer(NodeDecoratorDrawer decortator){
        TreeGlobalParams params = getGlobalParams();
        params.nodeDecoratorDrawer = decortator;

        configWholeTree(params);
    }

    /**
     * 设置树的方向，只会作用于本树，不会作用于子树
     * @param direction 方向，详见
     * {@link Direction#DIRECTION_LEFT_TO_RIGHT
     * @link Direction#DIRECTION_RIGHT_TO_LEFT
     * @link Direction#DIRECTION_DOWN_TO_UP
     * @link Direction#DIRECTION_UP_TO_DOWN}
     */
    public void setTreeDirection(int direction){
        mTreeDirection.direction = direction;
        requestLayout();
        invalidate();
    }

    /**
     * 设置树的方向，会作用于本树和所有子树
     * @param direction 方向，详见
     * {@link Direction#DIRECTION_LEFT_TO_RIGHT
     * @link Direction#DIRECTION_RIGHT_TO_LEFT
     * @link Direction#DIRECTION_DOWN_TO_UP
     * @link Direction#DIRECTION_UP_TO_DOWN}
     */
    public void setUnionTreeDirection(int direction){
        TreeGlobalParams params = getGlobalParams();
        params.direction.direction = direction;
        configWholeTree(params);
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

    /**
     * 是否为水平树
     * @return 是否为水平树
     */
    public boolean isTreeHorizontal(){
        return Direction.isHorizontal(mTreeDirection);
    }

    /**
     * 是否为垂直树
     * @return 是否为垂直树
     */
    public boolean isTreeVertical(){
        return !isTreeHorizontal();
    }

    /**
     * 获取树的方向
     * @return 树的方向
     */
    public Direction getTreeDirection(){
        return mTreeDirection;
    }


    /**
     * 深度优先遍历子控件
     * @param searchListener 遍历监听器
     */
    public void dfs(SearchListener searchListener){
        if(searchListener == null){
            return;
        }

        dfsInside(this,null,searchListener);
    }

    protected boolean dfsInside(View thisNode,View parentNode,SearchListener searchListener){
        if(thisNode instanceof TreeLayout){
            TreeLayout treeLayout = (TreeLayout) thisNode;
            View rootNode = treeLayout.getChildAt(0);

            searchListener.onTreeStart((TreeLayout) thisNode);

            if(!searchListener.onRootNode(rootNode,parentNode)){
                return false;
            }

            for(int i = 1;i < treeLayout.getChildCount();i++){
                View childNode = treeLayout.getChildAt(i);
                boolean continueSearch = treeLayout.dfsInside(childNode,rootNode,searchListener);
                if(!continueSearch){
                    return false;
                }
            }
            return true;
        }

        return searchListener.onLeafNode(thisNode,parentNode);
    }

    /**
     * 广度优先遍历子控件
     * @param searchListener 遍历监听器
     */
    public void bfs(SearchListener searchListener){
        if(searchListener == null){
            return;
        }

        List<Node<View>> nodeQueue = new LinkedList<>();
        bfsInside(this,null,searchListener,nodeQueue);
    }

    protected boolean bfsInside(View thisNode,View parentNode,SearchListener searchListener,List<Node<View>> nodeQueue){
        if(thisNode instanceof TreeLayout){
            searchListener.onTreeStart((TreeLayout) thisNode);

            TreeLayout treeLayout = (TreeLayout) thisNode;
            View rootNode = treeLayout.getChildAt(0);

            if(!searchListener.onRootNode(rootNode,parentNode)){
                return false;
            }

            for(int i = 1;i < treeLayout.getChildCount();i++){
                View childNode = treeLayout.getChildAt(i);
                if(childNode instanceof TreeLayout){
                    nodeQueue.add(new Node(rootNode,childNode));
                }else{
                    boolean continueSearch = searchListener.onLeafNode(childNode,rootNode);
                    if(!continueSearch){
                        return false;
                    }
                }
            }
        }

        if(nodeQueue.size() <= 0){
            return false;
        }

        Node<View> treeNode = nodeQueue.remove(0);
        return bfsInside(treeNode.getThisNode(),treeNode.getParentNode(),searchListener,nodeQueue);
    }

    /**
     * 获取根结点View
     * @return 根结点View
     */
    public final View getRootNode(){
        return getChildAt(0);
    }

    /**
     * 跳过装饰物绘制，只会作用于本树，不会作用于子树
     * @param skip 是否跳过
     */
    public void skipDrawDecorator(boolean skip){
        mSkipDrawDecorator = skip;
        invalidate();
    }

    /**
     * 是否跳过绘制点缀
     * @return 是否跳过绘制点缀
     */
    public boolean isSkipDrawDecor(){
        return mSkipDrawDecorator;
    }

    /**
     * 跳过装饰物绘制作用于本树和所有子树
     * @param skip 是否跳过
     */
    public void skipWholeDrawDecorator(boolean skip){
        TreeGlobalParams params = getGlobalParams();
        params.skipDrawDecor = skip;
        configWholeTree(params);
    }

    /**
     * 获取结点最大分支数，如果对分支没有限制则返回-1，详见{@link #BRENCH_INFINATE}
     * @return 结点最大分支数
     */
    public int getMaxBranch(){
        return mMaxBranch;
    }

    /**
     * 获取树形布局参数
     * @return 树形布局参数
     */
    public TreeGlobalParams getGlobalParams(){
        return new TreeGlobalParams(this);
    }

    /**
     * 设置树形布局参数
     * @param treeGlobalParams 树形布局参数
     */
    public void configTree(TreeGlobalParams treeGlobalParams){
        mLevelInterval = treeGlobalParams.levelInterval;
        mTreeDirection.direction = treeGlobalParams.direction.direction;
        mDecoratorDrawer = treeGlobalParams.nodeDecoratorDrawer;
        mLocked = treeGlobalParams.locked;
        requestLayout();
        invalidate();
    }

    /**
     * 设置树形布局参数，作用在树和子树上
     * @param treeGlobalParams 树形布局参数
     */
    public void configWholeTree(TreeGlobalParams treeGlobalParams){
        SearchListener treeSearchListener = new SearchListener(){
            @Override
            public boolean onLeafNode(View thisNode, View parentNode) {
                return true;
            }

            @Override
            public boolean onRootNode(View thisNode, View parentNode) {
                return true;
            }

            @Override
            public void onTreeStart(TreeLayout treeLayout) {
                treeLayout.configTree(treeGlobalParams);
            }
        };

        bfs(treeSearchListener);
    }

    /**
     * 通过Tree生成对应的布局
     * @param data 结点数据
     * @param treeContentLoader 子控件加载器
     * @param <D> 数据类型
     */
    public <D> void loadViewsFormData(Tree<D> data,TreeContentLoader<D> treeContentLoader){
        InnerTreeContentLoader<D> innerTreeContentLoader = new InnerTreeContentLoader<>(this,data,treeContentLoader);
        innerTreeContentLoader.loadViewsFromData();
    }
}
