package com.cxwl.shawn.thunder

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

/**
 * 闪屏页
 */
class WelcomeActivity : ShawnBaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)

		//点击Home键后再点击APP图标，APP重启而不是回到原来界面
		if (!isTaskRoot) {
			finish()
			return
		}
		//点击Home键后再点击APP图标，APP重启而不是回到原来界面

		Handler().postDelayed({
			startActivity(Intent(this, MainActivity::class.java))
			finish()
		}, 2000)
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			return true
		}
		return super.onKeyDown(keyCode, event)
	}

}
