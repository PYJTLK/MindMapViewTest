package com.pyjtlk.mindmapviewtest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.pyjtlk.widget.TreeLayout;

public class DocumentLineDrawer extends DirectLineDrawer{
    private int mLevelInterval;

    public DocumentLineDrawer(int lineWidth,int levelInterval,int color) {
        super(lineWidth, color);
        mLevelInterval = levelInterval;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint, Rect start, Rect end, int direction) {
        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;

        paint.setColor(getColor());
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(getLineWidth());

        switch(direction){
            case TreeLayout.DIRECTION_LEFT_TO_RIGHT:
                startX = start.right;
                endX = end.left;
                startY = (start.top + start.bottom) / 2;
                endY = (end.top + end.bottom)  /2;
                onDrawLineLeftToRight(canvas,paint,startX,startY,endX,endY);
                break;

            case TreeLayout.DIRECTION_RIGHT_TO_LEFT:
                startX = start.left;
                endX = end.right;
                startY = (start.top + start.bottom) / 2;
                endY = (end.top + end.bottom)  /2;
                onDrawLineRightToLeft(canvas,paint,startX,startY,endX,endY);
                break;

            case TreeLayout.DIRECTION_UP_TO_DOWN:
                startX = (start.left + start.right) / 2;
                endX = (end.left + end.right) / 2;
                startY = start.bottom;
                endY = end.top;
                onDrawLineUpToDown(canvas,paint,startX,startY,endX,endY);
                break;

            case TreeLayout.DIRECTION_DOWN_TO_UP:
                startX = (start.left + start.right) / 2;
                endX = (end.left + end.right) / 2;
                startY = start.top;
                endY = end.bottom;
                onDrawLineDownToUp(canvas,paint,startX,startY,endX,endY);
                break;
        }
    }

    protected void onDrawLineUpToDown(Canvas canvas, Paint paint,int startX,int startY,int endX,int endY) {
        canvas.drawLine(startX,startY,startX,startY + mLevelInterval / 2,paint);
        canvas.drawLine(startX,startY + mLevelInterval / 2,endX,
                startY + mLevelInterval / 2,paint);
        canvas.drawLine(endX,startY + mLevelInterval / 2,endX,
                endY,paint);
    }

    protected void onDrawLineDownToUp(Canvas canvas, Paint paint,int startX,int startY,int endX,int endY) {
        canvas.drawLine(startX,startY,startX,startY - mLevelInterval / 2,paint);
        canvas.drawLine(startX,startY - mLevelInterval / 2,endX,
                startY - mLevelInterval / 2,paint);
        canvas.drawLine(endX,startY - mLevelInterval / 2,endX,
                endY,paint);
    }

    protected void onDrawLineLeftToRight(Canvas canvas, Paint paint,int startX,int startY,int endX,int endY){
        canvas.drawLine(startX,startY,startX + mLevelInterval / 2,startY,paint);
        canvas.drawLine(startX + mLevelInterval / 2,startY,startX + mLevelInterval / 2,
                endY,paint);
        canvas.drawLine(startX + mLevelInterval / 2,endY,endX,
                endY,paint);
    }

    protected void onDrawLineRightToLeft(Canvas canvas, Paint paint,int startX,int startY,int endX,int endY){
        canvas.drawLine(startX,startY,startX - mLevelInterval / 2,startY,paint);
        canvas.drawLine(startX - mLevelInterval / 2,startY,startX - mLevelInterval / 2,
                endY,paint);
        canvas.drawLine(startX - mLevelInterval / 2,endY,endX,
                endY,paint);
    }

    public int getLevelInterval() {
        return mLevelInterval;
    }

    public void setLevelInterval(int levelInterval) {
        this.mLevelInterval = levelInterval;
    }
}
