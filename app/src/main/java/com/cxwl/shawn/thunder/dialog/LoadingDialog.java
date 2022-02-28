package com.cxwl.shawn.thunder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.cxwl.shawn.thunder.R;

import java.util.Objects;

/**
 * 加载动画
 */
public class LoadingDialog extends Dialog {

	public LoadingDialog(Context context) {
		super(context);
	}
	
	public void setStyle(int featureId) {
		requestWindowFeature(featureId);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(Window.FEATURE_NO_TITLE);
		Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(R.color.transparent);
		setContentView(R.layout.dialog_loading);
	}
	
}
