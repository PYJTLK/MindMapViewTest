package com.pyjtlk.container.tree;

/**
 * 结点类，保存父结点
 * @param <D>
 */
public class Node<D> {
    private D parentNode;
    private D thisNode;

    public Node(D parentNode, D thisNode) {
        this.parentNode = parentNode;
        this.thisNode = thisNode;
    }

    public D getParentNode() {
        return parentNode;
    }

    public D getThisNode() {
        return thisNode;
    }
}
