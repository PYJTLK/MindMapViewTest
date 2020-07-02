package com.pyjtlk.widget.linedrawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pyjtlk.widget.TreeLayout;

public class ConnectPointDrawer extends TreeLayout.NodeDecoratorDrawer {
    private int mPointRadius;
    private int mColor;
    private int mStartX;
    private int mStartY ;
    private int mEndX;
    private int mEndY;

    public ConnectPointDrawer(int pointRadius,int color){
        this(null,pointRadius,color);
    }

    public ConnectPointDrawer(TreeLayout.NodeDecoratorDrawer sourceDector, int pointRadius, int color) {
        super(sourceDector);
        mPointRadius = pointRadius;
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

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mColor);
        canvas.drawCircle(mStartX,mStartY,mPointRadius,paint);
        canvas.drawCircle(mEndX,mEndY,mPointRadius,paint);
    }

    public int getPointRadius() {
        return mPointRadius;
    }

    public void setPointRadius(int radius) {
        mPointRadius = radius;
    }

    public int getColor() {
        return mColor;
    }

    public void setmColor(int color) {
        mColor = color;
    }
}
