package com.pyjtlk.container.tree.xmlhandler;

public class BooleanTreeXmlHandler extends AbsTreeXmlHandler<Boolean> {
    @Override
    public Boolean parseData(String sourceData) {
        return Boolean.parseBoolean(sourceData);
    }
}
