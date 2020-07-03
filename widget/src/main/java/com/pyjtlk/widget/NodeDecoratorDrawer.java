package com.pyjtlk.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * 点缀绘制器
 */
public abstract class NodeDecoratorDrawer {
    private NodeDecoratorDrawer mSourceDector;

    public NodeDecoratorDrawer(NodeDecoratorDrawer sourceDector){
        mSourceDector = sourceDector;
    }

    /**
     * 绘制点缀
     * @param canvas 绘制点缀的画布
     * @param paint 绘制点缀的画笔
     * @param start 点缀的起点控件的区域，即父结点控件所在区域
     * @param end 点缀的终点控件的区域，即子结点控件所在区域
     * @param direction 树的方向
     *                  参考{@link Direction#DIRECTION_LEFT_TO_RIGHT,
     *                       @link Direction#DIRECTION_RIGHT_TO_LEFT,
     *                       @link Direction#DIRECTION_UP_TO_DOWN,
     *                       @link Direction#DIRECTION_DOWN_TO_UP}
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
    public void drawDecorator(Canvas canvas, Paint paint, Rect start, Rect end, View startView, View endView, int direction){
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
