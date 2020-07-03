package com.pyjtlk.widget.drawerfactory;

import com.pyjtlk.widget.TreeLayout;

public abstract class AbsDecoratorFactory {
    public abstract TreeLayout.NodeDecoratorDrawer createDecorator();
}
