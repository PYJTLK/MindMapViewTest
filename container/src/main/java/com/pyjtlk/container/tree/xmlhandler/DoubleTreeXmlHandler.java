package com.pyjtlk.container.tree.xmlhandler;

public class DoubleTreeXmlHandler extends AbsTreeXmlHandler<Double> {
    @Override
    public Double parseData(String sourceData) {
        return Double.parseDouble(sourceData);
    }
}
