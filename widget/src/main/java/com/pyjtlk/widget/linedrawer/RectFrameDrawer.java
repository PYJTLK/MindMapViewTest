package com.pyjtlk.widget.linedrawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.pyjtlk.widget.TreeLayout;

public class RectFrameDrawer extends TreeLayout.NodeDecoratorDrawer {
    private int mLineWidth;
    private int mColor;

    public RectFrameDrawer(int lineWidth, int color) {
        this(null,lineWidth,color);
    }

    public RectFrameDrawer(TreeLayout.NodeDecoratorDrawer sourceDector, int lineWidth, int color) {
        super(sourceDector);
        mLineWidth = lineWidth;
        mColor = color;
    }

    @Override
    protected void onDrawDecorator(Canvas canvas, Paint paint, Rect start, Rect end, int direction) {
        paint.setStrokeWidth(mLineWidth);
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(start,paint);
        canvas.drawRect(end,paint);
    }

    @Override
    public boolean skipThisDraw(View startView, View endView) {
        return endView instanceof TreeLayout;
    }

    public int getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(int mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }
}
