package com.pyjtlk.mindmapviewtest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pyjtlk.widget.TreeLayout;

public class DirectLineDrawer extends TreeLayout.LineDrawer {
    private int mLineWidth;
    private int mColor;

    public DirectLineDrawer(int lineWidth,int color){
        mLineWidth = lineWidth;
        mColor = color;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint, Rect start, Rect end,int direction) {
        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;

        switch(direction){
            case TreeLayout.DIRECTION_LEFT_TO_RIGHT:
                startX = start.right;
                endX = end.left;
                startY = (start.top + start.bottom) / 2;
                endY = (end.top + end.bottom)  /2;
                break;

            case TreeLayout.DIRECTION_RIGHT_TO_LEFT:
                startX = start.left;
                endX = end.right;
                startY = (start.top + start.bottom) / 2;
                endY = (end.top + end.bottom)  /2;
                break;

            case TreeLayout.DIRECTION_UP_TO_DOWN:
                startX = (start.right + start.left) / 2;
                endX = (end.right + end.left) / 2;
                startY = start.bottom;
                endY = end.top;
                break;

            case TreeLayout.DIRECTION_DOWN_TO_UP:
                startX = (start.right + start.left) / 2;
                endX = (end.right + end.left) / 2;
                startY = start.top;
                endY = end.bottom;
                break;
        }

        paint.setColor(mColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mLineWidth);
        canvas.drawLine(startX,startY,endX,endY,paint);
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
        this.mColor = color;
    }
}
