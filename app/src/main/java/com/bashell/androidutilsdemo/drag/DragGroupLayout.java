package com.bashell.androidutilsdemo.drag;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by bashellwang on 2016/4/17.
 */

/**
 * 继承FrameLayout而不是其他布局，减少其它不需要的多余的方法，如toLeftOf等
 * 只需要测量和布局即可
 */
public class DragGroupLayout extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private float WIDTH_RANGE_ARGU = 0.618f;
    private int mRange;
    private int mWidth;
    private int mHeight;
    private OnDragStatusChangedListener mDragListener;
    private Status mStatus = Status.Close;

    private static enum Status {
        Open, Close, Draging;
    }

    public interface OnDragStatusChangedListener {
        void onOpen();

        void onClose();

        void onDraging(float percent);
    }

    public void setDragStatusListener(OnDragStatusChangedListener dragListener) {
        this.mDragListener = dragListener;
    }

    public DragGroupLayout(Context context) {
        this(context, null);
    }

    public DragGroupLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGroupLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //a.获取 ViewDragHelper 实例对象，构造方法是私有的，只能通过静态方法来获取
        mViewDragHelper = ViewDragHelper.create(this, mCallBack);

    }

    //    回调方法
    //    c.重写事件
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {
        /**
         * 1.根据返回结果决定当前child是否可以被拖拽
         * 当尝试捕获时调用
         * @param child 当前被拖拽的child
         * @param pointerId 区分多点触摸的id
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //只想让 mMainContent 拖动，但是不能直接写 return child == mMainContent,因为还要让后续监听
            //事件发生；否则就屏蔽了 mLeftContent 的后续事件
            return true;
        }

        /**
         * 当capturedChild被捕获时调用
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定动画的执行速度
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        /**
         * 2.根据建议值修正将要移动到的横向位置，此时没有发生真正的移动
         * @param child 当前拖拽的子view
         * @param left 新的位置的建议值
         * @param dx 位置移动的变化量
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //控制 mMainContent的移动范围
            if (child == mMainContent) {
                left = fixLeft(left);
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //如果移动的是 mLeftContent ，则将其移动数据传给 mMainContent
            if (changedView == mLeftContent) {
                left = mMainContent.getLeft() + dx;
            }
            left = fixLeft(left);

            if (changedView == mLeftContent) {
                //移动的是左面板，则主面板移动，左面板保持不动
                mLeftContent.layout(0, 0, 0 + mWidth, mHeight);
                mMainContent.layout(left, 0, left + mWidth, mHeight);
            }
            //更新状态，执行动画
            dispatchEvent(left);
            //为了兼容低版本，手动要求重绘界面
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        /**
         * 拖动释放时调用
         * @param releasedChild
         * @param xvel 横向移动速度
         * @param yvel 纵向移动速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (xvel == 0 && mMainContent.getLeft() > mRange / 2) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }
    };

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return super.dispatchUnhandledMove(focused, direction);
    }

    /**
     * 根据当前滑动距离更新状态
     *
     * @param left
     */
    private void dispatchEvent(int left) {
        float percent = left * 1.0f / mRange;

        if (mDragListener != null) {
            mDragListener.onDraging(percent);
        }
        //更新状态
        Status preStatus = mStatus;
        mStatus = updateStatus(percent);
        if (preStatus != mStatus) {
            //状态改变
            if (mStatus == Status.Close) {
                if (mDragListener != null) {
                    mDragListener.onClose();
                }
            } else if (mStatus == Status.Open) {
                if (mDragListener != null) {
                    mDragListener.onOpen();
                }
            }
        }

        //伴随动画
        animaViews(percent);
    }

    private Status updateStatus(float percent) {
        if (percent == 0.0f) {
            return Status.Close;
        } else if (percent == 1.0f) {
            return Status.Open;
        } else {
            return Status.Draging;
        }
    }

    private void animaViews(float percent) {
        //左面板执行缩放，平移和透明度变化，属性动画不能用，为了兼容低版本，使用nineoldandroid.jar
//        mLeftContent.setScaleX(0.5f + 0.5f * percent);
//        mLeftContent.setScaleY(0.5f + 0.5f * percent);
        ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
        ViewHelper.setScaleY(mLeftContent, evaluate(percent, 0.5f, 1.0f));
        ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));
        ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth / 2.0f, 0));
        ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));

        //主面板缩放动画
        ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
        ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
        //背景色变化
        getBackground().setColorFilter((Integer) evaluateColor(percent, Color.TRANSPARENT, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    /**
     * 类型估值器 参考：TypeEvaluator-FloatEvaluator
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    private float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * 类型估值器 参考：TypeEvaluator-ArgbEvaluator
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    private Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (int) startValue;
        int endInt = (int) endValue;

        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;


        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startA + (int) (fraction * (endA - startA))) << 16) |
                (int) ((startA + (int) (fraction * (endA - startA))) << 8) |
                (int) ((startA + (int) (fraction * (endA - startA))));
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            //高频率滑动时使用，专门为动画效果实现的
            ViewCompat.postInvalidateOnAnimation(this);

        }
    }

    /**
     * 平滑打开
     *
     * @param isSmooth
     */
    private void open(boolean isSmooth) {
        if (isSmooth) {
            if (mViewDragHelper.smoothSlideViewTo(mMainContent, mRange, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(mRange, 0, mRange + mWidth, mHeight);
        }

    }

    public void open() {
        open(true);
    }

    public void close() {
        close(true);
    }

    /**
     * 平滑关闭
     *
     * @param isSmooth
     */
    private void close(boolean isSmooth) {
        if (isSmooth) {
            if (mViewDragHelper.smoothSlideViewTo(mMainContent, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(0, 0, mWidth, mHeight);
        }

    }

    /**
     * 调整左边栏滑动距离
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > mRange) {
            return mRange;
        } else {
            return left;
        }
    }


    //b.将ViewGroup的触摸事件传递给 ViewDragHelper
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            //多点触摸时有问题
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {

        }
        //持续让当前layout获取触摸事件
        return true;
    }

    //获取view界面上的控件，不通过id写死的方法 在界面填充完毕后可以获取childView
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //必须要有两个子view
        if (getChildCount() < 2) {
            throw new IllegalStateException("u must have 2 childs at least");
        }
        //容错处理
        if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("the child view must be instanceof viewgroup");
        }
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

    /**
     * 当尺寸有变化时调用
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mRange = (int) (mWidth * WIDTH_RANGE_ARGU);
    }
}
