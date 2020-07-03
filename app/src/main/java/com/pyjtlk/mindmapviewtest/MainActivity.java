package com.pyjtlk.mindmapviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.pyjtlk.widget.TreeLayout;


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

        /*
        DocumentLineDrawer documentLineDrawer = new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE);
        RectFrameDrawer rectFrameDrawer = new RectFrameDrawer(documentLineDrawer,6,Color.WHITE);
        ConnectPointDrawer connectPointDrawer = new ConnectPointDrawer(rectFrameDrawer,6,Color.WHITE);

         */
        ClassicDecoratorFactory factory = new ClassicDecoratorFactory(6,12,treeView.getLevelInterval(),Color.BLUE);

        treeView.setDecorDrawer(factory.createDecorator());


        treeView1 = findViewById(R.id.treeView1);
        treeView1.setDecorDrawer(factory.createDecorator());


        treeView2 = findViewById(R.id.treeView2);
        treeView2.setDecorDrawer(factory.createDecorator());

        /*
        treeView3 = findViewById(R.id.treeView3);
        treeView3.setDecorDrawer(new DocumentLineDrawer(6,treeView.getLevelInterval(),Color.WHITE));
         */
    }

    int i = 0;

    public void oncliked(View view) {
        //treeView.lockTree(!treeView.isLocked());
        //button.setText(treeView.isLocked() ? "locked" : "unlocked");
        //treeView1.setVisibility(treeView1.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        //treeView.scaleContent(0.5f);
        treeView.bfs(new TreeLayout.SearchListener() {
            @Override
            public boolean onNode(View thisNode, View parentNode) {
                if(thisNode instanceof Button){
                    ((Button)thisNode).setText("" + i);
                    i++;
                }
                return true;
            }
        });
    }
}
