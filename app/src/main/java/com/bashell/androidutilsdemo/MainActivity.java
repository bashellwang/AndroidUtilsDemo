package com.bashell.androidutilsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.bashell.androidutilsdemo.drag.DragGroupLayout;
import com.bashell.androidutilsdemo.quickindex.QuickIndex;
import com.bashell.androidutilsdemo.swipe.SwipeLayoutAdapter;
import com.bashell.androidutilsdemo.utils.Utils;

public class MainActivity extends Activity {

    private DragGroupLayout mDragLayout;
    private ListView mContentListView;
    private QuickIndex mQuickIndexView;

    private Handler mHandler = new Handler();
    private TextView mCenterTv;

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

        mQuickIndexView = (QuickIndex) findViewById(R.id.quick_index_view);
        mCenterTv = (TextView) findViewById(R.id.tv_center);
        mQuickIndexView.setmLetterTouchedListener(new QuickIndex.onLetterTouchListener() {
            @Override
            public void onLetterTouched(String text) {
                Utils.showToast(MainActivity.this, text);
                showLetter(text);
            }
        });
    }

    private void showLetter(String text) {

        mCenterTv.setVisibility(View.VISIBLE);
        mCenterTv.setText(text);

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterTv.setVisibility(View.GONE);
            }
        }, 2000);
    }


}

