package com.cxwl.shawn.thunder;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.thunder.common.MyApplication;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	
	private Context mContext;
	private EditText etUserName,etPwd;
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
	}

	private void doLogin() {
		if (checkInfo()) {
			loadingView.setVisibility(View.VISIBLE);
			OkHttpLogin();
		}
	}
	
	/**
	 * 验证用户信息
	 */
	private boolean checkInfo() {
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etPwd.getText().toString())) {
			Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 * 登录
	 */
	private void OkHttpLogin() {
	    final String url = "http://decision-admin.tianqi.cn/home/Work/login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("username", etUserName.getText().toString());
		builder.add("password", etPwd.getText().toString());
		builder.add("appid", "33");
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
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
                                        if (!object.isNull("status")) {
                                            int status  = object.getInt("status");
                                            if (status == 1) {//成功
                                                if (!object.isNull("info")) {
                                                    JSONObject obj = new JSONObject(object.getString("info"));
													if (!obj.isNull("groupid")) {
														MyApplication.GROUPID = obj.getString("groupid");
													}
													if (!obj.isNull("id")) {
														MyApplication.UID = obj.getString("id");
													}
													if (!obj.isNull("ishfadmin")) {
														MyApplication.ISHFADMIN = obj.getString("ishfadmin");
													}
													if (!obj.isNull("token")) {
														MyApplication.TOKEN = obj.getString("token");
													}
													if (!obj.isNull("username")) {
														MyApplication.USERNAME = obj.getString("username");
													}
													if (!obj.isNull("nickname")) {
														MyApplication.NICKNAME = obj.getString("nickname");
													}
													if (!obj.isNull("email")) {
														MyApplication.MAIL = obj.getString("email");
													}
													if (!obj.isNull("firm")) {
														MyApplication.UNIT = obj.getString("firm");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvLogin:
			doLogin();
			break;

		default:
			break;
		}
	}
	
}
