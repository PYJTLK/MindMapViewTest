package com.pyjtlk.widget.linedrawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pyjtlk.widget.Direction;
import com.pyjtlk.widget.TreeLayout;

public class DocumentLineDrawer extends DirectLineDrawer{
    private int mLevelInterval;

    public DocumentLineDrawer(int lineWidth,int levelInterval,int color) {
        super(lineWidth, color);
        mLevelInterval = levelInterval;
    }

    @Override
    protected void onDrawDecorator(Canvas canvas, Paint paint, Rect start, Rect end, int direction) {
        paint.setColor(getColor());
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(getLineWidth());

        switch(direction){
            case Direction.DIRECTION_LEFT_TO_RIGHT:
                mStartX = start.right;
                mEndX = end.left;
                mStartY = (start.top + start.bottom) / 2;
                mEndY = (end.top + end.bottom)  /2;
                onDrawLineLeftToRight(canvas,paint,mStartX,mStartY,mEndX,mEndY);
                break;

            case Direction.DIRECTION_RIGHT_TO_LEFT:
                mStartX = start.left;
                mEndX = end.right;
                mStartY = (start.top + start.bottom) / 2;
                mEndY = (end.top + end.bottom)  /2;
                onDrawLineRightToLeft(canvas,paint,mStartX,mStartY,mEndX,mEndY);
                break;

            case Direction.DIRECTION_UP_TO_DOWN:
                mStartX = (start.left + start.right) / 2;
                mEndX = (end.left + end.right) / 2;
                mStartY = start.bottom;
                mEndY = end.top;
                onDrawLineUpToDown(canvas,paint,mStartX,mStartY,mEndX,mEndY);
                break;

            case Direction.DIRECTION_DOWN_TO_UP:
                mStartX = (start.left + start.right) / 2;
                mEndX = (end.left + end.right) / 2;
                mStartY = start.top;
                mEndY = end.bottom;
                onDrawLineDownToUp(canvas,paint,mStartX,mStartY,mEndX,mEndY);
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
