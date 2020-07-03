package com.pyjtlk.widget.linedrawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pyjtlk.widget.TreeLayout;

public class DirectLineDrawer extends TreeLayout.NodeDecoratorDrawer {
    protected int mLineWidth;
    protected int mColor;
    protected int mStartX;
    protected int mStartY ;
    protected int mEndX;
    protected int mEndY;

    public DirectLineDrawer(int lineWidth, int color){
        this(null,lineWidth,color);
    }

    public DirectLineDrawer(TreeLayout.NodeDecoratorDrawer sourceDecorator, int lineWidth, int color){
        super(sourceDecorator);
        mLineWidth = lineWidth;
        mColor = color;
    }

    @Override
    protected void onDrawDecorator(Canvas canvas, Paint paint, Rect start, Rect end, int direction) {
        switch(direction){
            case TreeLayout.DIRECTION_LEFT_TO_RIGHT:
                mStartX = start.right;
                mEndX = end.left;
                mStartY = (start.top + start.bottom) / 2;
                mEndY = (end.top + end.bottom)  /2;
                break;

            case TreeLayout.DIRECTION_RIGHT_TO_LEFT:
                mStartX = start.left;
                mEndX = end.right;
                mStartY = (start.top + start.bottom) / 2;
                mEndY = (end.top + end.bottom)  /2;
                break;

            case TreeLayout.DIRECTION_UP_TO_DOWN:
                mStartX = (start.right + start.left) / 2;
                mEndX = (end.right + end.left) / 2;
                mStartY = start.bottom;
                mEndY = end.top;
                break;

            case TreeLayout.DIRECTION_DOWN_TO_UP:
                mStartX = (start.right + start.left) / 2;
                mEndX = (end.right + end.left) / 2;
                mStartY = start.top;
                mEndY = end.bottom;
                break;
        }

        paint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mLineWidth);
        canvas.drawLine(mStartX,mStartY,mEndX,mEndY,paint);
    }

    public int getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.mLineWidth = lineWidth;
    }

    public int getColor() {
        return mColor;
    }

    public void setmColor(int color) {
        mColor = color;
    }
}
