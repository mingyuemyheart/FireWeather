package com.cxwl.shawn.thunder

import android.os.Bundle;
import android.support.constraint.ConstraintLayout
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.util.CommonUtil;
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.ivLogo
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_title.*

/**
 * 关于我们
 */
class AboutActivity : ShawnBaseActivity(), OnClickListener{

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_about)
		initWidget()
	}

	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvTitle.text = "关于我们"
		tvVersion.text = "V" + CommonUtil.getVersion(this)

		val params : ConstraintLayout.LayoutParams = ivLogo.layoutParams as ConstraintLayout.LayoutParams
		params.width = CommonUtil.widthPixels(this)*3/4
		ivLogo.layoutParams = params
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.llBack -> finish()
		}
	}

}
