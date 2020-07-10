package com.pyjtlk.mindmapviewtest;

import android.view.View;
import android.widget.TextView;

import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.widget.treelayout.AbsTreeAdapter;
import com.pyjtlk.widget.treelayout.TreeLayout;

public class TreeAdapter extends AbsTreeAdapter<Float> {
    public TreeAdapter(TreeLayout treeLayout, Tree<Float> dataTree) {
        super(dataTree);
    }

    @Override
    public void onInitContent(View contentView, Float data) {
        TextView textView = contentView.findViewById(R.id.nodeText);
        textView.setText("data:" + data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.node;
    }
}
