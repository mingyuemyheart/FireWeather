package com.cxwl.shawn.thunder.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyApplication extends Application {

	private static MyApplication instance;
	private static Map<String,Activity> destoryMap = new HashMap<>();

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		getUserInfo(this);

		//解决调用相机闪退问题
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
			StrictMode.setVmPolicy(builder.build());
		}
	}

	public static MyApplication getApplication() {
		return instance;
	}

	/**
     * 添加到销毁队列
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName,activity);
    }
    
	/**
	*销毁指定Activity
	*/
    public static void destoryActivity() {
       Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }

	//本地保存用户信息参数
	public static String GROUPID = "";//用户组id
	public static String UID = "";//用户id
	public static String USERNAME = "";//手机号
	public static String TOKEN = "";//token
	public static String PHOTO = "";//头像地址
	public static String NICKNAME = "";//昵称
	public static String MAIL = "";//邮箱
	public static String UNIT = "";//单位名称
	public static String ISHFADMIN = "";//审核员，1不是审核员，2是审核员

	public static String USERINFO = "userInfo";//userInfo sharedPreferance名称
	public static class UserInfo {
		public static final String groupId = "groupId";
		public static final String uid = "uid";
		public static final String userName = "uName";
		public static final String token = "token";
		public static final String photo = "photo";
		public static final String nickName = "nickName";
		public static final String mail = "mail";
		public static final String unit = "unit";
		public static final String ishfadmin = "ishfadmin";
	}

	/**
	 * 清除用户信息
	 */
	public static void clearUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		GROUPID = "";
		UID = "";
		TOKEN = "";
		USERNAME = "";
		NICKNAME = "";
		PHOTO = "";
		MAIL = "";
		UNIT = "";
		ISHFADMIN = "";
	}

	/**
	 * 保存用户信息
	 */
	public static void saveUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(UserInfo.groupId, GROUPID);
		editor.putString(UserInfo.uid, UID);
		editor.putString(UserInfo.token, TOKEN);
		editor.putString(UserInfo.userName, USERNAME);
		editor.putString(UserInfo.nickName, NICKNAME);
		editor.putString(UserInfo.photo, PHOTO);
		editor.putString(UserInfo.mail, MAIL);
		editor.putString(UserInfo.unit, UNIT);
		editor.putString(UserInfo.ishfadmin, ISHFADMIN);
		editor.apply();
	}

	/**
	 * 获取用户信息
	 */
	public static void getUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		GROUPID = sharedPreferences.getString(UserInfo.groupId, "");
		UID = sharedPreferences.getString(UserInfo.uid, "");
		TOKEN = sharedPreferences.getString(UserInfo.token, "");
		USERNAME = sharedPreferences.getString(UserInfo.userName, "");
		NICKNAME = sharedPreferences.getString(UserInfo.nickName, "");
		PHOTO = sharedPreferences.getString(UserInfo.photo, "");
		MAIL = sharedPreferences.getString(UserInfo.mail, "");
		UNIT = sharedPreferences.getString(UserInfo.unit, "");
		ISHFADMIN = sharedPreferences.getString(UserInfo.ishfadmin, "");
	}

}
