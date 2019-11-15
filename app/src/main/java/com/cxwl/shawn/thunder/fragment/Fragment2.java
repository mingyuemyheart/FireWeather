package com.cxwl.shawn.thunder.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;
import com.cxwl.shawn.thunder.util.CommonUtil;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.cxwl.shawn.thunder.view.ThunderView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 统计
 */
public class Fragment2 extends Fragment implements View.OnClickListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private View view;
    private TextureMapView mapView;
    private AMap aMap;//高德地图
    private int AMapType = AMap.MAP_TYPE_SATELLITE;
    private AMapLocationClientOption mLocationOption;//声明mLocationOption对象
    private AMapLocationClient mLocationClient;//声明AMapLocationClient类对象
    private Marker locationMarker;
    private LatLng locationLatLng;
    private TextView tvPosition,tvStreet,tvThunder;
    private LinearLayout llContainer,llContainer1,llContainer2;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd", Locale.CHINA);
    private String TYPE = "hour";//数据类型，区分小时、天等
    private GeocodeSearch geocoderSearch;
    private AVLoadingIndicatorView loadingView;
    private int width = 0;
    private MyBroadCastReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shawn_fragment2, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBroadCast();
        initAmap(savedInstanceState);
    }

    private void initBroadCast() {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Fragment2.class.getName());
        intentFilter.addAction("broadcast_textsize");
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), Fragment2.class.getName())) {
                init();
            } else if (TextUtils.equals(intent.getAction(), "broadcast_textsize")) {
                float textSize = CommonUtil.getTextSize(getActivity());
                if (tvThunder != null) {
                    tvThunder.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }
            }
        }
    }

    private void init() {
        initWidget();
    }

    private void initWidget() {
        loadingView = view.findViewById(R.id.loadingView);
        ImageView ivLocation = view.findViewById(R.id.ivLocation);
        ivLocation.setOnClickListener(this);
        tvPosition = view.findViewById(R.id.tvPosition);
        tvStreet = view.findViewById(R.id.tvStreet);
        tvThunder = view.findViewById(R.id.tvThunder);
        llContainer = view.findViewById(R.id.llContainer);
        llContainer1 = view.findViewById(R.id.llContainer1);
        llContainer2 = view.findViewById(R.id.llContainer2);

        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        float textSize = CommonUtil.getTextSize(getActivity());
        tvThunder.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        List<String> dataList = new ArrayList<>();
        dataList.add("小时,hour");
        dataList.add("日,day");
        dataList.add("旬,tendays");
        dataList.add("月,month");
        dataList.add("年,annual");

        llContainer.removeAllViews();
        llContainer1.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            String[] values = dataList.get(i).split(",");
            TextView tvName = new TextView(getActivity());
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            tvName.setPadding(0, (int)(dm.density*3), 0, (int)(dm.density*3));
            if (i == 0) {
                tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvName.setTextColor(getResources().getColor(R.color.text_color3));
            }
            tvName.setText(values[0]);
            tvName.setTag(values[1]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.width = dm.widthPixels/5;
            tvName.setLayoutParams(params);
            llContainer.addView(tvName, i);

            TextView tvBar = new TextView(getActivity());
            tvBar.setGravity(Gravity.CENTER);
            if (i == 0) {
                tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            tvBar.setTag(values[1]);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.weight = 1.0f;
            params1.width = dm.widthPixels/5-(int)(dm.density*40);
            params1.height = (int) (dm.density*2);
            params1.setMargins((int)(dm.density*20), 0, (int)(dm.density*20), 0);
            tvBar.setLayoutParams(params1);
            llContainer1.addView(tvBar, i);

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llContainer != null) {
                        for (int i = 0; i < llContainer.getChildCount(); i++) {
                            TextView tvName = (TextView) llContainer.getChildAt(i);
                            if (TextUtils.equals(tvName.getTag()+"", v.getTag()+"")) {
                                tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
                                OkHttpThunderStatistic(locationLatLng.longitude, locationLatLng.latitude, v.getTag()+"");
                            }else {
                                tvName.setTextColor(getResources().getColor(R.color.text_color3));
                            }
                        }
                    }

                    if (llContainer1 != null) {
                        for (int i = 0; i < llContainer1.getChildCount(); i++) {
                            TextView tvBar = (TextView) llContainer1.getChildAt(i);
                            if (TextUtils.equals(tvBar.getTag()+"", v.getTag()+"")) {
                                tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }else {
                                tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        }
                    }
                }
            });
        }

        if (CommonUtil.isLocationOpen(getActivity())) {
            startLocation();
        }else {
            tvPosition.setText("北京市 | 东城区");
            tvStreet.setText("正义路2号");
            locationLatLng = new LatLng(39.904030, 116.407526);
            addLocationMarker();
            OkHttpThunderStatistic(locationLatLng.longitude, locationLatLng.latitude, "hour");
        }

    }

    /**
     * 初始化高德地图
     */
    private void initAmap(Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.926628, 105.178100), 4.0f));
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.setMapType(AMapType);
        aMap.setOnMapClickListener(this);
//        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
//            @Override
//            public void onMapLoaded() {
//                if (CommonUtil.isLocationOpen(getActivity())) {
//                    startLocation();
//                }else {
//                    tvPosition.setText("北京市 | 东城区");
//                    tvStreet.setText("正义路2号");
//                    locationLatLng = new LatLng(39.904030, 116.407526);
//                    addLocationMarker();
//                    OkHttpThunderStatistic(locationLatLng.longitude, locationLatLng.latitude, "hour");
//                }
//            }
//        });
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
            mLocationClient = new AMapLocationClient(getActivity());//初始化定位
            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        }
        mLocationClient.startLocation();//启动定位
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    tvPosition.setText(aMapLocation.getCity()+" | "+aMapLocation.getDistrict());
                    tvStreet.setText(aMapLocation.getStreet()+aMapLocation.getStreetNum());
                    locationLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    addLocationMarker();
                    OkHttpThunderStatistic(locationLatLng.longitude, locationLatLng.latitude, "hour");
                }
            }
        });
    }

    private void addLocationMarker() {
        if (locationLatLng == null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.position(locationLatLng);
        options.anchor(0.5f, 1.0f);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.shawn_iv_map_click_map),
                (int)(CommonUtil.dip2px(getActivity(), 21)), (int)(CommonUtil.dip2px(getActivity(), 32)));
        if (bitmap != null) {
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }else {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.shawn_iv_map_click_map));
        }
        if (locationMarker != null) {
            locationMarker.remove();
        }
        locationMarker = aMap.addMarker(options);
        locationMarker.setClickable(false);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        searchAddrByLatLng(latLng.latitude, latLng.longitude);
        locationLatLng = latLng;
        addLocationMarker();
        OkHttpThunderStatistic(locationLatLng.longitude, locationLatLng.latitude, TYPE);
    }

    /**
     * 通过经纬度获取地理位置信息
     * @param lat
     * @param lng
     */
    private void searchAddrByLatLng(final double lat, final double lng) {
        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
        loadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP);
                geocoderSearch.getFromLocationAsyn(query);
            }
        }).start();

    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    }
    @Override
    public void onRegeocodeSearched(final RegeocodeResult result, int rCode) {
        if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingView.setVisibility(View.GONE);
                    tvPosition.setText(result.getRegeocodeAddress().getCity()+" | "+result.getRegeocodeAddress().getDistrict());
                    tvStreet.setText("");
                }
            });
        }
    }

    /**
     * 雷电统计
     * @param lng
     * @param lat
     * @param type annual 年 month 月 tendays 旬  日day (暂无等待对方提供文件) 时 hour （暂无等待对方提供文件）
     */
    private void OkHttpThunderStatistic(double lng, double lat, final String type) {
        loadingView.setVisibility(View.VISIBLE);
        this.TYPE = type;
        final String url = String.format("http://lightning.app.tianqi.cn/lightning/lhdata/ldtj?lonlat=%s,%s&type=%s", lng, lat, type);
        Log.e("url", url);
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
                                        if (!obj.isNull("reminder")) {
                                            tvThunder.setText(obj.getString("reminder"));
                                        }else {
                                            tvThunder.setText("暂无数据");
                                        }

                                        String time = null;
                                        if (!obj.isNull("time")) {
                                            time = obj.getString("time");
                                        }

                                        if (!obj.isNull("data")) {
                                            List<StrongStreamDto> list = new ArrayList<>();
                                            JSONArray array = obj.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                StrongStreamDto dto = new StrongStreamDto();
                                                dto.thunderCount = array.getInt(i);
                                                if (TextUtils.equals(type, "hour")) {
                                                    dto.thunderTime = (i+1)+"";
                                                }else if (TextUtils.equals(type, "day")) {
                                                    if (!TextUtils.isEmpty(time) && time.contains("-")) {
                                                        String[] value = time.split("-");
                                                        try {
                                                            long t = sdf1.parse(value[0]).getTime()+i*1000*60*60*24;
                                                            dto.thunderTime = sdf2.format(new Date(t).getTime());
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }else if (TextUtils.equals(type, "month")) {
                                                    dto.thunderTime = (i+1)+"";
                                                }else if (TextUtils.equals(type, "tendays")) {
                                                    dto.thunderTime = (i+1)+"";
                                                }else if (TextUtils.equals(type, "annual")) {
                                                    if (!TextUtils.isEmpty(time) && time.contains("-")) {
                                                        String[] value = time.split("-");
                                                        int t = Integer.parseInt(value[0])+i;
                                                        dto.thunderTime = t+"";
                                                    }
                                                }
                                                list.add(dto);
                                            }

                                            llContainer2.removeAllViews();
                                            ThunderView thunderView = new ThunderView(getActivity());
                                            thunderView.setData(list, type);
                                            llContainer2.addView(thunderView, width-(int)(CommonUtil.dip2px(getActivity(), 30)), (int)(CommonUtil.dip2px(getActivity(), 120)));
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
            case R.id.ivLocation:
                if (locationLatLng != null) {
                    addLocationMarker();
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(locationLatLng));
                }
                break;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

}
