package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.container.tree.xmlhandler.FloatTreeXmlHandler;
import com.pyjtlk.container.tree.xmlhandler.StringTreeXmlHandler;
import com.pyjtlk.widget.Direction;
import com.pyjtlk.widget.mindmaplayout.MindMapLayout;
import com.pyjtlk.widget.treelayout.TreeLayout;

public class MindMapTestActivity extends AppCompatActivity {

    private MindMapLayout mindMapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mind_map_test);

        mindMapLayout = findViewById(R.id.mindmap);
        ClassicDecoratorFactory classicDecoratorFactory = new ClassicDecoratorFactory(6,12,150, Color.BLUE);
        mindMapLayout.setOrientation(MindMapLayout.ORIENTATION_HORIZONTAL);

        Tree<String> leftTree = Tree.parseFromXml(getResources().openRawResource(R.raw.tree_string),new StringTreeXmlHandler());
        Tree<String> rightTree = Tree.parseFromXml(getResources().openRawResource(R.raw.tree_string),new StringTreeXmlHandler());
        TreeLayout.TreeGlobalParams params = new TreeLayout.TreeGlobalParams();
        params.nodeDecoratorDrawer = classicDecoratorFactory.createDecorator();
        params.levelInterval = 150;
        params.locked = true;
        mindMapLayout.loadViewsFromData(leftTree,rightTree,new MyTreeContentLoader(),params);
    }

    public void oncliked(View view) {
        mindMapLayout.setOrientation(mindMapLayout.isHorizontal() ?
                MindMapLayout.ORIENTATION_VERTICAL : MindMapLayout.ORIENTATION_HORIZONTAL);
    }
}
