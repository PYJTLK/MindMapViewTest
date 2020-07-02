package com.pyjtlk.mindmapviewtest;

import com.pyjtlk.widget.TreeLayout;
import com.pyjtlk.widget.linedrawer.AbsDecoratorFactory;
import com.pyjtlk.widget.linedrawer.ConnectPointDrawer;
import com.pyjtlk.widget.linedrawer.DirectLineDrawer;
import com.pyjtlk.widget.linedrawer.DocumentLineDrawer;
import com.pyjtlk.widget.linedrawer.RectFrameDrawer;

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
        DirectLineDrawer documentLineDrawer = new DocumentLineDrawer(mLineWidth,mInterval,mColor);
        RectFrameDrawer rectFrameDrawer = new RectFrameDrawer(documentLineDrawer,mLineWidth,mColor);
        ConnectPointDrawer connectPointDrawer = new ConnectPointDrawer(rectFrameDrawer,mRadius, mColor);
        return connectPointDrawer;
    }
}
