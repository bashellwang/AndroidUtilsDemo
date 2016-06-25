package com.bashell.androidutilsdemo.quickindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bashellwang on 2016/6/25.
 */
public class QuickIndex extends View {
    private float cellWidth;
    private float cellHeight;
    private Paint mPaint;
    private int textSize = 40;
    private int x, y;

    private int touchIndex = -1;

    public onLetterTouchListener getmLetterTouchedListener() {
        return mLetterTouchedListener;
    }

    public void setmLetterTouchedListener(onLetterTouchListener mLetterTouchedListener) {
        this.mLetterTouchedListener = mLetterTouchedListener;
    }

    private onLetterTouchListener mLetterTouchedListener;

    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};

    public interface onLetterTouchListener {
        void onLetterTouched(String text);
    }

    public QuickIndex(Context context) {
        this(context, null);
    }

    public QuickIndex(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndex(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 绘制过程，绘制索引UI
         */
        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            x = (int) (cellWidth / 2.0f - mPaint.measureText(text) / 2.0f);
            int textHeight = getTextHeight(mPaint, text);
            y = (int) (cellHeight / 2.0f + textHeight / 2.0f + i * cellHeight);
            canvas.drawText(text, x, y, mPaint);
        }
    }

    /**
     * 获取文本的高度
     *
     * @param mPaint
     * @param text
     * @return
     */
    private int getTextHeight(Paint mPaint, String text) {
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                //获取当前触摸到的位置id
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    //判断当前触摸位置是否和上次触摸位置一致
                    if (index != touchIndex) {
                        if (mLetterTouchedListener != null) {
                            mLetterTouchedListener.onLetterTouched(LETTERS[index]);
                            //更新触摸位置
                            touchIndex = index;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (index != touchIndex) {
                        if (mLetterTouchedListener != null) {
                            mLetterTouchedListener.onLetterTouched(LETTERS[index]);
                            touchIndex = index;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                touchIndex = -1;
                break;
            default:
                break;
        }
        return true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(heightMeasureSpec));
    }

    /**
     * 模板代码
     * 当指定宽高属性为wrap_content时，如果不重写onMeasure方法，那么默认会填充整个父布局
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 100;//默认
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellWidth = getMeasuredWidth();
        cellHeight = getMeasuredHeight() / LETTERS.length;
    }
}
