package com.bashell.androidutilsdemo.swipe;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by bashellwang on 2016/5/1.
 * 侧拉删除自定义ViewGroup
 */
public class SwipeLayout extends FrameLayout {
    private static final String TAG = "SwipeLayout";
    private ViewDragHelper mViewDragHelper;
    private View mFrontView;
    private View mBackView;
    private int mHeight;
    private int mWidth;
    private int mRange;
    private Status mStatus = Status.Close;


    public OnSwipeLayoutListener getSwipeListener() {
        return mSwipeListener;
    }

    public void setSwipeListener(OnSwipeLayoutListener mSwipeListener) {
        this.mSwipeListener = mSwipeListener;
    }

    public OnSwipeLayoutListener mSwipeListener;

    public static enum Status {
        Close, Draging, Open
    }

    /**
     * 使用接口提供给外部调用
     */
    public interface OnSwipeLayoutListener {
        void onClose(SwipeLayout mSwipeLayout);

        void onOpen(SwipeLayout mSwipeLayout);

        void onDraging(SwipeLayout mSwipeLayout);

        //要去开启，让外部获取当前 SwipeLayout 对象
        void onStartOpen(SwipeLayout mSwipeLayout);

        //要去关闭
        void onStartClose(SwipeLayout mSwipeLayout);
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 1. 获取dragHelper
        mViewDragHelper = ViewDragHelper.create(this, mCallBack);
    }


    // 3. 重写监听事件
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //捕获拖拽view
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限定view的拖拽范围
            if (child == mFrontView) {
                if (left < -mRange) {
                    return -mRange;
                } else if (left > 0) {
                    return 0;
                }
            }
            if (child == mBackView) {
                if (left < mWidth - mRange) {
                    return mWidth - mRange;
                } else if (left > mWidth) {
                    return mWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            //这里两个view，当位置改变的时候，另外一个也需要跟着改变
            if (changedView == mFrontView) {
                mBackView.offsetLeftAndRight(dx);
            } else if (changedView == mBackView) {
                mFrontView.offsetLeftAndRight(dx);
            }

            //4. 向外暴露监听事件接口
            dispatchSwipeEvent();

            //为了兼容低版本，手动要求重绘界面
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //滑动距离超过一半
            if (xvel == 0 && mFrontView.getLeft() < -mRange / 2.0f) {
                open();
            } else if (xvel < 0) {
                //向左快速滑动
                open();
            } else {
                close();
            }
        }

    };

    /**
     * 4. 向外暴露接口提供事件处理方法
     */
    private void dispatchSwipeEvent() {
        if (mSwipeListener != null) {
            mSwipeListener.onDraging(this);
        }
        //记录之前状态
        Status preStatus = mStatus;
        //更新状态
        mStatus = updateStatus();
        if (preStatus != mStatus && mSwipeListener != null) {
            if (mStatus == Status.Close) {
                mSwipeListener.onClose(this);
            } else if (mStatus == Status.Open) {
                mSwipeListener.onOpen(this);
            } else if (mStatus == Status.Draging) {
                //接收通知来决定当前的处理动作
                if (preStatus == Status.Close) {
                    mSwipeListener.onStartOpen(this);
                } else if (preStatus == Status.Open) {
                    mSwipeListener.onStartOpen(this);
                }
            }
        }

    }

    private Status updateStatus() {
        int left = mFrontView.getLeft();
        if (left == -mRange) {
            return Status.Open;
        } else if (left == 0) {
            return Status.Close;
        } else {
            return Status.Draging;
        }
    }

    public void close() {
        close(true);
    }

    public void open() {
        open(true);
    }

    public void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            if (mViewDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(false);
        }
    }

    public void open(boolean isSmooth) {
        int finalLeft = -mRange;
        if (isSmooth) {
            if (mViewDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(true);
        }
    }

    /**
     * 配合smoothSlideViewTo 完成平滑移动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    // 2. 处理拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //持续接受事件
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取控件
        mBackView = getChildAt(0);
        mFrontView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取当前viewgroup的宽高及拖拽范围
        mHeight = mFrontView.getMeasuredHeight();
        mWidth = mFrontView.getMeasuredWidth();
        mRange = mBackView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //摆放view位置
        layoutContent(false);
    }

    /**
     * 摆放 mFrontView 和 mBackView 的相对位置 mBackView 跟在 mFrontView 后面
     *
     * @param isOpen 条目是否打开
     */
    private void layoutContent(boolean isOpen) {
        //摆放位置根据 矩形来摆放
        Rect frontRect = computeFrontViewRect(isOpen);
        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);

        Rect backRect = computeBackViewRect(frontRect);
        mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);

        //调整顺序,api23以上内部增加了 invalidate（），导致无法成功打开
//        bringChildToFront(mFrontView);
    }


    private Rect computeFrontViewRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -mRange;
        }
        return new Rect(left, 0, left + mWidth, mHeight);
    }

    private Rect computeBackViewRect(Rect frontViewRect) {
        int left = frontViewRect.right;
        return new Rect(left, 0, left + mRange, mHeight);
    }


}
