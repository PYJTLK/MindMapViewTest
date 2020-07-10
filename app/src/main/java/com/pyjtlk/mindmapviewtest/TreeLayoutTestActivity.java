package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.container.tree.xmlhandler.FloatTreeXmlHandler;
import com.pyjtlk.widget.treelayout.TreeLayout;

public class TreeLayoutTestActivity extends AppCompatActivity {

    private TreeLayout treeLayout;
    private TreeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ClassicDecoratorFactory factory = new ClassicDecoratorFactory(6,12,150, Color.WHITE);
        treeLayout = findViewById(R.id.treeLayout);
        treeLayout.setDecorDrawer(factory.createDecorator());

        Tree<Float> tree = Tree.parseFromXml(getResources().openRawResource(R.raw.tree_float),new FloatTreeXmlHandler());

        adapter = new TreeAdapter(treeLayout,tree);
        adapter.bind(treeLayout);

        treeLayout.lockTree(false);
    }

    public void oncliked(View view) {
    }
}
