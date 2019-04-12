package com.cxwl.shawn.thunder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.thunder.common.CONST;
import com.cxwl.shawn.thunder.common.MyApplication;
import com.cxwl.shawn.thunder.util.AuthorityUtil;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登录界面
 */
public class ShawnLoginActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private EditText etUserName,etPwd;
	private TextView tvSend;
	private int seconds = 60;
	private Timer timer;
    private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_login);
		mContext = this;
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
		etUserName = findViewById(R.id.etUserName);
		etPwd = findViewById(R.id.etPwd);
		TextView tvLogin = findViewById(R.id.tvLogin);
		tvLogin.setOnClickListener(this);
		tvSend = findViewById(R.id.tvSend);
		tvSend.setOnClickListener(this);
	}

	/**
	 * 验证手机号码
	 */
	private boolean checkMobileInfo() {
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(mContext, "请输入手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 获取验证码
	 */
	private void OkHttpCode() {
		final String url = "http://decision-admin.tianqi.cn/home/Lightlogin/light_code";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("mobile", etUserName.getText().toString().trim());
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								resetTimer();
								Toast.makeText(mContext, "登录失败，重新登录试试", Toast.LENGTH_SHORT).show();
							}
						});
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
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("status")) {
											int status  = obj.getInt("status");
											if (status == 1) {//成功发送验证码
												//发送验证码成功
												etPwd.setFocusable(true);
												etPwd.setFocusableInTouchMode(true);
												etPwd.requestFocus();
											}else {//发送验证码失败
												if (!obj.isNull("msg")) {
													resetTimer();
													Toast.makeText(mContext, obj.getString("msg"), Toast.LENGTH_SHORT).show();
												}
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						});

					}
				});
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 101:
					if (seconds <= 0) {
						resetTimer();
					}else {
						tvSend.setText(seconds--+"s");
					}
					break;

				default:
					break;
			}
		};
	};

	/**
	 * 重置计时器
	 */
	private void resetTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		seconds = 60;
		tvSend.setText("获取验证码");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		resetTimer();
	}

	/**
	 * 验证登录信息
	 */
	private boolean checkInfo() {
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(mContext, "请输入手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etPwd.getText().toString())) {
			Toast.makeText(mContext, "请输入手机验证码", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 登录接口
	 */
	private void OkhttpLogin() {
		final String url = "http://decision-admin.tianqi.cn/home/Lightlogin/light_login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("mobile", etUserName.getText().toString().trim());
		builder.add("code", etPwd.getText().toString().trim());
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
							}
						});
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
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status  = object.getInt("status");
											if (status == 1) {//成功
												if (!object.isNull("user")) {
													JSONObject obj = object.getJSONObject("user");
													if (!obj.isNull("usergroup")) {
														MyApplication.GROUPID = obj.getString("usergroup");
													}
													if (!obj.isNull("id")) {
														MyApplication.UID = obj.getString("id");
													}
													if (!obj.isNull("mobile")) {
														MyApplication.USERNAME = obj.getString("mobile");
													}
													if (!obj.isNull("nickname")) {
														MyApplication.NICKNAME = obj.getString("nickname");
													}
													if (!obj.isNull("photo")) {
														MyApplication.PHOTO = obj.getString("photo");
														checkStorageAuthority();
													}
													if (!obj.isNull("department")) {
														MyApplication.UNIT = obj.getString("department");
													}

													MyApplication.saveUserInfo(mContext);

													setResult(RESULT_OK);
													finish();
												}
											}else {
												//失败
												if (!object.isNull("msg")) {
													String msg = object.getString("msg");
													if (msg != null) {
														Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
													}
												}
											}
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

	/**
	 * 下载头像保存在本地
	 */
	private void downloadPortrait() {
		if (TextUtils.isEmpty(MyApplication.PHOTO)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(MyApplication.PHOTO).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final byte[] bytes = response.body().bytes();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
								try {
									File files = new File(CONST.SDCARD_PATH);
									if (!files.exists()) {
										files.mkdirs();
									}
									FileOutputStream fos = new FileOutputStream(CONST.PORTRAIT_ADDR);
									if (bitmap != null) {
										bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
										if (!bitmap.isRecycled()) {
											bitmap.recycle();
										}
									}
								} catch (FileNotFoundException e) {
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
			case R.id.tvSend:
				if (timer == null) {
					if (checkMobileInfo()) {
						timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								handler.sendEmptyMessage(101);
							}
						}, 0, 1000);
						OkHttpCode();
					}
				}
				break;
			case R.id.tvLogin:
				if (checkInfo()) {
					loadingView.setVisibility(View.VISIBLE);
					OkhttpLogin();
				}
				break;

			default:
				break;
		}
	}

	/**
	 * 申请存储权限
	 */
	private void checkStorageAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			downloadPortrait();
		}else {
			if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AuthorityUtil.AUTHOR_STORAGE);
			}else {
				downloadPortrait();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					downloadPortrait();
				}else {
					if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用存储权限，是否前往设置？");
					}
				}
				break;
		}
	}
	
}
