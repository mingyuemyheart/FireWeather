package com.cxwl.shawn.thunder.common;

import android.os.Environment;

import com.cxwl.shawn.thunder.R;

public class CONST {
	
	//下拉刷新progresBar四种颜色
	public static final int color1 = R.color.refresh_color1;
	public static final int color2 = R.color.refresh_color2;
	public static final int color3 = R.color.refresh_color3;
	public static final int color4 = R.color.refresh_color4;

	//保存路径
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory()+"/ChinaThunder";
	public static String PORTRAIT_ADDR = SDCARD_PATH + "/portrait.png";//头像保存的路径

}
