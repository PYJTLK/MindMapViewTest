package com.pyjtlk.mindmapviewtest;

import android.view.View;
import android.widget.TextView;

import com.pyjtlk.widget.treelayout.TreeLayout;

public class MyTreeContentLoader extends TreeLayout.TreeContentLoader<String> {
    @Override
    public void onInitContent(View contentView, String data) {
        TextView textView = contentView.findViewById(R.id.nodeText);
        textView.setText(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.node;
    }
}
