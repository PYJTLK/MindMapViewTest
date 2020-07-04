package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pyjtlk.widget.MindMapLayout;
import com.pyjtlk.widget.TreeLayout;


public class MainActivity extends AppCompatActivity {

    private MindMapLayout mindMapLayout;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        mindMapLayout = findViewById(R.id.mindMapLayout);
        ClassicDecoratorFactory factory = new ClassicDecoratorFactory(6,12,150,Color.WHITE);
        mindMapLayout.setDecorDrawer(factory.createDecorator());
        mindMapLayout.setOrientation(MindMapLayout.ORIENTATION_HORIZONTAL);
    }

    public void oncliked(View view) {
        //button.setText(mindMapLayout.isLocked() ? "locked" : "unlocked");
        //mindMapLayout.lockMap(!mindMapLayout.isLocked());
        mindMapLayout.setOrientation(mindMapLayout.getOrientation() == MindMapLayout.ORIENTATION_HORIZONTAL ?
               MindMapLayout.ORIENTATION_VERTICAL : MindMapLayout.ORIENTATION_HORIZONTAL);

        //mindMapLayout.skipDrawDecorator(!mindMapLayout.isSkipDrawDecorator());
    }
}
