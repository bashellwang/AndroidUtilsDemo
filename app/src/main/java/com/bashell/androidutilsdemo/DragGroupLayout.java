package com.bashell.androidutilsdemo;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by bashellwang on 2016/4/17.
 */

/**
 * 继承FrameLayout而不是其他布局，减少其它不需要的多余的方法，如toLeftOf等
 * 只需要测量和布局即可
 */
public class DragGroupLayout extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private View mLeftContent;
    private View mMainContent;
    private float WIDTH_RANGE_ARGU = 0.618f;
    private float mRange;

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

        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
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
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };


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
        mLeftContent = getChildAt(0);
        mMainContent = getChildAt(1);
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
        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();

        mRange = mWidth *  WIDTH_RANGE_ARGU;
    }
}
