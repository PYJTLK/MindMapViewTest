package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.pyjtlk.widget.TreeView;

public class MainActivity extends AppCompatActivity {

    private TreeView treeView;
   // private TreeView treeView1;
    //private TreeView treeView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        treeView = findViewById(R.id.treeView);
        treeView.setLineDrawer(new DirectLineDrawer(6));

        //treeView1 = findViewById(R.id.treeView1);
        //treeView1.setLineDrawer(new DirectLineDrawer(6));

       // treeView2 = findViewById(R.id.treeView2);
       // treeView2.setLineDrawer(new DirectLineDrawer(6));
    }
}
