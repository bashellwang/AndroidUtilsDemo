package com.bashell.androidutilsdemo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by bashellwang on 2016/4/23.
 */
public class Utils {
    private Utils mInstance = null;

    public Utils getInstance() {
        if (mInstance == null) {
            mInstance = new Utils();
        }
        return mInstance;
    }

    public static Toast mToast = null;

    public static void showToast(Context mContext, CharSequence str) {

        if (mToast == null) {
            mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
    }
}
