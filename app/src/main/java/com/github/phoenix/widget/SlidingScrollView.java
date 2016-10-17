package com.github.phoenix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 监听ScrollView的滑动
 *
 * @author Phoenix
 * @date 2016-7-15 10:34
 */
public class SlidingScrollView extends ScrollView {
    private OnScrollChangedListener onScrollChangedListener;

    public SlidingScrollView(Context context) {
        this(context, null);
    }

    public SlidingScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    /**
     * 滑动监听，对外提供的方法
     * @param onScrollChangedListener
     */
    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }
}
