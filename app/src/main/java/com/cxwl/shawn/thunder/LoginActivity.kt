package com.cxwl.shawn.thunder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.cxwl.shawn.thunder.common.CONST
import com.cxwl.shawn.thunder.common.MyApplication
import com.cxwl.shawn.thunder.util.AuthorityUtil
import com.cxwl.shawn.thunder.util.CommonUtil
import com.cxwl.shawn.thunder.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * 登录界面
 */
class LoginActivity : ShawnBaseActivity(), View.OnClickListener {

	private var seconds = 60
	private var timer: Timer? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		initWidget()
	}

	/**
	 * 初始化控件
	 */
	private fun initWidget() {
		tvLogin.setOnClickListener(this)
		tvSend.setOnClickListener(this)

		val params : ConstraintLayout.LayoutParams = ivLogo.layoutParams as ConstraintLayout.LayoutParams
		params.width = CommonUtil.widthPixels(this)*3/4
		ivLogo.layoutParams = params
	}

	/**
	 * 验证手机号码
	 */
	private fun checkMobileInfo(): Boolean {
		if (TextUtils.isEmpty(etUserName!!.text.toString())) {
			Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show()
			return false
		}
		return true
	}

	/**
	 * 获取验证码
	 */
	private fun okHttpCode() {
		val url = "http://channellive2.tianqi.cn/Weather/User/Login3Sendcode"
		val builder = FormBody.Builder()
		builder.add("phonenumber", etUserName!!.text.toString().trim())
		val body: RequestBody = builder.build()
		Thread(Runnable {
			OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {
					runOnUiThread {
						resetTimer()
						Toast.makeText(this@LoginActivity, "登录失败，重新登录试试", Toast.LENGTH_SHORT).show()
					}
				}
				@Throws(IOException::class)
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val result = response.body!!.string()
					runOnUiThread {
						if (!TextUtils.isEmpty(result)) {
							try {
								val obj = JSONObject(result)
								if (!obj.isNull("status")) {
									val status = obj.getInt("status")
									if (status == 301) { //成功发送验证码
										//发送验证码成功
										etPwd.isFocusable = true
										etPwd.isFocusableInTouchMode = true
										etPwd.requestFocus()
									} else { //发送验证码失败
										if (!obj.isNull("msg")) {
											resetTimer()
											Toast.makeText(this@LoginActivity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
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

	@SuppressLint("HandlerLeak") val handler: Handler = object : Handler() {
		override fun handleMessage(msg: Message) {
			when (msg.what) {
				101 -> if (seconds <= 0) {
					resetTimer()
				} else {
					tvSend!!.text = seconds--.toString() + "s"
				}
			}
		}
	}

	/**
	 * 重置计时器
	 */
	private fun resetTimer() {
		if (timer != null) {
			timer!!.cancel()
			timer = null
		}
		seconds = 60
		tvSend!!.text = "获取验证码"
	}

	override fun onDestroy() {
		super.onDestroy()
		resetTimer()
	}

	/**
	 * 验证登录信息
	 */
	private fun checkInfo(): Boolean {
		if (TextUtils.isEmpty(etUserName.text.toString())) {
			Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show()
			return false
		}
		if (TextUtils.isEmpty(etPwd.text.toString())) {
			Toast.makeText(this, "请输入手机验证码", Toast.LENGTH_SHORT).show()
			return false
		}
		return true
	}

	/**
	 * 登录接口
	 */
	private fun okhttpLogin() {
		showDialog()
		val url = "http://channellive2.tianqi.cn/Weather/User/Login3"
		val builder = FormBody.Builder()
		builder.add("phonenumber", etUserName.text.toString().trim())
		builder.add("vcode", etPwd.text.toString().trim())
		val body: RequestBody = builder.build()
		Thread(Runnable {
			OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {
					runOnUiThread {
						cancelDialog()
						Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show() }
				}
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
												checkStorageAuthority()
											}
											if (!obj.isNull("department")) {
												MyApplication.UNIT = obj.getString("department")
											}
											MyApplication.saveUserInfo(this@LoginActivity)
											setResult(Activity.RESULT_OK)
											finish()
										}
									} else { //失败
										if (!`object`.isNull("msg")) {
											val msg = `object`.getString("msg")
											if (msg != null) {
												Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
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

	/**
	 * 下载头像保存在本地
	 */
	private fun downloadPortrait() {
		if (TextUtils.isEmpty(MyApplication.PHOTO)) {
			return
		}
		Thread(Runnable {
			OkHttpUtil.enqueue(Request.Builder().url(MyApplication.PHOTO).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {}
				@Throws(IOException::class)
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val bytes = response.body!!.bytes()
					runOnUiThread {
						val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
						try {
							val files = File(CONST.SDCARD_PATH)
							if (!files.exists()) {
								files.mkdirs()
							}
							val fos = FileOutputStream(CONST.PORTRAIT_ADDR)
							if (bitmap != null) {
								bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
								if (!bitmap.isRecycled) {
									bitmap.recycle()
								}
							}
						} catch (e: FileNotFoundException) {
							e.printStackTrace()
						}
					}
				}
			})
		}).start()
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.tvSend -> {
				if (timer == null) {
					if (checkMobileInfo()) {
						timer = Timer()
						timer!!.schedule(object : TimerTask() {
							override fun run() {
								handler.sendEmptyMessage(101)
							}
						}, 0, 1000)
						okHttpCode()
					}
				}
			}
			R.id.tvLogin -> {
				if (checkInfo()) {
					okhttpLogin()
				}
			}
		}
	}

	/**
	 * 申请存储权限
	 */
	private fun checkStorageAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			downloadPortrait()
		} else {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), AuthorityUtil.AUTHOR_STORAGE)
			} else {
				downloadPortrait()
			}
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			AuthorityUtil.AUTHOR_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				downloadPortrait()
			} else {
				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用存储权限，是否前往设置？")
				}
			}
		}
	}
	
}
