package com.cxwl.shawn.thunder.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.adapter.ShawnPictureLibraryAdapter;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.cxwl.shawn.thunder.view.PhotoView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 雷电图库
 */
public class FragmentKepu2 extends Fragment implements View.OnClickListener {

    private View view;
    private ShawnPictureLibraryAdapter picAdapter;
    private List<StrongStreamDto> picList = new ArrayList<>();
    private ViewPager mViewPager;
    private RelativeLayout reViewPager;
    private TextView tvCount;
    private AVLoadingIndicatorView loadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shawn_fragment_kepu2, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget();
        initGridView();
    }

    private void initWidget() {
        loadingView = view.findViewById(R.id.loadingView);
        reViewPager = view.findViewById(R.id.reViewPager);
        ImageView ivExit = view.findViewById(R.id.ivExit);
        ivExit.setOnClickListener(this);
        tvCount = view.findViewById(R.id.tvCount);

        OkHttpPictures();
    }

    private void initGridView() {
        GridView gridView = view.findViewById(R.id.gridView);
        picAdapter = new ShawnPictureLibraryAdapter(getActivity(), picList);
        gridView.setAdapter(picAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (reViewPager.getVisibility() == View.GONE) {
                    if (mViewPager != null) {
                        mViewPager.setCurrentItem(arg2);
                    }
                    scaleExpandAnimation(reViewPager);
                    reViewPager.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        ImageView[] imageArray = new ImageView[picList.size()];
        for (int i = 0; i < picList.size(); i++) {
            StrongStreamDto dto = picList.get(i);
            if (!TextUtils.isEmpty(dto.imgUrl)) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get().load(dto.imgUrl).into(imageView);
                imageArray[i] = imageView;
            }
        }

        mViewPager = view.findViewById(R.id.viewPager);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(imageArray);
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                tvCount.setText((arg0+1)+"/"+picList.size());
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private class MyViewPagerAdapter extends PagerAdapter {

        private ImageView[] mImageViews;

        private MyViewPagerAdapter(ImageView[] imageViews) {
            this.mImageViews = imageViews;
        }

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Drawable drawable = mImageViews[position].getDrawable();
            photoView.setImageDrawable(drawable);
            container.addView(photoView, 0);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    scaleColloseAnimation(reViewPager);
                    reViewPager.setVisibility(View.GONE);
                }
            });
            return photoView;
        }

    }

    /**
     * 放大动画
     * @param view
     */
    private void scaleExpandAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1.0f,0,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setDuration(300);
        animationSet.addAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);

        view.startAnimation(animationSet);
    }

    /**
     * 缩小动画
     * @param view
     */
    private void scaleColloseAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setDuration(300);
        animationSet.addAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);

        view.startAnimation(animationSet);
    }

    /**
     * 获取图库数据
     * review：审核状态（1：审核通过，0：未审核，-1：审核拒绝）
     */
    private void OkHttpPictures() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://lightning.app.tianqi.cn/lightning/lhdata/ldzp";
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("data")) {
                                            JSONArray array = obj.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                StrongStreamDto dto = new StrongStreamDto();
                                                JSONObject itemObj = array.getJSONObject(i);
                                                if (!itemObj.isNull("name")) {
                                                    dto.eventContent = itemObj.getString("name");
                                                }
                                                if (!itemObj.isNull("names")) {
                                                    dto.imgUrl = itemObj.getString("names");
                                                    picList.add(dto);
                                                }
                                            }

                                            if (picAdapter != null) {
                                                picAdapter.notifyDataSetChanged();
                                            }
                                            initViewPager();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                loadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivExit:
                if (reViewPager.getVisibility() == View.VISIBLE) {
                    scaleColloseAnimation(reViewPager);
                    reViewPager.setVisibility(View.GONE);
                }
                break;
        }
    }


}
