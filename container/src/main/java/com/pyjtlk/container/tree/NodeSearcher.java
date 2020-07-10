package com.pyjtlk.container.tree;

/**
 * 结点访问者
 * @param <D> 结点的数据类型
 */
public interface NodeSearcher<D> {
    /**
     * 访问结点
     * @param parent 父结点
     * @param thisTree 本结点
     * @return 是否终止遍历
     */
    boolean onNode(Tree<D> parent,Tree<D> thisTree);
}
