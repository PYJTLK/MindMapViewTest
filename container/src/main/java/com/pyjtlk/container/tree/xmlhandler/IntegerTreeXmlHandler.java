package com.pyjtlk.container.tree.xmlhandler;

public class IntegerTreeXmlHandler extends AbsTreeXmlHandler<Integer> {
    @Override
    public Integer parseData(String sourceData) {
        return Integer.parseInt(sourceData);
    }
}
