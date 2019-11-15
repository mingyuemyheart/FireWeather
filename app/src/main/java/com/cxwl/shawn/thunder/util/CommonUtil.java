package com.cxwl.shawn.thunder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static boolean isLocationOpen(final Context context) {
        LocationManager locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int statusBarHeight(Context context) {
        int statusBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取底部导航栏高度
     * @param context
     * @return
     */
    public static int navigationBarHeight(Context context) {
        int navigationBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 读取assets下文件
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        String Result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

    /**
     * 根据当前时间获取日期，格式为MM/dd
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getDate(int i) {
        String date = null;

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day+i);

        if (c.get(Calendar.MONTH) == 0) {
            date = "01";
        }else if (c.get(Calendar.MONTH) == 1) {
            date = "02";
        }else if (c.get(Calendar.MONTH) == 2) {
            date = "03";
        }else if (c.get(Calendar.MONTH) == 3) {
            date = "04";
        }else if (c.get(Calendar.MONTH) == 4) {
            date = "05";
        }else if (c.get(Calendar.MONTH) == 5) {
            date = "06";
        }else if (c.get(Calendar.MONTH) == 6) {
            date = "07";
        }else if (c.get(Calendar.MONTH) == 7) {
            date = "08";
        }else if (c.get(Calendar.MONTH) == 8) {
            date = "09";
        }else if (c.get(Calendar.MONTH) == 9) {
            date = "10";
        }else if (c.get(Calendar.MONTH) == 10) {
            date = "11";
        }else if (c.get(Calendar.MONTH) == 11) {
            date = "12";
        }

        return date+"/"+c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据当前时间获取星期几
     * @param context
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getWeek(Context context, int i) {
        String week = null;

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day+i);

        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            week = "周日";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            week = "周一";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            week = "周二";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            week = "周三";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            week = "周四";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            week = "周五";
        }else if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            week = "周六";
        }

        return week;
    }

    /**
     * 获取listview高度
     * @param listView
     */
    public static int getListViewHeightBasedOnChildren(ListView listView) {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return height;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return height;
    }

    /**
     * 查询高德地图最新行政区划json并保存到本地文件
     * @param context
     * @param adName 行政区划对应名称
     * @param adCode 行政区划id
     */
    public static void drawOnlineDistrict(final Context context, final String adName, final String adCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DistrictSearch search = new DistrictSearch(context);
                DistrictSearchQuery query = new DistrictSearchQuery();
                query.setKeywords(adCode);//传入关键字
                query.setShowBoundary(true);//是否返回边界值
                search.setQuery(query);
                search.searchDistrictAsyn();
                search.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
                    @Override
                    public void onDistrictSearched(DistrictResult districtResult) {
                        if (districtResult == null || districtResult.getDistrict() == null) {
                            return;
                        }

                        final DistrictItem item = districtResult.getDistrict().get(0);
                        if (item == null) {
                            return;
                        }

                        String[] polyStr = item.districtBoundary();
                        if (polyStr == null || polyStr.length == 0) {
                            return;
                        }

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("name", adName);
                            obj.put("id", adCode);

                            JSONArray array1 = new JSONArray();

                            for (String str : polyStr) {
                                String[] latLng = str.split(";");
                                JSONArray array2 = new JSONArray();
                                for (String latstr : latLng) {
                                    String[] lats = latstr.split(",");
                                    JSONArray array3 = new JSONArray();
                                    try {
                                        array3.put(Double.parseDouble(lats[0]));
                                        array3.put(Double.parseDouble(lats[1]));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("array3", array3.toString());
                                    array2.put(array3);
                                }
                                Log.e("array2", array2.toString());
                                array1.put(array2);
                            }

                            Log.e("array1", array1.toString());
                            obj.put("coordinates", array1);
                            Log.e("obj", obj.toString());

                            File file = new File(Environment.getExternalStorageDirectory()+ File.separator+adCode+".json");
                            CommonUtil.writeExternalFile(file, obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });//绑定监听器
            }
        }).start();
    }

    /**
     * SDCard写入文件，注意读写权限
     * @param file
     * @param content
     */
    public static void writeExternalFile(File file, String content) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(content.getBytes());
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     * @param context
     * @param fileName
     * @return
     */
    public static String readFile(Context context, String fileName) {
        try {
            FileInputStream inputStream = context.openFileInput(fileName+".txt");
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String result = new String(arrayOutputStream.toByteArray());
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据当前时间获取日期
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getDate(String time, int i) {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        try {
            Date date = sdf2.parse(time);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DAY_OF_MONTH, i);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String date = sdf1.format(c.getTime());
        return date;
    }

    /**
     * 根据当前时间获取星期几
     * @param i (+1为后一天，-1为前一天，0表示当天)
     * @return
     */
    public static String getWeek(int i) {
        String week = "";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, i);

        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                week = "周日";
                break;
            case Calendar.MONDAY:
                week = "周一";
                break;
            case Calendar.TUESDAY:
                week = "周二";
                break;
            case Calendar.WEDNESDAY:
                week = "周三";
                break;
            case Calendar.THURSDAY:
                week = "周四";
                break;
            case Calendar.FRIDAY:
                week = "周五";
                break;
            case Calendar.SATURDAY:
                week = "周六";
                break;
        }

        return week;
    }

    /**
     * 获取当前网络连接的类型信息
     * 没有网络0：WIFI网络1：3G网络2：2G网络3
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static void saveTextSize(Context context, float textSize, int progress) {
        SharedPreferences sp = context.getSharedPreferences("text_size", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("textSize", textSize);
        editor.putInt("progress", progress);
        editor.apply();
    }

    public static float getTextSize(Context context) {
        SharedPreferences sp = context.getSharedPreferences("text_size", Context.MODE_PRIVATE);
        return sp.getFloat("textSize", 14);
    }

    public static float getProgress(Context context) {
        SharedPreferences sp = context.getSharedPreferences("text_size", Context.MODE_PRIVATE);
        return sp.getFloat("progress", 14);
    }

}
