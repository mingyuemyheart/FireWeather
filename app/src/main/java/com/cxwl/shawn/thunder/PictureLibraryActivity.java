package com.cxwl.shawn.thunder;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.adapter.PictureLibraryAdapter;
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
 * 图库
 */
public class PictureLibraryActivity extends BaseActivity implements View.OnClickListener {

    private int page = 1;
    private PictureLibraryAdapter picAdapter;
    private List<StrongStreamDto> picList = new ArrayList<>();
    private ViewPager mViewPager;
    private RelativeLayout reViewPager;
    private TextView tvCount;
    private AVLoadingIndicatorView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_picture_library);
        initWidget();
        initGridView();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("图库");
        reViewPager = findViewById(R.id.reViewPager);
        ImageView ivExit = findViewById(R.id.ivExit);
        ivExit.setOnClickListener(this);
        tvCount = findViewById(R.id.tvCount);

        OkHttpPictures();
    }

    private void initGridView() {
        GridView gridView = findViewById(R.id.gridView);
        picAdapter = new PictureLibraryAdapter(this, picList);
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
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
                    page += 1;
                    OkHttpPictures();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get().load(dto.imgUrl).into(imageView);
                imageArray[i] = imageView;
            }
        }

        mViewPager = findViewById(R.id.viewPager);
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
        final String url = String.format("http://decision-admin.tianqi.cn/Home/other/light_get_upload_imgs/review/%s/page/%s/pageSize/%s", 1, page, 30);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            StrongStreamDto dto = new StrongStreamDto();
                                            JSONObject itemObj = array.getJSONObject(i);
                                            if (!itemObj.isNull("content")) {
                                                dto.eventContent = itemObj.getString("content");
                                            }
                                            if (!itemObj.isNull("imgs")) {
                                                JSONArray imgs = itemObj.getJSONArray("imgs");
                                                if (imgs.length() > 0) {
                                                    dto.imgUrl = imgs.getString(0);
                                                    picList.add(dto);
                                                }
                                            }
                                        }

                                        if (picAdapter != null) {
                                            picAdapter.notifyDataSetChanged();
                                        }
                                        initViewPager();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (reViewPager.getVisibility() == View.VISIBLE) {
            scaleColloseAnimation(reViewPager);
            reViewPager.setVisibility(View.GONE);
            return false;
        }else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.ivExit:
                if (reViewPager.getVisibility() == View.VISIBLE) {
                    scaleColloseAnimation(reViewPager);
                    reViewPager.setVisibility(View.GONE);
                }else {
                    finish();
                }
                break;
        }
    }

}
