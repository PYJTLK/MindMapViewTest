package com.pyjtlk.container.tree.xmlhandler;

public class FloatTreeXmlHandler extends AbsTreeXmlHandler<Float> {
    @Override
    public Float parseData(String sourceData) {
        return Float.parseFloat(sourceData);
    }
}
