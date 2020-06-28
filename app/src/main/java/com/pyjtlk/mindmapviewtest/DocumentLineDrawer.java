package com.pyjtlk.mindmapviewtest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

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

        startX = start.right;
        endX = end.left;
        startY = (start.top + start.bottom) / 2;
        endY = (end.top + end.bottom)  /2;

        paint.setColor(getColor());
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(getLineWidth());

        canvas.drawLine(startX,startY,startX + mLevelInterval / 2,startY,paint);
        canvas.drawLine(startX + mLevelInterval / 2,startY,startX + mLevelInterval / 2,
                endY,paint);
        canvas.drawLine(startX + mLevelInterval / 2,endY,endX,
                endY,paint);
    }

    public int getLevelInterval() {
        return mLevelInterval;
    }

    public void setLevelInterval(int levelInterval) {
        this.mLevelInterval = levelInterval;
    }
}
