package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pyjtlk.container.tree.xmlhandler.FloatTreeXmlHandler;
import com.pyjtlk.container.tree.Tree;
import com.pyjtlk.widget.treelayout.TreeLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void oncliked(View view) {
        Intent intent = new Intent(this,TreeLayoutTestActivity.class);
        startActivity(intent);
    }

    public void oncliked1(View view) {
        Intent intent = new Intent(this,MindMapTestActivity.class);
        startActivity(intent);
    }
}
