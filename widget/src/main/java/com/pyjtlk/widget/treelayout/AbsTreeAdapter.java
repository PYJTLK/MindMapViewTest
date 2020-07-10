package com.pyjtlk.widget.treelayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pyjtlk.container.tree.NodeSearcher;
import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.widget.SearchListener;

import java.util.LinkedList;
import java.util.List;

public abstract class AbsTreeAdapter<D>{
    protected Tree<D> mDataTree;
    protected TreeLayout mTreeLayout;
    protected Context mContext;

    public AbsTreeAdapter(Tree<D> dataTree) {
        mDataTree = dataTree;
    }

    public View getContentView(LayoutInflater layoutInflater, ViewGroup parentView){
        View view = layoutInflater.inflate(getLayoutId(),parentView,false);
        return view;
    }

    public abstract void onInitContent(View contentView,D data);

    public abstract int getLayoutId();

    public void bind(TreeLayout treeLayout){
        mTreeLayout = treeLayout;
        mContext = mTreeLayout.getContext();
        createTreeData();
    }

    private void createTreeData(){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        List<TreeLayout> queue = new LinkedList<>();

        mDataTree.bfs(new NodeSearcher<D>() {
            @Override
            public boolean onNode(Tree<D> parent, Tree<D> thisTree) {
                if(!thisTree.isLeaf()){
                    TreeLayout treeLayout;

                    if(queue.isEmpty()){
                        treeLayout = mTreeLayout;
                    }else{
                        treeLayout = queue.remove(0);
                    }

                    View rootNodeView = getContentView(layoutInflater,treeLayout);
                    TreeLayout.LayoutParams noMarginLayoutParams = (TreeLayout.LayoutParams) rootNodeView.getLayoutParams();
                    noMarginLayoutParams.setMargins(0,0,0,0);
                    rootNodeView.setLayoutParams(noMarginLayoutParams);
                    onInitContent(rootNodeView,thisTree.getRootData());
                    treeLayout.addView(rootNodeView);

                    List<Tree<D>> childrenNode = thisTree.getChildren();

                    for(int i = 0;i < childrenNode.size();i++){
                        Tree<D> childNode = childrenNode.get(i);
                        if(childNode.isLeaf()){
                            View childNodeView = getContentView(layoutInflater,treeLayout);
                            onInitContent(childNodeView,childNode.getRootData());
                            treeLayout.addView(childNodeView);
                        }else{
                            TreeLayout subTreeLayout = new TreeLayout(mContext);
                            loadWrapLayoutParams(subTreeLayout);
                            treeLayout.addView(subTreeLayout);
                            queue.add(subTreeLayout);
                        }
                    }
                }
                return true;
            }
        });

        notifyAllTreeChanged();
    }

    public void notifyAllTreeChanged(){
        notifyAllTreeChanged(new TreeLayout.TreeGlobalParams(mTreeLayout));
    }

    public void notifyAllTreeChanged(TreeLayout.TreeGlobalParams treeGlobalParams){
        SearchListener searchListener = new SearchListener() {
            @Override
            public boolean onLeafNode(View thisNode, View parentNode) {
                return true;
            }

            @Override
            public boolean onRootNode(View thisNode, View parentNode) {
                return true;
            }

            @Override
            public void onTreeStart(TreeLayout treeLayout) {
                treeLayout.setTreeDirection(treeGlobalParams.direction.direction);
                treeLayout.setLevelInterval(treeGlobalParams.levelInterval);
                treeLayout.setDecorDrawer(treeGlobalParams.nodeDecoratorDrawer);
                treeLayout.lockTree(true);
            }
        };

        mTreeLayout.bfs(searchListener);
    }

    protected void loadWrapLayoutParams(View view){
        TreeLayout.LayoutParams layoutParams = new TreeLayout.LayoutParams(TreeLayout.LayoutParams.WRAP_CONTENT,
                TreeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
    }
}
