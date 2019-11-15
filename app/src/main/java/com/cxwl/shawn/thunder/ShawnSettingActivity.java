package com.cxwl.shawn.thunder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.util.AutoUpdateUtil;
import com.cxwl.shawn.thunder.util.CommonUtil;
import com.cxwl.shawn.thunder.util.DataCleanManager;

/**
 * 系统设置
 */
public class ShawnSettingActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private TextView tvCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_setting);
        mContext = this;
        initWidget();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("系统设置");
        LinearLayout llCache = findViewById(R.id.llCache);
        llCache.setOnClickListener(this);
        LinearLayout llVersion = findViewById(R.id.llVersion);
        llVersion.setOnClickListener(this);
        tvCache = findViewById(R.id.tvCache);
        TextView tvVersion = findViewById(R.id.tvVersion);
        LinearLayout llTextSize = findViewById(R.id.llTextSize);
        llTextSize.setOnClickListener(this);

        try {
            String cache = DataCleanManager.getCacheSize(mContext);
            if (!TextUtils.isEmpty(cache)) {
                tvCache.setText(cache);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvVersion.setText(CommonUtil.getVersion(mContext));
    }

    /**
     * 清除缓存
     */
    private void dialogCache() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText("确定要清除缓存？");
        tvNegtive.setText("取消");
        tvPositive.setText("确定");
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                DataCleanManager.clearCache(mContext);
                try {
                    String cache = DataCleanManager.getCacheSize(mContext);
                    if (!TextUtils.isEmpty(cache)) {
                        tvCache.setText(cache);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.llCache:
                dialogCache();
                break;
            case R.id.llVersion:
                AutoUpdateUtil.checkUpdate(this, mContext, "99", getString(R.string.app_name), false);
                break;
            case R.id.llTextSize:
                startActivity(new Intent(mContext, ShawnTextsizeActivity.class));
                break;

        }
    }

}
