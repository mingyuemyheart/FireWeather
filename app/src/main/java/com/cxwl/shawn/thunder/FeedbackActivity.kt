package com.cxwl.shawn.thunder;

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.cxwl.shawn.thunder.common.MyApplication
import com.cxwl.shawn.thunder.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 意见反馈
 */
class FeedbackActivity : ShawnBaseActivity(), OnClickListener{

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_feedback)
		initWidget()
	}

	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvTitle.text = "意见反馈"
		tvControl.text = "提交"
		tvControl.visibility = View.VISIBLE
		tvControl.setOnClickListener(this)
	}

	/**
	 * 意见反馈
	 */
	private fun okHttpFeedback() {
		showDialog()
		val url = "http://decision-admin.tianqi.cn/home/Work/request"
		val builder = FormBody.Builder()
//		if (!TextUtils.isEmpty(MyApplication.UID)) {
//			builder.add("uid", MyApplication.UID)
//		}
		builder.add("uid", "4622")
		builder.add("content", etContent.text.toString())
		builder.add("appid", "36")
		val body: RequestBody = builder.build()
		Thread(Runnable {
			OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {}
				@Throws(IOException::class)
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val result = response.body!!.string()
					runOnUiThread {
						cancelDialog()
						if (!TextUtils.isEmpty(result)) {
							try {
								val `object` = JSONObject(result)
								if (!`object`.isNull("status")) {
									val status = `object`.getInt("status")
									if (status == 1) { //成功
										Toast.makeText(this@FeedbackActivity, "提交成功！", Toast.LENGTH_SHORT).show()
										finish()
									} else { //失败
										if (!`object`.isNull("msg")) {
											val msg = `object`.getString("msg")
											if (msg != null) {
												Toast.makeText(this@FeedbackActivity, msg, Toast.LENGTH_SHORT).show()
											}
										}
									}
								}
							} catch (e: JSONException) {
								e.printStackTrace()
							}
						}
					}
				}
			})
		}).start()
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.llBack -> finish()
			R.id.tvControl -> {
				if (TextUtils.isEmpty(etContent!!.text.toString())) {
					Toast.makeText(this, "请填写意见内容...", Toast.LENGTH_SHORT).show()
					return
				}
				okHttpFeedback()
			}
		}
	}

}
