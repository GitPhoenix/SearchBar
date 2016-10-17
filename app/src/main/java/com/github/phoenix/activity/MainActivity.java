package com.github.phoenix.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.phoenix.R;
import com.github.phoenix.widget.ADViewPager;
import com.github.phoenix.widget.SlidingScrollView;

public class MainActivity extends AppCompatActivity {
    private SlidingScrollView scrollView;
    private RelativeLayout rlSearchBar;
    private EditText edtSearchInfo;
    private ImageView ivClear, ivMsg;
    private TextView tvCity;

    private ADViewPager adViewPager;
    //轮播图片资源，这是同淘宝上获取的链接，实际项目中从网络获取
    private String[] imageUrls = {"http://img13.360buyimg.com/n8/jfs/t2494/199/2974963318/623734/7b6fe7e5/572c1618N7ee89e63.jpg",
            "http://img13.360buyimg.com/n8/jfs/t2827/183/199606439/285948/d507257d/5707b5f3N0df2f00b.jpg",
            "http://img12.360buyimg.com/n7/jfs/t1255/168/901893672/288658/677b172c/555c00d6N0d7c93c4.jpg",
            "http://img11.360buyimg.com/n8/jfs/t2773/14/1006959733/136923/a687b77d/5731878cNd2855d6f.jpg",
            "http://img13.360buyimg.com/cms/s190x190_jfs/t2581/258/2147586971/249027/5b84a329/5757fc91Nd8bcb260.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setSubView();
        initEvent();
    }

    private void initView() {
            scrollView = (SlidingScrollView) findViewById(R.id.slidingscrollview_search);
            rlSearchBar = (RelativeLayout) findViewById(R.id.rl_search_bar);
            ivClear = (ImageView) findViewById(R.id.iv_clear_search_content);
            ivMsg = (ImageView) findViewById(R.id.iv_search_message);
            tvCity = (TextView) findViewById(R.id.tv_search_city);
            edtSearchInfo = (EditText) findViewById(R.id.edt_search_info);

            adViewPager = (ADViewPager) findViewById(R.id.ad_search_view_pager);
    }

    private void setSubView() {
        //初始化搜索条背景为透明
        rlSearchBar.getBackground().setAlpha(0);

        adViewPager.setIndicatorDrawableChecked(R.drawable.shape_dot_focus) //当前指示点
                .setIndicatorDrawableUnchecked(R.drawable.shape_dot_blur) //非当前指示点
                .setAutoPlay(true) //是否开启自动轮播
                .setDisplayIndicator(true) //是否显示指示器
                .setIndicatorGravity(Gravity.RIGHT) //指示器位置
                .setImageUri(imageUrls)  //图片路径
                .setBannerHref(null)  //点击图片跳转的路径
                .setTargetActivity(null)  //点击图片跳转的webView页面
                .startPlay();
    }

    private void initEvent() {
        //不透明度的范围0~255，根据轮播图的高度和滑出屏幕的高度求出百分比，然后计算出不透明度值，再设置搜索条的不透明度就行了
        scrollView.setOnScrollChangedListener(new SlidingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                int height = adViewPager.getHeight() - rlSearchBar.getHeight();
                float fraction = (float) Math.min(Math.max(t, 0), height) / height;
                int alpha = (int) (fraction * 255);
                rlSearchBar.getBackground().setAlpha((int) (fraction * 255));
            }
        });

        edtSearchInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //当编辑框中有内容才显示删除按钮
                if (!TextUtils.isEmpty(s.toString())) {
                    ivClear.setVisibility(View.VISIBLE);
                } else {
                    ivClear.setVisibility(View.GONE);
                }
            }
        });

        //清除搜索框中内容
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchInfo.setText("");
            }
        });

        ivMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "消息中心敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "百度定位敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
