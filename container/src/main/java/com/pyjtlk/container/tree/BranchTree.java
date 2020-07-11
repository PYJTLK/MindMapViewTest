package com.pyjtlk.container.tree;

import java.util.Collection;

/**
 * n叉树，n叉树对分支进行了严格的限制，插入新的结点后如果超出分支限制则会报错
 * @param <D> 结点的数据类型
 */
public class BranchTree<D> extends Tree<D>{
    private int mBranch;

    public BranchTree(int branch) {
        super();
        if(branch < 0){
            throw new IllegalArgumentException("branch should >= 0");
        }
        this.mBranch = branch;
    }

    public BranchTree(D data, int branch) {
        super(data);
        mBranch = branch;
    }

    /**
     * 插入检查
     * @param insertNum 插入结点数
     * @return 是否在分支限制内
     */
    public boolean checkInsert(int insertNum){
        return mChildren.size() + insertNum <= mBranch;
    }

    @Override
    public void insertChild(Tree<D> childTree) {
        typeCheck(childTree);

        if(!checkInsert(1))
            throw new IllegalArgumentException("branch should <= " + mBranch);
        super.insertChild(childTree);
    }

    @Override
    public void insertChild(Tree<D> childTree, int index) {
        typeCheck(childTree);

        if(!checkInsert(1))
            throw new IllegalArgumentException("branch should <= " + mBranch);
        super.insertChild(childTree, index);
    }

    @Override
    public void insertChildren(Collection<Tree<D>> children) {
        if(!checkInsert(children.size()))
            throw new IllegalArgumentException("branch should <= " + mBranch);
        super.insertChildren(children);
    }

    @Override
    public void insertChildren(Collection<Tree<D>> children, int index) {
        if (!checkInsert(children.size()))
            throw new IllegalArgumentException("branch should <= " + mBranch);
        super.insertChildren(children, index);
    }

    private void typeCheck(Tree<D> tree){
        if(!(tree instanceof BranchTree)){
            throw new IllegalArgumentException(tree + "is not a BranchTree");
        }
    }
}
