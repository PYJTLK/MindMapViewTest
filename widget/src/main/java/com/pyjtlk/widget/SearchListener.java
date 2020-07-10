package com.pyjtlk.widget;

import android.view.View;

import com.pyjtlk.widget.treelayout.TreeLayout;

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
    boolean onLeafNode(View thisNode, View parentNode);

    boolean onRootNode(View thisNode,View parentNode);

    /**
     * 开始访问树形布局时调用
     * @param treeLayout 当前的树形布局
     */
    void onTreeStart(TreeLayout treeLayout);
}
