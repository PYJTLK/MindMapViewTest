package com.pyjtlk.container.stack;

import java.util.LinkedList;
import java.util.List;

/**
 * 栈类，此栈类是非线程安全的
 * @param <D> 数据类型
 */
public class Stack<D>{
    private List<D> mContainer;

    public Stack(){
        mContainer = new LinkedList<>();
    }

    /**
     * 入栈
     * @param data 入栈数据
     */
    public void push(D data){
        mContainer.add(0,data);
    }

    /**
     * 出栈
     * @return 出栈数据
     */
    public D pop(){
        return mContainer.get(0);
    }

    /**
     * 栈的数据个数
     * @return 栈的数据个数
     */
    public int getSize(){
        return mContainer.size();
    }

    /**
     * 栈是否为空
     * @return 栈是否为空
     */
    public boolean empty(){
        return getSize() == 0;
    }

    /**
     * 清空栈
     */
    public void clear(){
        mContainer.clear();
    }
}
