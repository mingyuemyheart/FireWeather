package com.cxwl.shawn.thunder

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.cxwl.shawn.thunder.util.AutoUpdateUtil
import com.cxwl.shawn.thunder.util.CommonUtil
import com.cxwl.shawn.thunder.util.DataCleanManager
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.dialog_cache.view.*
import kotlinx.android.synthetic.main.layout_title.*

/**
 * 系统设置
 */
class SettingActivity : ShawnBaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initWidget()
    }

    private fun initWidget() {
        llBack.setOnClickListener(this)
        tvTitle.text = "系统设置"
        llCache.setOnClickListener(this)
        llVersion.setOnClickListener(this)
        try {
            val cache = DataCleanManager.getCacheSize(this)
            if (!TextUtils.isEmpty(cache)) {
                tvCache.text = cache
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tvVersion.text = CommonUtil.getVersion(this)
    }

    /**
     * 清除缓存
     */
    private fun dialogCache() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_cache, null)
        val dialog = Dialog(this, R.style.CustomProgressDialog)
        dialog.setContentView(view)
        dialog.show()
        view.tvContent.text = "确定要清除缓存？"
        view.tvNegtive.text = "取消"
        view.tvPositive.text = "确定"
        view.tvNegtive.setOnClickListener { dialog.dismiss() }
        view.tvPositive.setOnClickListener {
            dialog.dismiss()
            DataCleanManager.clearCache(this)
            try {
                val cache = DataCleanManager.getCacheSize(this)
                if (!TextUtils.isEmpty(cache)) {
                    tvCache!!.text = cache
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llBack -> finish()
            R.id.llCache -> dialogCache()
            R.id.llVersion -> AutoUpdateUtil.checkUpdate(this, this, "126", getString(R.string.app_name), false)
        }
    }

}
