# SearchBar
搜索条，其背景色随着界面的上下滑动而渐变
搜索条，其背景色随着界面的上下滑动而渐变。在这里考虑到搜索条的样式会根据UI进行设计，没有固定的样式，所以就直接写在了布局文件中。文中涉及到自定义轮播图控件ADViewPager
具体应用请阅读：<http://www.jianshu.com/p/3564b08db10e>
######效果图：

![SearchBar.gif](https://github.com/GitPhoenix/SearchBar/blob/master/art/SearchBar.gif)
<br>一：原理
不透明度的范围0~255，根据轮播图的高度和轮播图滑出屏幕的高度+搜索条的高度求出百分比，然后计算出不透明度值，再设置搜索条的不透明度就行了。

二：布局
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent">

    <com.github.phoenix.widget.SlidingScrollView
        android:id="@+id/slidingscrollview_search"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <com.github.phoenix.widget.ADViewPager
                android:id="@+id/ad_search_view_pager"
                android:layout_width="match_parent"
                android:layout_height="280dp"/>

            <TextView
                android:layout_width="match_parent"
                android:layout_height="600dp"
                android:text="搜索条"
                android:gravity="center"
                android:textColor="@android:color/holo_red_light"
                android:textSize="22dp"/>

        </LinearLayout>

    </com.github.phoenix.widget.SlidingScrollView>

    <!--搜索条，放在文件尾就会浮在最上层；如果放在文件首就会被SlidingScrollView盖住-->
    <RelativeLayout
        android:id="@+id/rl_search_bar"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:gravity="center_vertical"
        android:layout_alignParentTop="true"
        android:paddingLeft="6dp"
        android:paddingRight="6dp"
        android:background="@android:color/holo_red_light">

        <TextView
            android:id="@+id/tv_search_city"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:text="贵阳市"
            android:textColor="@android:color/darker_gray"
            android:layout_alignParentLeft="true"
            android:gravity="center"
            android:textSize="14sp"/>

        <LinearLayout
            android:id="@+id/ll_search"
            android:layout_width="wrap_content"
            android:layout_height="36dp"
            android:layout_centerInParent="true"
            android:layout_marginLeft="8dp"
            android:layout_marginRight="8dp"
            android:gravity="center_vertical"
            android:layout_toRightOf="@+id/tv_search_city"
            android:layout_toLeftOf="@+id/iv_search_message"
            android:orientation="horizontal"
            android:background="@drawable/shape_bg_edit">

            <ImageView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@drawable/img_search"
                android:layout_marginLeft="4dp"/>

            <EditText
                android:id="@+id/edt_search_info"
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:paddingLeft="5dp"
                android:textColor="#333"
                android:layout_weight="1"
                android:hint="搜索"
                android:textSize="18sp"
                android:maxLines="1"
                android:background="@android:color/transparent"
                android:gravity="center_vertical"/>

            <ImageView
                android:id="@+id/iv_clear_search_content"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@drawable/img_edit_clear"
                android:visibility="gone"
                android:layout_marginRight="8dp"/>

        </LinearLayout>

        <ImageView
            android:id="@+id/iv_search_message"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentRight="true"
            android:layout_centerVertical="true"
            android:background="@drawable/img_message"/>

    </RelativeLayout>

</RelativeLayout>
```

三：滑动监听
在布局文件中我们看到了SlidingScrollView，这是继承了ScrollView并重写了onScrollChanged(int l, int t, int oldl, int oldt)方法。从ScrollView的源码中我们找到了onScrollChanged这个方法，再仔细一看是包级私有的，我们无法使用，Google为什么这样做呢？虽然不可以直接用官方的控件，但是我们可以重写它，修改它的权限，这样我们就可以在外部使用了。
onScrollChanged里面有4个参数，l代表滑动后当前ScrollView可视界面的左上角在整个ScrollView的X轴中的位置，oldi也就是滑动前的X轴位置。同理，t也是当前可视界面的左上角在整个ScrollView的Y轴上的位置，oldt也就是移动前的Y轴位置。

```
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
```

四：实际应用
```
//在界面加载的时候，初始化搜索条背景为透明
rlSearchBar.getBackground().setAlpha(0);

scrollView.setOnScrollChangedListener(new SlidingScrollView.OnScrollChangedListener() {
	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		int height = adViewPager.getHeight() - rlSearchBar.getHeight();
		float fraction = (float) Math.min(Math.max(t, 0), height) / height;
		int alpha = (int) (fraction * 255);
		rlSearchBar.getBackground().setAlpha((int) (fraction * 255));
	}
});
```
在RelativeLayout 布局文件中，搜索条放在文件末尾，并且不设置相对位置，就会浮在界面最顶端，不然会被SlidingScrollView覆盖的。
