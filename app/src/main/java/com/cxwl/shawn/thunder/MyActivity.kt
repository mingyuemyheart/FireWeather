package com.cxwl.shawn.thunder;

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.cxwl.shawn.thunder.common.CONST
import com.cxwl.shawn.thunder.common.MyApplication
import com.cxwl.shawn.thunder.util.AuthorityUtil
import kotlinx.android.synthetic.main.activity_my.*
import kotlinx.android.synthetic.main.layout_title.*

/**
 * 我的
 */
class MyActivity : ShawnBaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        initWidget()
    }

    private fun initWidget() {
        tvTitle.text = "我的"
        clPortrait.setOnClickListener(this)
        llCheck.setOnClickListener(this)
        llPublish.setOnClickListener(this)
        llNews.setOnClickListener(this)
        llFeedback.setOnClickListener(this)
        llAbout.setOnClickListener(this)
        llSetting.setOnClickListener(this)
        llProtocal.setOnClickListener(this)

        refreshUserInfo()
    }

    private fun getPortrait() {
        val bitmap = BitmapFactory.decodeFile(CONST.PORTRAIT_ADDR)
        if (bitmap != null) {
            ivPortrait!!.setImageBitmap(bitmap)
        } else {
            ivPortrait!!.setImageResource(R.drawable.icon_portrait)
        }
    }

    private fun refreshUserInfo() {
        checkAuthority()
        if (!TextUtils.isEmpty(MyApplication.NICKNAME)) {
            tvUserName!!.text = MyApplication.NICKNAME
            //            llCheck.setVisibility(View.VISIBLE);
//            llPublish.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(MyApplication.USERNAME)) {
            tvUserName!!.text = MyApplication.USERNAME
            //            llCheck.setVisibility(View.VISIBLE);
//            llPublish.setVisibility(View.VISIBLE);
        } else {
            tvUserName!!.text = "未登录"
            llCheck!!.visibility = View.GONE
            llPublish.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.clPortrait -> if (TextUtils.isEmpty(MyApplication.USERNAME)) { //登录
                startActivityForResult(Intent(this, LoginActivity::class.java), 1001)
            } else { //个人信息
                startActivityForResult(Intent(this, PersonInfoActivity::class.java), 1001)
            }
            R.id.llCheck -> startActivity(Intent(this, ShawnCheckActivity::class.java))
            R.id.llNews -> startActivity(Intent(this, NewsActivity::class.java))
            R.id.llFeedback -> startActivity(Intent(this, FeedbackActivity::class.java))
            R.id.llAbout -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.llSetting -> startActivity(Intent(this, SettingActivity::class.java))
//            R.id.llProtocal -> startActivity(Intent(this, ShawnProtocalActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1001 -> refreshUserInfo()
            }
        }
    }

    /**
     * 申请权限
     */
    private fun checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            getPortrait()
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), AuthorityUtil.AUTHOR_STORAGE)
            } else {
                getPortrait()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AuthorityUtil.AUTHOR_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPortrait()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AuthorityUtil.intentAuthorSetting(this, "\"" + getString(R.string.app_name) + "\"" + "需要使用存储权限，是否前往设置？")
                }
            }
        }
    }

}
