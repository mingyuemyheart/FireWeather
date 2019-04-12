package com.cxwl.shawn.thunder;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.thunder.common.MyApplication;
import com.cxwl.shawn.thunder.util.OkHttpUtil;

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
 * 修改用户信息
 * @author shawn_sun
 */
public class ShawnModifyInfoActivity extends ShawnBaseActivity implements OnClickListener{
	
	private Context mContext;
	private EditText etContent;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_modify_info);
		mContext = this;
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		etContent = findViewById(R.id.etContent);
		TextView tvControl = findViewById(R.id.tvControl);
		tvControl.setOnClickListener(this);
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setText("完成");

		if (getIntent().hasExtra("title")) {
			title = getIntent().getStringExtra("title");
			if (title != null) {
				tvTitle.setText(title);
			}
		}
		
		if (getIntent().hasExtra("content")) {
			String content = getIntent().getStringExtra("content");
			if (content != null) {
				etContent.setText(content);
				etContent.setSelection(content.length());
			}
		}
	}
	
	/**
	 * 修改用户信息
	 */
	private void OkHttpModify() {
		final String url = "http://decision-admin.tianqi.cn/home/Lightlogin/light_update";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("id", MyApplication.UID);
		if (TextUtils.equals(title, "昵称")) {
			builder.add("nickname", etContent.getText().toString().trim());
		}else if (TextUtils.equals(title, "单位名称")) {
			builder.add("department", etContent.getText().toString().trim());
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
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status = object.getInt("status");
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
													}
													if (!obj.isNull("department")) {
														MyApplication.UNIT = obj.getString("department");
													}

													MyApplication.saveUserInfo(mContext);

													setResult(RESULT_OK);
													finish();
												}
											}else {//失败
												if (!object.isNull("msg")) {
													Toast.makeText(mContext, object.getString("msg"), Toast.LENGTH_SHORT).show();
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvControl:
			if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
				OkHttpModify();
			}else {
				if (title != null) {
					Toast.makeText(mContext, "请输入"+title, Toast.LENGTH_SHORT).show();
				}
			}
			break;

		default:
			break;
		}
	}
	
}
