package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.pyjtlk.widget.TreeLayout;

public class MainActivity extends AppCompatActivity {

    private TreeLayout treeView;
    private TreeLayout treeView1;
    private TreeLayout treeView2;
    private TreeLayout treeView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        treeView = findViewById(R.id.treeView);
        treeView.setLineDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(), Color.WHITE));

        /*
        treeView1 = findViewById(R.id.treeView1);
        treeView1.setLineDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE));

        treeView2 = findViewById(R.id.treeView2);
        treeView2.setLineDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE));

        treeView3 = findViewById(R.id.treeView3);
        treeView3.setLineDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE));

         */
    }
}
