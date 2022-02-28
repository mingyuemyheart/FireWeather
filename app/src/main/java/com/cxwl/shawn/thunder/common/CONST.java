package com.cxwl.shawn.thunder.common;

import android.os.Environment;

import com.cxwl.shawn.thunder.R;

public class CONST {
	
	//下拉刷新progresBar四种颜色
	public static final int color1 = R.color.refresh_color1;
	public static final int color2 = R.color.refresh_color2;
	public static final int color3 = R.color.refresh_color3;
	public static final int color4 = R.color.refresh_color4;

	public static final String WEB_URL = "web_Url";//网页地址的标示
	public static final String ACTIVITY_NAME = "activity_name";//界面名称
	public static final String LOCAL_ID = "local_id";//local_id
	public static final String COLUMN_ID = "column_id";//column_id

	public static String noValue = "--";

	//通用
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory()+"/FireWeather";
	public static String PORTRAIT_ADDR = SDCARD_PATH + "/portrait.png";//头像保存的路径
	public static String VIDEO_ADDR = SDCARD_PATH + "/video";//拍摄视频保存的路径
	public static String OLD_VIDEO_ADDR = SDCARD_PATH + "/videofile";//拍摄视频保存的路径
	public static String THUMBNAIL_ADDR = SDCARD_PATH + "/thumbnail";//缩略图保存的路径
	public static String PICTURE_ADDR = SDCARD_PATH + "/picture";//拍照保存的路径
	public static String VIDEOTYPE = ".mp4";//mp4格式播放视频要快，比.3gp速度快很多
	public static String PICTURETYPE = ".jpg";
	public static int TIME = 120;//视频录制时间限定为120秒

}
