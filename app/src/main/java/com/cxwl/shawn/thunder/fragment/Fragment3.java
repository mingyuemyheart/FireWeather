package com.cxwl.shawn.thunder.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 科普
 */
public class Fragment3 extends Fragment {

    private View view;
    private LinearLayout llContainer,llContainer1;
    private MainViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private MyBroadCastReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shawn_fragment3, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBroadCast();
    }

    private void initBroadCast() {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Fragment3.class.getName());
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), Fragment3.class.getName())) {
                initWidget();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    private void initWidget() {
        llContainer = view.findViewById(R.id.llContainer);
        llContainer1 = view.findViewById(R.id.llContainer1);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        List<String> dataList = new ArrayList<>();
        dataList.add("雷电科普");
        dataList.add("雷电图库");

        llContainer.removeAllViews();
        llContainer1.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            String name = dataList.get(i);
            TextView tvName = new TextView(getActivity());
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvName.setPadding(0, (int)(dm.density*8), 0, (int)(dm.density*8));
            tvName.setOnClickListener(new MyOnClickListener(i));
            if (i == 0) {
                tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvName.setTextColor(getResources().getColor(R.color.text_color3));
            }
            tvName.setText(name);
            tvName.setTag(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.width = dm.widthPixels/2;
            tvName.setLayoutParams(params);
            llContainer.addView(tvName, i);

            TextView tvBar = new TextView(getActivity());
            tvBar.setGravity(Gravity.CENTER);
            tvBar.setTag(i);
            tvBar.setOnClickListener(new MyOnClickListener(i));
            if (i == 0) {
                tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.weight = 1.0f;
            params1.width = dm.widthPixels/2-(int)(dm.density*80);
            params1.height = (int) (dm.density*2);
            params1.setMargins((int)(dm.density*40), 0, (int)(dm.density*40), 0);
            tvBar.setLayoutParams(params1);
            llContainer1.addView(tvBar, i);

            Fragment fragment = new FragmentKepu1();
            fragments.add(fragment);
            fragment = new FragmentKepu2();
            fragments.add(fragment);

            viewPager = view.findViewById(R.id.viewPager);
            viewPager.setSlipping(false);//设置ViewPager是否可以滑动
            viewPager.setAdapter(new MyPagerAdapter());
            viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (llContainer != null) {
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    TextView tvName = (TextView) llContainer.getChildAt(i);
                    if (i == arg0) {
                        tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }else {
                        tvName.setTextColor(getResources().getColor(R.color.text_color3));
                    }
                }
            }

            if (llContainer1 != null) {
                for (int i = 0; i < llContainer1.getChildCount(); i++) {
                    TextView tvBar = (TextView) llContainer1.getChildAt(i);
                    if (i == arg0) {
                        tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }else {
                        tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
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

}
