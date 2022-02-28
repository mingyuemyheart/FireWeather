package com.cxwl.shawn.thunder;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.cxwl.shawn.thunder.dialog.LoadingDialog;

public class ShawnBaseActivity extends Activity{

	private LoadingDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 23) {
			ShawnBaseActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}

	public void showDialog() {
		if (mDialog == null) {
			mDialog = new LoadingDialog(this);
		}
		mDialog.show();
	}

	public void cancelDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelDialog();//解决activity已经销毁，而还在调用dialog
	}
	
}
