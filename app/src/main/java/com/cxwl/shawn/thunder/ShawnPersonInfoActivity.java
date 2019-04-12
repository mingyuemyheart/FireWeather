package com.cxwl.shawn.thunder;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.thunder.common.CONST;
import com.cxwl.shawn.thunder.common.MyApplication;
import com.cxwl.shawn.thunder.util.AuthorityUtil;
import com.cxwl.shawn.thunder.util.GetPathFromUri4kitkat;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.cxwl.shawn.thunder.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 修改个人信息
 */
public class ShawnPersonInfoActivity extends ShawnBaseActivity implements OnClickListener {

	private Context mContext = null;
	private TextView tvNickName,tvMobile,tvUnit;
	private CircleImageView ivPortrait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_person_info);
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
		tvTitle.setText("个人信息");
		LinearLayout llPortrait = findViewById(R.id.llPortrait);
		llPortrait.setOnClickListener(this);
		ivPortrait = findViewById(R.id.ivPortrait);
		LinearLayout llNickName = findViewById(R.id.llNickName);
		llNickName.setOnClickListener(this);
		tvNickName = findViewById(R.id.tvNickName);
		tvMobile = findViewById(R.id.tvMobile);
		LinearLayout llUnit = findViewById(R.id.llUnit);
		llUnit.setOnClickListener(this);
		tvUnit = findViewById(R.id.tvUnit);
		TextView tvLogout = findViewById(R.id.tvLogout);
		tvLogout.setOnClickListener(this);

		refreshUserinfo();
	}


	private void getPortrait() {
		Bitmap bitmap = BitmapFactory.decodeFile(CONST.PORTRAIT_ADDR);
		if (bitmap != null) {
			ivPortrait.setImageBitmap(bitmap);
		}else {
			ivPortrait.setImageResource(R.drawable.shawn_icon_portrait);
		}
	}

	private void refreshUserinfo() {
		getPortrait();

		if (!TextUtils.isEmpty(MyApplication.NICKNAME)) {
			tvNickName.setText(MyApplication.NICKNAME);
		}
		if (!TextUtils.isEmpty(MyApplication.USERNAME)) {
			tvMobile.setText(MyApplication.USERNAME);
		}
		if (!TextUtils.isEmpty(MyApplication.UNIT)) {
			tvUnit.setText(MyApplication.UNIT);
		}

	}

	/**
	 * 退出登录对话框
	 */
	private void dialogLogout() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
		TextView tvContent = view.findViewById(R.id.tvContent);
		TextView tvNegtive = view.findViewById(R.id.tvNegtive);
		TextView tvPositive = view.findViewById(R.id.tvPositive);

		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();

		tvContent.setText("确定要退出登录？");
		tvNegtive.setText("取消");
		tvPositive.setText("确定");
		tvNegtive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		tvPositive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				//清除sharedPreferance里保存的用户信息
				MyApplication.clearUserInfo(mContext);

				//删除头像信息
				File file = new File(CONST.PORTRAIT_ADDR);
				if (file.exists()) {
					file.delete();
				}

				setResult(RESULT_OK);
				finish();
			}
		});
	}

	/**
	 * 获取相册
	 */
	private void getAlbum() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "false");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.llBack:
				setResult(RESULT_OK);
				finish();
				break;
			case R.id.llPortrait:
				checkStorageAuthority();
				break;
			case R.id.llNickName:
				intent = new Intent(mContext, ShawnModifyInfoActivity.class);
				intent.putExtra("title", "昵称");
				intent.putExtra("content", MyApplication.NICKNAME);
				startActivityForResult(intent, 1);
				break;
			case R.id.llUnit:
				intent = new Intent(mContext, ShawnModifyInfoActivity.class);
				intent.putExtra("title", "单位名称");
				intent.putExtra("content", MyApplication.UNIT);
				startActivityForResult(intent, 3);
				break;
			case R.id.tvLogout:
				dialogLogout();
				break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0://手机相册返回
				if (data != null) {
					Uri uri = data.getData();
					String filePath = GetPathFromUri4kitkat.getPath(mContext, uri);
					if (filePath == null) {
						Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
						return;
					}
					savePortraitFile(filePath);
				}else {
					Toast.makeText(mContext, "图片没找到", Toast.LENGTH_SHORT).show();
					return;
				}
				break;
			case 1:
				if (!TextUtils.isEmpty(MyApplication.NICKNAME)) {
					tvNickName.setText(MyApplication.NICKNAME);
				}
				break;
			case 3:
				if (!TextUtils.isEmpty(MyApplication.UNIT)) {
					tvUnit.setText(MyApplication.UNIT);
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 保存头像到本地
	 * @param imgPath
	 */
	private void savePortraitFile(String imgPath) {
		if (TextUtils.isEmpty(imgPath)) {
			return;
		}
		File file = new File(imgPath);
		if (!file.exists()) {
			return;
		}
		final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		if (bitmap == null) {
			return;
		}
		ivPortrait.setImageBitmap(bitmap);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File files = new File(CONST.SDCARD_PATH);
					if (!files.exists()) {
						files.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(CONST.PORTRAIT_ADDR);
					bitmap.compress(CompressFormat.JPEG, 50, fos);
					if (!bitmap.isRecycled()) {
						bitmap.recycle();
					}
					if (new File(CONST.PORTRAIT_ADDR).exists()) {
						OkHttpUploadPortrait();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 上传头像
	 */
	private void OkHttpUploadPortrait() {
		final String url = "http://decision-admin.tianqi.cn/home/Lightlogin/light_update";
		File file = new File(CONST.PORTRAIT_ADDR);
		if (!file.exists() || TextUtils.isEmpty(MyApplication.UID)) {
			return;
		}
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.addFormDataPart("id", MyApplication.UID);
		builder.addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
		final RequestBody body = builder.build();
		OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mContext, "上传失败！", Toast.LENGTH_SHORT).show();
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
							getPortrait();
						}
					}
				});
			}
		});
	}

	/**
	 * 申请存储权限
	 */
	private void checkStorageAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			getAlbum();
		}else {
			if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AuthorityUtil.AUTHOR_STORAGE);
			}else {
				getAlbum();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					getAlbum();
				}else {
					if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用存储权限，是否前往设置？");
					}
				}
				break;
		}
	}
	
}
