package com.cxwl.shawn.thunder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.thunder.fragment.Fragment1;
import com.cxwl.shawn.thunder.fragment.Fragment2;
import com.cxwl.shawn.thunder.fragment.Fragment3;
import com.cxwl.shawn.thunder.fragment.Fragment4;
import com.cxwl.shawn.thunder.util.AuthorityUtil;
import com.cxwl.shawn.thunder.util.AutoUpdateUtil;
import com.cxwl.shawn.thunder.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private Context mContext;
    private MainViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private long mExitTime;//记录点击完返回按钮后的long型时间
    private ImageView iv1, iv2, iv3, iv4;
    private TextView tv1, tv2, tv3, tv4;
    private String BROADCAST_ACTION_NAME = "";//四个fragment广播名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_main);
        mContext = this;
        AutoUpdateUtil.checkUpdate(this, mContext, "99", getString(R.string.app_name), true);
        checkMultiAuthority();
    }

    private void init() {
        initWidget();
        initViewPager();
    }

    private void initWidget() {
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        LinearLayout ll1 = findViewById(R.id.ll1);
        ll1.setOnClickListener(new MyOnClickListener(0));
        LinearLayout ll2 = findViewById(R.id.ll2);
        ll2.setOnClickListener(new MyOnClickListener(1));
        LinearLayout ll3 = findViewById(R.id.ll3);
        ll3.setOnClickListener(new MyOnClickListener(2));
        LinearLayout ll4 = findViewById(R.id.ll4);
        ll4.setOnClickListener(new MyOnClickListener(3));
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        Fragment fragment = new Fragment1();
        fragments.add(fragment);
        fragment = new Fragment2();
        fragments.add(fragment);
        fragment = new Fragment3();
        fragments.add(fragment);
        fragment = new Fragment4();
        fragments.add(fragment);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setSlipping(false);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    if (!BROADCAST_ACTION_NAME.contains(Fragment1.class.getName())) {
                        Intent intent = new Intent();
                        intent.setAction(Fragment1.class.getName());
                        sendBroadcast(intent);
                        BROADCAST_ACTION_NAME += Fragment1.class.getName();
                    }

                    iv1.setImageResource(R.drawable.tab_icon_ybon);
                    iv2.setImageResource(R.drawable.tab_icon_tj);
                    iv3.setImageResource(R.drawable.tab_icon_kp);
                    iv4.setImageResource(R.drawable.tab_icon_wd);
                    tv1.setTextColor(Color.WHITE);
                    tv2.setTextColor(getResources().getColor(R.color.text_color4));
                    tv3.setTextColor(getResources().getColor(R.color.text_color4));
                    tv4.setTextColor(getResources().getColor(R.color.text_color4));
                    break;
                case 1:
                    if (!BROADCAST_ACTION_NAME.contains(Fragment2.class.getName())) {
                        Intent intent = new Intent();
                        intent.setAction(Fragment2.class.getName());
                        sendBroadcast(intent);
                        BROADCAST_ACTION_NAME += Fragment2.class.getName();
                    }

                    iv1.setImageResource(R.drawable.tab_icon_yb);
                    iv2.setImageResource(R.drawable.tab_icon_tjon);
                    iv3.setImageResource(R.drawable.tab_icon_kp);
                    iv4.setImageResource(R.drawable.tab_icon_wd);
                    tv1.setTextColor(getResources().getColor(R.color.text_color4));
                    tv2.setTextColor(Color.WHITE);
                    tv3.setTextColor(getResources().getColor(R.color.text_color4));
                    tv4.setTextColor(getResources().getColor(R.color.text_color4));
                    break;
                case 2:
                    if (!BROADCAST_ACTION_NAME.contains(Fragment3.class.getName())) {
                        Intent intent = new Intent();
                        intent.setAction(Fragment3.class.getName());
                        sendBroadcast(intent);
                        BROADCAST_ACTION_NAME += Fragment3.class.getName();
                    }

                    iv1.setImageResource(R.drawable.tab_icon_yb);
                    iv2.setImageResource(R.drawable.tab_icon_tj);
                    iv3.setImageResource(R.drawable.tab_icon_kpon);
                    iv4.setImageResource(R.drawable.tab_icon_wd);
                    tv1.setTextColor(getResources().getColor(R.color.text_color4));
                    tv2.setTextColor(getResources().getColor(R.color.text_color4));
                    tv3.setTextColor(Color.WHITE);
                    tv4.setTextColor(getResources().getColor(R.color.text_color4));
                    break;
                case 3:
                    iv1.setImageResource(R.drawable.tab_icon_yb);
                    iv2.setImageResource(R.drawable.tab_icon_tj);
                    iv3.setImageResource(R.drawable.tab_icon_kp);
                    iv4.setImageResource(R.drawable.tab_icon_wdon);
                    tv1.setTextColor(getResources().getColor(R.color.text_color4));
                    tv2.setTextColor(getResources().getColor(R.color.text_color4));
                    tv3.setTextColor(getResources().getColor(R.color.text_color4));
                    tv4.setTextColor(Color.WHITE);
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /**
     * @ClassName: MyOnClickListener
     * @Description: TODO头标点击监听
     * @author Panyy
     * @date 2013 2013年11月6日 下午2:46:08
     *
     */
    private class MyOnClickListener implements View.OnClickListener {
        private int index;

        private MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            if (viewPager != null) {
                viewPager.setCurrentItem(index);
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(fragments.get(position).getView());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(mContext, "再按一次退出"+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return false;
    }

    /**
     * 申请多个权限
     */
    private void checkMultiAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            init();
        }else {
            AuthorityUtil.deniedList.clear();
            for (int i = 0; i < AuthorityUtil.allPermissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, AuthorityUtil.allPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    AuthorityUtil.deniedList.add(AuthorityUtil.allPermissions[i]);
                }
            }
            if (AuthorityUtil.deniedList.isEmpty()) {//所有权限都授予
                init();
            }else {
                String[] permissions = AuthorityUtil.deniedList.toArray(new String[AuthorityUtil.deniedList.size()]);//将list转成数组
                ActivityCompat.requestPermissions(MainActivity.this, permissions, AuthorityUtil.AUTHOR_MULTI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_MULTI:
                init();
                break;
        }
    }

}
