package com.github.phoenix.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.phoenix.R;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果；
 * 既支持自动轮播页面也支持手势滑动切换页面
 *
 * @author Phoenix
 * @date 2016-7-13 16:26
 */
public class ADViewPager extends FrameLayout {
    private Context context;
    private ImageView[] allPage;
    private ViewPager viewPager;
    private LinearLayout dotLayout;

    private Class<?> cls;
    //自定义轮播图的资源
    private String[] imageUrls;
    //点击轮播图跳转的路径
    private String[] imageHref;
    //指示器位置
    private int pageIndicatorGravity = Gravity.RIGHT;
    //当前指示器样式
    private int indicatorDrawableChecked;
    private int indicatorDrawableUnchecked;
    //自动轮播启用开关
    private boolean isAutoPlay = true;
    //轮播指示器启用开关
    private boolean isDisplayIndicator = true;
    private int currentIndex = 0;
    //定时任务
    private ScheduledExecutorService scheduledExecutorService;

    private Handler viewPagerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem((currentIndex + 1) % allPage.length);
        }
    };

    /**
     * 执行轮播图切换任务
     */
    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                viewPagerHandler.obtainMessage().sendToTarget();
            }
        }
    }

    public ADViewPager(Context context) {
        this(context, null);
    }

    public ADViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ADViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * 初始化ADViewPager
     */
    private void initADViewPager() {
        if (imageUrls == null || imageUrls.length == 0) {
            return;
        }
        LayoutInflater.from(context).inflate(R.layout.view_ad_pager, this, true);
        dotLayout = (LinearLayout) findViewById(R.id.ll_dot);
        viewPager = (ViewPager) findViewById(R.id.viewPager_ad);
        dotLayout.removeAllViews();

        allPage = new ImageView[imageUrls.length];
        // 热点个数与图片特殊相等
        for (int i = 0; i < imageUrls.length; i++) {
            ImageView pageView = new ImageView(context);
            Glide.with(context).load(imageUrls[i]).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(pageView);
            pageView.setScaleType(ImageView.ScaleType.FIT_XY);
            allPage[i] = pageView;

            final int index = i;
            pageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (imageHref == null || imageHref.length <= index || TextUtils.isEmpty(imageHref[index]) || cls == null) {
                        return;
                    }
                    context.startActivity(new Intent(context, cls)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra("url", imageHref[index]));
                }
            });
        }

        if (isDisplayIndicator) {
            drawPageIndicator();
        }

        PagerAdapter adapter = new ADViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setFocusable(true);
        viewPager.addOnPageChangeListener(new ADViewPagerChangeListener());
    }

    /**
     * 绘制指示器
     */
    private void drawPageIndicator() {
        if (imageUrls.length <= 1) {
            return;
        }
        for (int i = 0; i < imageUrls.length; i++) {
            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;
            params.rightMargin = 4;
            dotView.setBackgroundResource(indicatorDrawableUnchecked);
            dotLayout.addView(dotView, params);
        }
        dotLayout.setGravity(pageIndicatorGravity);
        dotLayout.getChildAt(0).setBackgroundResource(indicatorDrawableChecked);
    }

    /**
     * 填充ViewPager的页面适配器
     */
    private class ADViewPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(allPage[position]);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageView imageView = allPage[position];
            ((ViewPager) container).addView(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
            return allPage == null ? 0 : allPage.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }
    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     */
    private class ADViewPagerChangeListener implements ViewPager.OnPageChangeListener {
        private boolean isScrolled;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动
                    isScrolled = false;
                    break;
                case 2:// 界面切换
                    isScrolled = true;
                    break;
                case 0:// 滑动结束
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isScrolled) {// 当前为最后一张，此时从右向左滑，则切换到第一张
                        viewPager.setCurrentItem(0);
                    } else if (viewPager.getCurrentItem() == 0 && !isScrolled) {// 当前为第一张，此时从左向右滑，则切换到最后一张
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageSelected(int arg0) {
            setCurrentDot(arg0);
        }
    }

    /**
     * 设置当前选中的指示器
     *
     * @param position
     */
    private void setCurrentDot(int position) {
        currentIndex = position;
        if (!isDisplayIndicator) {
            return;
        }
        for (int i = 0; i < dotLayout.getChildCount(); i++) {
            if (i == position) {
                dotLayout.getChildAt(position).setBackgroundResource(indicatorDrawableChecked);
            } else {
                dotLayout.getChildAt(i).setBackgroundResource(indicatorDrawableUnchecked);
            }
        }
    }

    /**
     * 初始化page 并且开始执行轮播
     *
     * @param delay  延迟轮播，单位second
     * @param period 轮播的周期，单位second
     */
    public void startPlay(long delay, long period) {
        initADViewPager();
        if (isAutoPlay && imageUrls.length > 1) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), delay, period, TimeUnit.SECONDS);
        }
    }

    /**
     * 初始化page,并且开始执行轮播，默认延迟1秒开始执行，且周期是6秒
     */
    public void startPlay() {
        startPlay(1, 6);
    }

    /**
     * 停止轮播图切换
     */
    private void stopPlay() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
        if (viewPagerHandler != null) {
            viewPagerHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 设置轮播图的Uri
     *
     * @param imageUrls
     */
    public ADViewPager setImageUri(String[] imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    /**
     * 设置点击轮播图跳转的路径
     *
     * @param imageHref
     */
    public ADViewPager setBannerHref(String[] imageHref) {
        this.imageHref = imageHref;
        return this;
    }

    /**
     * 设置是否自动播放
     *
     * @param isAutoPlay
     * @return
     */
    public ADViewPager setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    /**
     * 设置是否显示指示器
     *
     * @param isDisplayIndicator
     * @return
     */
    public ADViewPager setDisplayIndicator(boolean isDisplayIndicator) {
        this.isDisplayIndicator = isDisplayIndicator;
        return this;
    }

    /**
     * 设置指示器的位置
     *
     * @param gravity
     * @return
     */
    public ADViewPager setIndicatorGravity(int gravity) {
        this.pageIndicatorGravity = gravity;
        return this;
    }

    /**
     * 设置指示器选中时的drawable
     *
     * @param resId
     * @return
     */
    public ADViewPager setIndicatorDrawableChecked(int resId) {
        this.indicatorDrawableChecked = resId;
        return this;
    }

    /**
     * 设置指示器未选中时的drawable
     *
     * @param resId
     * @return
     */
    public ADViewPager setIndicatorDrawableUnchecked(int resId) {
        this.indicatorDrawableUnchecked = resId;
        return this;
    }

    /**
     * 设置点击AD跳转的页面
     *
     * @param cls
     * @return
     */
    public ADViewPager setTargetActivity(Class<?> cls) {
        this.cls = cls;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();
    }
}
