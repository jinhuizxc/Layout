package com.example.jh.layout;

/**
 * Created by jinhui on 2018/4/9.
 * email: 1004260403@qq.com
 */

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * 上下反弹效果,可以设置背景图片
 * Created by slack on 2016/11/18.
 */
public class BounceScrollViewWithBackground extends ScrollView {

    private static final String TAG = "BounceScrollViewWithBackground";


    /**
     * 包含的View
     */
    private View innerView;
    /**
     * 存储正常时的位置
     */
    private Rect mRect = new Rect();

    private int downY, tempY, moveY;

    private boolean isFirstTouch = true;

    public BounceScrollViewWithBackground(Context context) {
        this(context, null);
    }

    public BounceScrollViewWithBackground(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BounceScrollViewWithBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isFirstTouch = true;
        // 取消滑动到顶部或底部时边缘的黄色或蓝色底纹
        if (Build.VERSION.SDK_INT >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            innerView = getChildAt(0);
        }
        super.onFinishInflate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                intercepted = true;
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (innerView != null) {
            handleTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void handleTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {

            case MotionEvent.ACTION_MOVE:

                if (isFirstTouch) {
                    downY = (int) event.getRawY();
                    moveY = downY;
                    isFirstTouch = false;
                }

                // need move...
                if ((tempY = (int) event.getRawY() - moveY) != 0) {

                    if (mRect.isEmpty()) {
                        /**
                         * 记录移动前的位置
                         */
                        mRect.set(innerView.getLeft(), innerView.getTop(),
                                innerView.getRight(), innerView.getBottom());
                    }
                    // 移动 1/2
                    innerView.layout(innerView.getLeft(), innerView.getTop() + tempY / 2,
                            innerView.getRight(), innerView.getBottom() + tempY / 2);

                }

                moveY = (int) event.getRawY();

                break;
            // 反弹回去
            case MotionEvent.ACTION_UP:
                isFirstTouch = true;
                if (!mRect.isEmpty()) {
                    resetPosition();
                }
                break;

        }
    }

    /**
     * getMeasuredHeight():the inner view height
     * getHeight()：the screen hight
     */
    private boolean isNeedMove() {
        int offset = innerView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        return scrollY == 0 || scrollY == offset;
    }


    private void resetPosition() {
        Animation animation = new TranslateAnimation(0, 0, innerView.getTop(),
                mRect.top);
        animation.setDuration(300);
        animation.setFillAfter(true);
        innerView.startAnimation(animation);
        innerView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mRect.setEmpty();
    }


}
