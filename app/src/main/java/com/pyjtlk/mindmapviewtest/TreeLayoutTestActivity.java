package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.container.tree.xmlhandler.StringTreeXmlHandler;
import com.pyjtlk.widget.treelayout.TreeLayout;

public class TreeLayoutTestActivity extends AppCompatActivity {

    private TreeLayout treeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ClassicDecoratorFactory factory = new ClassicDecoratorFactory(6,12,150, Color.WHITE);
        treeLayout = findViewById(R.id.treeLayout);
        treeLayout.setDecorDrawer(factory.createDecorator());
        Tree<String> tree = Tree.parseFromXml(getResources().openRawResource(R.raw.tree_string),new StringTreeXmlHandler());
        treeLayout.lockTree(true);
        treeLayout.loadViewsFormData(tree,new MyTreeContentLoader());
    }

    int direction = 1;

    public void oncliked(View view) {
        treeLayout.lockTree(false);
        if(direction >= 4){
            direction = 0;
        }

        treeLayout.setUnionTreeDirection(direction);

        direction++;
    }
}
