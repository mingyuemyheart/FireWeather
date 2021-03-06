package com.cxwl.shawn.thunder;

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.cxwl.shawn.thunder.common.MyApplication
import com.cxwl.shawn.thunder.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_modify_info.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 修改用户信息
 * @author shawn_sun
 */
class ModifyInfoActivity : ShawnBaseActivity(), OnClickListener {

	private var title: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_modify_info)
		initWidget()
	}

	/**
	 * 初始化控件
	 */
	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvControl.setOnClickListener(this)
		tvControl.visibility = View.VISIBLE
		tvControl.text = "完成"
		if (intent.hasExtra("title")) {
			title = intent.getStringExtra("title")
			if (title != null) {
				tvTitle.text = title
			}
		}
		if (intent.hasExtra("content")) {
			val content = intent.getStringExtra("content")
			if (content != null) {
				etContent.setText(content)
				etContent.setSelection(content.length)
			}
		}
	}

	/**
	 * 修改用户信息
	 */
	private fun okHttpModify() {
		val url = "http://channellive2.tianqi.cn/weather/user/update"
		val builder = FormBody.Builder()
		builder.add("token", MyApplication.TOKEN)
		builder.add("id", MyApplication.UID)
		if (TextUtils.equals(title, "昵称")) {
			builder.add("nickname", etContent!!.text.toString().trim())
		} else if (TextUtils.equals(title, "单位名称")) {
			builder.add("department", etContent!!.text.toString().trim())
		}
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
						if (!TextUtils.isEmpty(result)) {
							try {
								val `object` = JSONObject(result)
								if (!`object`.isNull("status")) {
									val status = `object`.getInt("status")
									if (status == 1) { //成功
										if (!`object`.isNull("info")) {
											val obj = `object`.getJSONObject("info")
											if (!obj.isNull("token")) {
												MyApplication.TOKEN = obj.getString("token")
											}
											if (!obj.isNull("groupid")) {
												MyApplication.GROUPID = obj.getString("groupid")
											}
											if (!obj.isNull("uid")) {
												MyApplication.UID = obj.getString("uid")
											}
											if (!obj.isNull("phonenumber")) {
												MyApplication.USERNAME = obj.getString("phonenumber")
											}
											if (!obj.isNull("nickname")) {
												MyApplication.NICKNAME = obj.getString("nickname")
											}
											if (!obj.isNull("photo")) {
												MyApplication.PHOTO = obj.getString("photo")
											}
											if (!obj.isNull("department")) {
												MyApplication.UNIT = obj.getString("department")
											}
											MyApplication.saveUserInfo(this@ModifyInfoActivity)
											setResult(Activity.RESULT_OK)
											finish()
										}
									} else { //失败
										if (!`object`.isNull("msg")) {
											Toast.makeText(this@ModifyInfoActivity, `object`.getString("msg"), Toast.LENGTH_SHORT).show()
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
			R.id.tvControl -> if (!TextUtils.isEmpty(etContent.text.toString().trim())) {
				okHttpModify()
			} else {
				if (title != null) {
					Toast.makeText(this, "请输入$title", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}
	
}
