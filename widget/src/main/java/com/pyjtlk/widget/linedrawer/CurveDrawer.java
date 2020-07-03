package com.pyjtlk.widget.linedrawer;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import com.pyjtlk.widget.TreeLayout;

public class CurveDrawer extends DirectLineDrawer {
    protected float mArcStart;
    protected float mSweepAngle;
    protected Path mPath = new Path();

    public CurveDrawer(int lineWidth, int color) {
        super(lineWidth, color);
    }

    public CurveDrawer(TreeLayout.NodeDecoratorDrawer sourceDecorator, int lineWidth, int color) {
        super(sourceDecorator, lineWidth, color);
    }

    @Override
    public void drawDecorator(Canvas canvas, Paint paint, Rect start, Rect end, View startView, View endView, int direction) {
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mLineWidth);

        mPath.reset();

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

        switch(direction){
            case TreeLayout.DIRECTION_LEFT_TO_RIGHT:
            case TreeLayout.DIRECTION_RIGHT_TO_LEFT:
                onDrawCurveHorizontal(canvas,paint);
                break;

            case TreeLayout.DIRECTION_UP_TO_DOWN:
            case TreeLayout.DIRECTION_DOWN_TO_UP:
                onDrawCurveVertical(canvas,paint);
                break;
        }
    }

    private void onDrawCurveHorizontal(Canvas canvas, Paint paint){
        int width = Math.abs(mStartX - mEndX);
        int height = Math.abs(mStartY - mEndY);

        if(mStartX < mEndX && mStartY < mEndY){
           mPath.addArc(mStartX,mStartY - height,mEndX+ width,mEndY,180,-90);
           canvas.drawPath(mPath,paint);
        }else if(mStartX < mEndX && mStartY > mEndY){
            mPath.addArc(mStartX,mStartY - height,mEndX + width,mEndY + height * 2,180,90);
            canvas.drawPath(mPath,paint);
        }else if(mStartX > mEndX && mStartY < mEndY){
            mPath.addArc(mEndX - width,mStartY - height,mStartX,mEndY,0,90);
            canvas.drawPath(mPath,paint);
        }else if(mStartX > mEndX && mStartY > mEndY){
            mPath.addArc(mEndX - width,mEndY,mStartX,mStartY + height,0,-90);
            canvas.drawPath(mPath,paint);
        }else{
            canvas.drawLine(mStartX,mStartY,mEndX,mEndY,paint);
        }
    }

    private void onDrawCurveVertical(Canvas canvas, Paint paint){
        int width = Math.abs(mStartX - mEndX);
        int height = Math.abs(mStartY - mEndY);

        if(mStartX < mEndX && mStartY < mEndY){
            mPath.addArc(mStartX  - width,mStartY,mEndX,mEndY + height,0,-90);
            canvas.drawPath(mPath,paint);
        }else if(mStartX < mEndX && mStartY > mEndY){
            mPath.addArc(mStartX - width,mEndY - height,mEndX,mStartY,0,90);
            canvas.drawPath(mPath,paint);
        }else if(mStartX > mEndX && mStartY < mEndY){
            mPath.addArc(mEndX,mStartY,mStartX + width,mEndY + height,180,90);
            canvas.drawPath(mPath,paint);
        }else if(mStartX > mEndX && mStartY > mEndY){
            mPath.addArc(mEndX,mEndY - height,mStartX + width,mStartY,180,-90);
            canvas.drawPath(mPath,paint);
        }else{
            canvas.drawLine(mStartX,mStartY,mEndX,mEndY,paint);
        }
    }
}
