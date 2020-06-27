package com.pyjtlk.mindmapviewtest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pyjtlk.widget.TreeView;
import com.pyjtlk.widget.TreeView.LineDrawer;

public class DirectLineDrawer extends LineDrawer {
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

        switch(direction){
            case TreeView.DIRECTION_LEFT_TO_RIGHT:
                startX = start.right;
                endX = end.left;
                startY = (start.top + start.bottom) / 2;
                endY = (end.top + end.bottom)  /2;
                break;

            case TreeView.DIRECTION_RIGHT_TO_LEFT:
                startX = start.left;
                endX = end.right;
                startY = (start.top + start.bottom) / 2;
                endY = (end.top + end.bottom)  /2;
                break;
        }

        paint.setColor(Color.LTGRAY);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mLineWidth);
        canvas.drawLine(startX,startY,endX,endY,paint);
    }
}
