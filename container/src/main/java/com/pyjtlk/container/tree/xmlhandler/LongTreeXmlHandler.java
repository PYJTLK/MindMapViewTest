package com.pyjtlk.container.tree.xmlhandler;

public class LongTreeXmlHandler extends AbsTreeXmlHandler<Long> {
    @Override
    public Long parseData(String sourceData) {
        return Long.parseLong(sourceData);
    }
}
