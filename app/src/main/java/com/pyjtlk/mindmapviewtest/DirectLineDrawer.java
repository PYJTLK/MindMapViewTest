package com.pyjtlk.mindmapviewtest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pyjtlk.widget.TreeLayout;

public class DirectLineDrawer extends TreeLayout.LineDrawer {
    private int mLineWidth;

    public DirectLineDrawer(int lineWidth){
        mLineWidth = lineWidth;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint, Rect start, Rect end,int direction) {
        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;

        startX = start.right;
        endX = end.left;
        startY = (start.top + start.bottom) / 2;
        endY = (end.top + end.bottom)  /2;

        paint.setColor(Color.LTGRAY);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mLineWidth);
        canvas.drawLine(startX,startY,endX,endY,paint);
    }
}
