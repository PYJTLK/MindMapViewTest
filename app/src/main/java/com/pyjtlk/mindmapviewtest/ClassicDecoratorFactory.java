package com.pyjtlk.mindmapviewtest;

import com.pyjtlk.widget.TreeLayout;
import com.pyjtlk.widget.drawerfactory.AbsDecoratorFactory;
import com.pyjtlk.widget.linedrawer.ConnectPointDrawer;
import com.pyjtlk.widget.linedrawer.CurveDrawer;
import com.pyjtlk.widget.framedrawer.RectFrameDrawer;

public class ClassicDecoratorFactory extends AbsDecoratorFactory {
    private int mLineWidth;
    private int mRadius;
    private int mColor;
    private int mInterval;

    public ClassicDecoratorFactory(int lineWidth, int radius, int interval, int color) {
        mLineWidth = lineWidth;
        mRadius = radius;
        mColor = color;
        mInterval = interval;
    }

    @Override
    public TreeLayout.NodeDecoratorDrawer createDecorator() {
        //DocumentLineDrawer documentLineDrawer = new DocumentLineDrawer(mLineWidth,mInterval,mColor);
        CurveDrawer curveDrawer = new CurveDrawer(mLineWidth,mColor);
        RectFrameDrawer rectFrameDrawer = new RectFrameDrawer(curveDrawer,mLineWidth,mColor);
        ConnectPointDrawer connectPointDrawer = new ConnectPointDrawer(rectFrameDrawer,mRadius, mColor);
        return connectPointDrawer;
    }
}
