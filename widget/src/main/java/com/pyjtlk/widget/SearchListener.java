package com.pyjtlk.widget;

import android.view.View;

/**
 * 遍历监听器
 */
public interface SearchListener {
    /**
     * 访问结点时调用
     * @param thisNode 此结点
     * @param parentNode 父结点
     * @return 返回true，则继续遍历，返回false，则中断此次遍历
     */
    boolean onNode(View thisNode, View parentNode);

    /**
     * 结点为叶结点时调用
     * @param thisNode
     * @param parentNode
     */
    void onBranchEnd(View thisNode, View parentNode);
}
