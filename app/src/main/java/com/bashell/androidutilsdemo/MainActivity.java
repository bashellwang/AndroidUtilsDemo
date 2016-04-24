package com.bashell.androidutilsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {

    private DragGroupLayout mDragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mDragLayout = (DragGroupLayout) findViewById(R.id.root_layout);
        mDragLayout.setDragStatusListener(new DragGroupLayout.OnDragStatusChangedListener() {
            @Override
            public void onOpen() {
                Utils.showToast(MainActivity.this, "onOpen");
            }

            @Override
            public void onClose() {
                Utils.showToast(MainActivity.this, "onClose");
            }

            @Override
            public void onDraging(float percent) {
                Utils.showToast(MainActivity.this, "onDraging");
            }
        });
    }
}
