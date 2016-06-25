package com.bashell.androidutilsdemo.swipe;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bashell.androidutilsdemo.R;
import com.bashell.androidutilsdemo.bean.Cheeses;
import com.bashell.androidutilsdemo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by bashellwang on 2016/5/1.
 */
public class SwipeLayoutAdapter extends BaseAdapter {

    private Context mContext;
    //用于记录当前有哪些条目被打开了
    private ArrayList<SwipeLayout> swipeItems = new ArrayList<>();

    public SwipeLayoutAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Cheeses.NAMES.length;
    }

    @Override
    public Object getItem(int position) {
        return Cheeses.NAMES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = View.inflate(mContext, R.layout.item_swipe_layout, null);
        }
        ViewHolder mViewHolder = ViewHolder.getHolder(view);
        SwipeLayout mRootLayout = (SwipeLayout) view;
        mRootLayout.setSwipeListener(new SwipeLayout.OnSwipeLayoutListener() {
            @Override
            public void onClose(SwipeLayout mSwipeLayout) {
                Utils.showToast(mContext, "SwipeLayout--onClose");
                swipeItems.remove(mSwipeLayout);
            }

            @Override
            public void onOpen(SwipeLayout mSwipeLayout) {
                Utils.showToast(mContext, "SwipeLayout--onOpen");
                swipeItems.add(mSwipeLayout);
            }

            @Override
            public void onDraging(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartOpen(SwipeLayout mSwipeLayout) {
                Utils.showToast(mContext, "SwipeLayout--onStartOpen");
                //遍历将其它打开的条目关掉
                for (SwipeLayout item : swipeItems) {
                    item.close();
                }
                swipeItems.clear();
            }

            @Override
            public void onStartClose(SwipeLayout mSwipeLayout) {
                Utils.showToast(mContext, "SwipeLayout--onStartClose");
            }
        });
        return view;
    }

    static class ViewHolder {
        TextView tv_call;
        TextView tv_delete;

        public static ViewHolder getHolder(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                ViewHolder holder = new ViewHolder();
                holder.tv_call = (TextView) view.findViewById(R.id.tv_call);
                holder.tv_delete = (TextView) view.findViewById(R.id.tv_delete);
                tag = holder;
                view.setTag(holder);
            }
            return (ViewHolder) tag;
        }
    }
}
