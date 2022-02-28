package com.cxwl.shawn.thunder;

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.cxwl.shawn.thunder.common.CONST
import com.cxwl.shawn.thunder.common.MyApplication
import com.cxwl.shawn.thunder.util.AuthorityUtil
import com.cxwl.shawn.thunder.util.GetPathFromUri4kitkat
import com.cxwl.shawn.thunder.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_person_info.*
import kotlinx.android.synthetic.main.dialog_cache.view.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * 修改个人信息
 */
class PersonInfoActivity : ShawnBaseActivity(), View.OnClickListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_person_info)
		initWidget()
	}

	/**
	 * 初始化控件
	 */
	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvTitle.text = "个人信息"
		llPortrait.setOnClickListener(this)
		llNickName.setOnClickListener(this)
		llUnit.setOnClickListener(this)
		tvLogout.setOnClickListener(this)
		refreshUserinfo()
	}

	private fun getPortrait() {
		val bitmap = BitmapFactory.decodeFile(CONST.PORTRAIT_ADDR)
		if (bitmap != null) {
			ivPortrait!!.setImageBitmap(bitmap)
		} else {
			ivPortrait!!.setImageResource(R.drawable.icon_portrait)
		}
	}

	private fun refreshUserinfo() {
		getPortrait()
		if (!TextUtils.isEmpty(MyApplication.NICKNAME)) {
			tvNickName.text = MyApplication.NICKNAME
		}
		if (!TextUtils.isEmpty(MyApplication.USERNAME)) {
			tvMobile.text = MyApplication.USERNAME
		}
		if (!TextUtils.isEmpty(MyApplication.UNIT)) {
			tvUnit.text = MyApplication.UNIT
		}
	}

	/**
	 * 退出登录对话框
	 */
	private fun dialogLogout() {
		val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val view = inflater.inflate(R.layout.dialog_cache, null)
		val dialog = Dialog(this, R.style.CustomProgressDialog)
		dialog.setContentView(view)
		dialog.show()
		view.tvContent.text = "确定要退出登录？"
		view.tvNegtive.text = "取消"
		view.tvPositive.text = "确定"
		view.tvNegtive.setOnClickListener { dialog.dismiss() }
		view.tvPositive.setOnClickListener {
			dialog.dismiss()
			//清除sharedPreferance里保存的用户信息
			MyApplication.clearUserInfo(this)
			//删除头像信息
			val file = File(CONST.PORTRAIT_ADDR)
			if (file.exists()) {
				file.delete()
			}
			setResult(Activity.RESULT_OK)
			finish()
		}
	}

	/**
	 * 获取相册
	 */
	private fun getAlbum() {
		val intent = Intent()
		intent.action = Intent.ACTION_GET_CONTENT
		intent.type = "image/*"
		intent.putExtra("crop", "false")
		intent.putExtra("aspectX", 1)
		intent.putExtra("aspectY", 1)
		intent.putExtra("outputX", 150)
		intent.putExtra("outputY", 150)
		intent.putExtra("return-data", true)
		startActivityForResult(intent, 0)
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(Activity.RESULT_OK)
			finish()
		}
		return super.onKeyDown(keyCode, event)
	}

	override fun onClick(v: View) {
		val intent: Intent
		when (v.id) {
			R.id.llBack -> {
				setResult(Activity.RESULT_OK)
				finish()
			}
			R.id.llPortrait -> checkStorageAuthority()
			R.id.llNickName -> {
				intent = Intent(this, ModifyInfoActivity::class.java)
				intent.putExtra("title", "昵称")
				intent.putExtra("content", MyApplication.NICKNAME)
				startActivityForResult(intent, 1)
			}
			R.id.llUnit -> {
				intent = Intent(this, ModifyInfoActivity::class.java)
				intent.putExtra("title", "单位名称")
				intent.putExtra("content", MyApplication.UNIT)
				startActivityForResult(intent, 3)
			}
			R.id.tvLogout -> dialogLogout()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			when (requestCode) {
				0 -> if (data != null) {
					val uri = data.data
					val filePath = GetPathFromUri4kitkat.getPath(this, uri)
					if (filePath == null) {
						Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show()
						return
					}
					savePortraitFile(filePath)
				} else {
					Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show()
					return
				}
				1 -> if (!TextUtils.isEmpty(MyApplication.NICKNAME)) {
					tvNickName!!.text = MyApplication.NICKNAME
				}
				3 -> if (!TextUtils.isEmpty(MyApplication.UNIT)) {
					tvUnit.text = MyApplication.UNIT
				}
			}
		}
	}

	/**
	 * 保存头像到本地
	 * @param imgPath
	 */
	private fun savePortraitFile(imgPath: String) {
		if (TextUtils.isEmpty(imgPath)) {
			return
		}
		val file = File(imgPath)
		if (!file.exists()) {
			return
		}
		val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return
		ivPortrait!!.setImageBitmap(bitmap)
		Thread(Runnable {
			try {
				val files = File(CONST.SDCARD_PATH)
				if (!files.exists()) {
					files.mkdirs()
				}
				val fos = FileOutputStream(CONST.PORTRAIT_ADDR)
				bitmap.compress(CompressFormat.JPEG, 50, fos)
				if (!bitmap.isRecycled) {
					bitmap.recycle()
				}
				if (File(CONST.PORTRAIT_ADDR).exists()) {
					okHttpUploadPortrait()
				}
			} catch (e: FileNotFoundException) {
				e.printStackTrace()
			}
		}).start()
	}

	/**
	 * 上传头像
	 */
	private fun okHttpUploadPortrait() {
		val url = "http://channellive2.tianqi.cn/weather/user/update"
		val file = File(CONST.PORTRAIT_ADDR)
		if (!file.exists() || TextUtils.isEmpty(MyApplication.UID)) {
			return
		}
		val builder = MultipartBody.Builder()
		builder.addFormDataPart("id", MyApplication.UID)
		builder.addFormDataPart("token", MyApplication.TOKEN)
		builder.addFormDataPart("photo", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
		val body: RequestBody = builder.build()
		OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
			override fun onFailure(call: Call, e: IOException) {
				runOnUiThread { Toast.makeText(this@PersonInfoActivity, "上传失败！", Toast.LENGTH_SHORT).show() }
			}
			@Throws(IOException::class)
			override fun onResponse(call: Call, response: Response) {
				if (!response.isSuccessful) {
					return
				}
				val result = response.body!!.string()
				runOnUiThread {
					if (!TextUtils.isEmpty(result)) {
						getPortrait()
					}
				}
			}
		})
	}

	/**
	 * 申请存储权限
	 */
	private fun checkStorageAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			getAlbum()
		} else {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), AuthorityUtil.AUTHOR_STORAGE)
			} else {
				getAlbum()
			}
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			AuthorityUtil.AUTHOR_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getAlbum()
			} else {
				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用存储权限，是否前往设置？")
				}
			}
		}
	}
	
}
