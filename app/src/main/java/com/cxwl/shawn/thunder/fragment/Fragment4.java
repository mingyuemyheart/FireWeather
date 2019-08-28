package com.cxwl.shawn.thunder.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.ShawnIntroActivity;
import com.cxwl.shawn.thunder.ShawnLoginActivity;
import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.ShawnAboutActivity;
import com.cxwl.shawn.thunder.ShawnCheckActivity;
import com.cxwl.shawn.thunder.ShawnFeedbackActivity;
import com.cxwl.shawn.thunder.ShawnNewsActivity;
import com.cxwl.shawn.thunder.ShawnPersonInfoActivity;
import com.cxwl.shawn.thunder.ShawnProtocalActivity;
import com.cxwl.shawn.thunder.ShawnSettingActivity;
import com.cxwl.shawn.thunder.common.CONST;
import com.cxwl.shawn.thunder.common.MyApplication;
import com.cxwl.shawn.thunder.util.AuthorityUtil;
import com.cxwl.shawn.thunder.view.CircleImageView;

/**
 * 我的
 */
public class Fragment4 extends Fragment implements View.OnClickListener {

    private CircleImageView ivPortrait;
    private TextView tvUserName;
    private LinearLayout llCheck,llPublish;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shawn_fragment4, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {
        RelativeLayout rePortrait = view.findViewById(R.id.rePortrait);
        rePortrait.setOnClickListener(this);
        ivPortrait = view.findViewById(R.id.ivPortrait);
        tvUserName = view.findViewById(R.id.tvUserName);
        llCheck = view.findViewById(R.id.llCheck);
        llCheck.setOnClickListener(this);
        llPublish = view.findViewById(R.id.llPublish);
        llPublish.setOnClickListener(this);
        LinearLayout llNews = view.findViewById(R.id.llNews);
        llNews.setOnClickListener(this);
        LinearLayout llFeedback = view.findViewById(R.id.llFeedback);
        llFeedback.setOnClickListener(this);
        LinearLayout llAbout = view.findViewById(R.id.llAbout);
        llAbout.setOnClickListener(this);
        LinearLayout llSetting = view.findViewById(R.id.llSetting);
        llSetting.setOnClickListener(this);
        LinearLayout llIntro = view.findViewById(R.id.llIntro);
        llIntro.setOnClickListener(this);
        LinearLayout llProtocal = view.findViewById(R.id.llProtocal);
        llProtocal.setOnClickListener(this);

        refreshUserInfo();

    }

    private void getPortrait() {
        Bitmap bitmap = BitmapFactory.decodeFile(CONST.PORTRAIT_ADDR);
        if (bitmap != null) {
            ivPortrait.setImageBitmap(bitmap);
        }else {
            ivPortrait.setImageResource(R.drawable.shawn_icon_portrait);
        }
    }

    private void refreshUserInfo() {
        checkAuthority();

        if (!TextUtils.isEmpty(MyApplication.NICKNAME)) {
            tvUserName.setText(MyApplication.NICKNAME);
//            llCheck.setVisibility(View.VISIBLE);
//            llPublish.setVisibility(View.VISIBLE);
        }else if (!TextUtils.isEmpty(MyApplication.USERNAME)) {
            tvUserName.setText(MyApplication.USERNAME);
//            llCheck.setVisibility(View.VISIBLE);
//            llPublish.setVisibility(View.VISIBLE);
        }else {
            tvUserName.setText("未登录");
            llCheck.setVisibility(View.GONE);
            llPublish.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rePortrait:
                if (TextUtils.isEmpty(MyApplication.USERNAME)) {//登录
                    startActivityForResult(new Intent(getActivity(), ShawnLoginActivity.class), 1001);
                }else {//个人信息
                    startActivityForResult(new Intent(getActivity(), ShawnPersonInfoActivity.class), 1001);
                }
                break;
            case R.id.llCheck:
                startActivity(new Intent(getActivity(), ShawnCheckActivity.class));
                break;
            case R.id.llNews:
                startActivity(new Intent(getActivity(), ShawnNewsActivity.class));
                break;
            case R.id.llFeedback:
                startActivity(new Intent(getActivity(), ShawnFeedbackActivity.class));
                break;
            case R.id.llAbout:
                startActivity(new Intent(getActivity(), ShawnAboutActivity.class));
                break;
            case R.id.llSetting:
                startActivity(new Intent(getActivity(), ShawnSettingActivity.class));
                break;
            case R.id.llIntro:
                startActivity(new Intent(getActivity(), ShawnIntroActivity.class));
                break;
            case R.id.llProtocal:
                startActivity(new Intent(getActivity(), ShawnProtocalActivity.class));
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    refreshUserInfo();
                    break;
            }
        }
    }

    /**
     * 申请权限
     */
    private void checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            getPortrait();
        }else {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AuthorityUtil.AUTHOR_STORAGE);
            }else {
                getPortrait();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPortrait();
                }else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AuthorityUtil.intentAuthorSetting(getActivity(), "\""+getString(R.string.app_name)+"\""+"需要使用存储权限，是否前往设置？");
                    }
                }
                break;
        }
    }

}
