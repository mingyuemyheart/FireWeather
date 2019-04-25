package com.cxwl.shawn.thunder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cxwl.shawn.thunder.util.AuthorityUtil;
import com.cxwl.shawn.thunder.util.CommonUtil;
import com.cxwl.shawn.thunder.util.GetPathFromUri4kitkat;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 雷电上报
 */
public class ShawnThunderUploadActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private LinearLayout llAdd;
    private TextView tvTextCount,tvImgType,tvEventType,tvPosition,tvTime;
    private EditText etContent;
    private ImageView imageView;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private AMapLocationClientOption mLocationOption;//声明mLocationOption对象
    private AMapLocationClient mLocationClient;//声明AMapLocationClient类对象
    private AVLoadingIndicatorView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_thunder_upload);
        mContext = this;
        checkMultiAuthority();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("雷电上报");
        TextView tvControl = findViewById(R.id.tvControl);
        tvControl.setText("发布");
        tvControl.setOnClickListener(this);
        tvControl.setVisibility(View.VISIBLE);
        etContent = findViewById(R.id.etContent);
        etContent.addTextChangedListener(textWatcher);
        tvTextCount = findViewById(R.id.tvTextCount);
        llAdd = findViewById(R.id.llAdd);
        llAdd.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        tvImgType = findViewById(R.id.tvImgType);
        tvEventType = findViewById(R.id.tvEventType);
        tvPosition = findViewById(R.id.tvPosition);
        tvTime = findViewById(R.id.tvTime);
        tvTime.setText(sdf1.format(new Date()));
        LinearLayout llEventType = findViewById(R.id.llEventType);
        llEventType.setOnClickListener(this);

        if (CommonUtil.isLocationOpen(this)) {
            startLocation();
        }else {
            tvPosition.setText("北京市|东城区");
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();//初始化定位参数
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
            mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        }
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);//初始化定位
            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        }
        mLocationClient.startLocation();//启动定位
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    tvPosition.setText(aMapLocation.getCity()+aMapLocation.getDistrict());
                }
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            tvTextCount.setText("("+s.length()+"/100)");
        }
    };

    private boolean checkCondition() {
        if (TextUtils.isEmpty(etContent.getText().toString())) {
            Toast.makeText(mContext, "说点什么吧！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageView.getVisibility() != View.VISIBLE) {
            Toast.makeText(mContext, "请选取一张照片！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvImgType.getText().toString())) {
            Toast.makeText(mContext, "请选择图片类型！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvEventType.getText().toString())) {
            Toast.makeText(mContext, "请选择事件行业！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 上传图片
     */
    private void OkHttpUpload() {
        final String url = "http://decision-admin.tianqi.cn/Home/other/light_upload_imgs";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("imgtype", tvImgType.getText().toString());
        builder.addFormDataPart("type", tvEventType.getText().toString());
        builder.addFormDataPart("addr", tvPosition.getText().toString());
        builder.addFormDataPart("time", tvTime.getText().toString());
        builder.addFormDataPart("content", etContent.getText().toString());
        String imgPath = imageView.getTag()+"";
        File imgFile = new File(imgPath);
        if (imgFile.exists()) {
            builder.addFormDataPart("pic1", imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
        }
        final RequestBody body = builder.build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
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
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("code")) {
                                        String code = obj.getString("code");
                                        if (TextUtils.equals(code, "200")) {
                                            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                                            loadingView.setVisibility(View.GONE);
                                            finish();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
            case R.id.llBack:
                finish();
                break;
            case R.id.llAdd:
            case R.id.imageView:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1001);
                break;
            case R.id.llEventType:
                startActivityForResult(new Intent(mContext, ShawnEventTypeActivity.class), 1002);
                break;
            case R.id.tvControl:
                if (checkCondition()) {
                    loadingView.setVisibility(View.VISIBLE);
                    OkHttpUpload();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001://读取相册
                    if (data != null) {
                        Uri uri = data.getData();
                        String filePath = GetPathFromUri4kitkat.getPath(mContext, uri);
                        if (filePath == null) {
                            Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag(filePath);
                            imageView.setVisibility(View.VISIBLE);
                            llAdd.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 1002://获取事件类型
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            String eventType = bundle.getString("eventType");
                            if (!TextUtils.isEmpty(eventType)) {
                                tvEventType.setText(eventType);
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    //需要申请的所有权限
    private String[] allPermissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 申请多个权限
     */
    private void checkMultiAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            initWidget();
        }else {
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < allPermissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, allPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(allPermissions[i]);
                }
            }
            if (deniedList.isEmpty()) {//所有权限都授予
                initWidget();
            }else {
                String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
                ActivityCompat.requestPermissions(ShawnThunderUploadActivity.this, permissions, AuthorityUtil.AUTHOR_MULTI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_MULTI:
                initWidget();
                break;
        }
    }

}
