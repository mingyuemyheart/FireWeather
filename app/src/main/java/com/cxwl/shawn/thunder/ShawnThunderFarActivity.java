package com.cxwl.shawn.thunder;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 雷电有多远
 */
public class ShawnThunderFarActivity extends ShawnBaseActivity implements View.OnClickListener {

    private TextView tvLook,tvSound,tvLine1,tvLine2,tvThunderLevel1,tvThunderLevel2,tvThunderLevel3,tvThunderLevel4,tvThunderLevel5;
    private ImageView ivThunderClick;
    private RelativeLayout reResult;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private long clickTime = 0;//记录点击时间
    private int clickCount = 0;//点击次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_thunder_far);
        initWidget();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.app_name));
        TextView tvControl = findViewById(R.id.tvControl);
        tvControl.setText("重置");
        tvControl.setVisibility(View.VISIBLE);
        tvControl.setOnClickListener(this);
        tvLook = findViewById(R.id.tvLook);
        tvSound = findViewById(R.id.tvSound);
        tvLine1 = findViewById(R.id.tvLine1);
        tvLine2 = findViewById(R.id.tvLine2);
        ivThunderClick = findViewById(R.id.ivThunderClick);
        ivThunderClick.setOnClickListener(this);
        reResult = findViewById(R.id.reResult);
        tvThunderLevel1 = findViewById(R.id.tvThunderLevel1);
        tvThunderLevel2 = findViewById(R.id.tvThunderLevel2);
        tvThunderLevel3 = findViewById(R.id.tvThunderLevel3);
        tvThunderLevel4 = findViewById(R.id.tvThunderLevel4);
        tvThunderLevel5 = findViewById(R.id.tvThunderLevel5);

        reset();
    }

    private void reset() {
        clickCount = 0;
        clickTime = 0;
        tvLook.setText("看到闪电点击");
        tvLine1.setVisibility(View.GONE);
        tvSound.setText("听到声音点击");
        tvSound.setVisibility(View.GONE);
        tvLine2.setVisibility(View.GONE);
        ivThunderClick.setVisibility(View.VISIBLE);
        reResult.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.tvControl:
                reset();
                break;
            case R.id.ivThunderClick:
                clickCount++;
                if (clickCount == 1) {
                    tvLook.setText("看到闪电"+sdf1.format(new Date()));
                    tvLine1.setVisibility(View.VISIBLE);
                    tvSound.setText("听到声音点击");
                    tvSound.setVisibility(View.VISIBLE);
                    clickTime = System.currentTimeMillis();
                }else if (clickCount == 2) {
                    long time = (System.currentTimeMillis() - clickTime);
                    tvSound.setText("听到声音"+sdf1.format(new Date()));
                    tvLine2.setVisibility(View.VISIBLE);
                    reResult.setVisibility(View.VISIBLE);
                    ivThunderClick.setVisibility(View.GONE);
                    if (time <= 3000) {
                        tvThunderLevel1.setBackgroundResource(R.drawable.shawn_icon_thunder_level1);
                        tvThunderLevel2.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel3.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel4.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel5.setBackgroundColor(Color.TRANSPARENT);
                    }else if (time <= 6000) {
                        tvThunderLevel1.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel2.setBackgroundResource(R.drawable.shawn_icon_thunder_level2);
                        tvThunderLevel3.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel4.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel5.setBackgroundColor(Color.TRANSPARENT);
                    }else if (time <= 12000) {
                        tvThunderLevel1.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel2.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel3.setBackgroundResource(R.drawable.shawn_icon_thunder_level3);
                        tvThunderLevel4.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel5.setBackgroundColor(Color.TRANSPARENT);
                    }else if (time <= 20000) {
                        tvThunderLevel1.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel2.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel3.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel4.setBackgroundResource(R.drawable.shawn_icon_thunder_level4);
                        tvThunderLevel5.setBackgroundColor(Color.TRANSPARENT);
                    }else if (time <= 30000) {
                        tvThunderLevel1.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel2.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel3.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel4.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel5.setBackgroundResource(R.drawable.shawn_icon_thunder_level5);
                    }else {
                        tvThunderLevel1.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel2.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel3.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel4.setBackgroundColor(Color.TRANSPARENT);
                        tvThunderLevel5.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
                break;
        }
    }

}
