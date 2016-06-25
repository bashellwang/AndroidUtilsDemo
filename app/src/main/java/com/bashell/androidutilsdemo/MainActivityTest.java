package com.bashell.androidutilsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.bashell.androidutilsdemo.drag.DragGroupLayout;
import com.bashell.androidutilsdemo.swipe.SwipeLayoutAdapter;

public class MainActivityTest extends Activity {

    private DragGroupLayout mDragLayout;
    private ListView mContentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_test);

        mContentListView = (ListView) findViewById(R.id.lv_content);
        mContentListView.setAdapter(new SwipeLayoutAdapter(MainActivityTest.this));
    }
}
