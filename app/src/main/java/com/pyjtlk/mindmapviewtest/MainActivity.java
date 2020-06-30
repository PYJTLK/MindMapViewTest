package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.pyjtlk.widget.TreeLayout;
import com.pyjtlk.widget.linedrawer.DirectLineDrawer;


public class MainActivity extends AppCompatActivity {

    private TreeLayout treeView;
    private TreeLayout treeView1;
    private TreeLayout treeView2;
    private TreeLayout treeView3;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        treeView = findViewById(R.id.treeView);
        treeView.setLineDrawer(new DirectLineDrawer(6,Color.WHITE));


        //treeView1 = findViewById(R.id.treeView1);
        //treeView1.setLineDrawer(new DirectLineDrawer(6,Color.WHITE));

        /*
        treeView2 = findViewById(R.id.treeView2);
        treeView2.setLineDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE));

        treeView3 = findViewById(R.id.treeView3);
        treeView3.setLineDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE));
         */
    }

    public void oncliked(View view) {
        treeView.lockTree(!treeView.isLocked());
        button.setText(treeView.isLocked() ? "locked" : "unlocked");
        //treeView1.setVisibility(treeView1.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }
}
