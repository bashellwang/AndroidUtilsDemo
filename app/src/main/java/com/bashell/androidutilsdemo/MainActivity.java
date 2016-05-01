package com.bashell.androidutilsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.bashell.androidutilsdemo.drag.DragGroupLayout;
import com.bashell.androidutilsdemo.swipe.SwipeLayoutAdapter;
import com.bashell.androidutilsdemo.utils.Utils;

public class MainActivity extends Activity {

    private DragGroupLayout mDragLayout;
    private ListView mContentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mDragLayout = (DragGroupLayout) findViewById(R.id.root_layout);
        mDragLayout.setDragStatusListener(new DragGroupLayout.OnDragStatusChangedListener() {
            @Override
            public void onOpen() {
                Utils.showToast(MainActivity.this, "DragGroupLayout--onOpen");
            }

            @Override
            public void onClose() {
                Utils.showToast(MainActivity.this, "DragGroupLayout--onClose");
            }

            @Override
            public void onDraging(float percent) {
            }
        });

        mContentListView = (ListView) findViewById(R.id.lv_content);
        mContentListView.setAdapter(new SwipeLayoutAdapter(MainActivity.this));
    }
}
