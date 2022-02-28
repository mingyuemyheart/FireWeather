package com.cxwl.shawn.thunder

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.cxwl.shawn.thunder.adapter.MyPagerAdapter
import com.cxwl.shawn.thunder.fragment.DisaterSceneFragment
import com.cxwl.shawn.thunder.fragment.DisaterReportFragment
import com.cxwl.shawn.thunder.fragment.PdfListFragment
import com.cxwl.shawn.thunder.util.AuthorityUtil
import com.cxwl.shawn.thunder.util.AutoUpdateUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseFragmentActivity(), View.OnClickListener {

    private val fragments : ArrayList<Fragment> = ArrayList()
    private var mExitTime : Long = 0//记录点击完返回按钮后的long型时间
    private var BROADCAST_ACTION_NAME = "" //四个fragment广播名字
    private var isShowObserveMap = true //fragment1
    private var isShowReportMap = true //fragment2
    private var selectPager = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkMultiAuthority()
    }

    private fun init() {
        initWidget()
        initViewPager()
    }

    private fun initWidget() {
        AutoUpdateUtil.checkUpdate(this, this, "126", getString(R.string.app_name), true)
        ivSetting.setOnClickListener(this)
        ivControl.setOnClickListener(this)
        iv1.setOnClickListener(MyOnClickListener(0, viewPager))
        iv2.setOnClickListener(MyOnClickListener(1, viewPager))
        iv3.setOnClickListener(MyOnClickListener(2, viewPager))
        tv1.setOnClickListener(MyOnClickListener(0, viewPager))
        tv2.setOnClickListener(MyOnClickListener(1, viewPager))
        tv3.setOnClickListener(MyOnClickListener(2, viewPager))
    }

    /**
     * 初始化viewPager
     */
    private fun initViewPager() {
        fragments.add(DisaterSceneFragment())
        fragments.add(DisaterReportFragment())
        fragments.add(PdfListFragment())
        viewPager!!.setSlipping(false) //设置ViewPager是否可以滑动
        viewPager!!.offscreenPageLimit = fragments.size
        viewPager!!.adapter = MyPagerAdapter(supportFragmentManager, fragments)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }
            override fun onPageSelected(p0: Int) {
                selectPager = p0
                when (p0) {
                    0 -> {
                        if (!BROADCAST_ACTION_NAME.contains(DisaterSceneFragment::class.java.name)) {
                            val intent = Intent()
                            intent.action = DisaterSceneFragment::class.java.name
                            sendBroadcast(intent)
                            BROADCAST_ACTION_NAME += DisaterSceneFragment::class.java.name
                        }
                        if (isShowObserveMap) {
                            ivControl.setImageResource(R.drawable.icon_switch_list)
                        } else {
                            ivControl.setImageResource(R.drawable.icon_switch_map)
                        }
                        tvTitle.text = tv1.text.toString()
                        ivControl.visibility = View.VISIBLE
                        iv1.setImageResource(R.drawable.tab_icon_oneon)
                        iv2.setImageResource(R.drawable.tab_icon_twooff)
                        iv3.setImageResource(R.drawable.tab_icon_threeoff)
                        tv1.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.yellow))
                        tv2.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        tv3.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    }
                    1 -> {
                        if (!BROADCAST_ACTION_NAME.contains(DisaterReportFragment::class.java.name)) {
                            val intent = Intent()
                            intent.action = DisaterReportFragment::class.java.name
                            sendBroadcast(intent)
                            BROADCAST_ACTION_NAME += DisaterReportFragment::class.java.name
                        }
                        if (isShowReportMap) {
                            ivControl.setImageResource(R.drawable.icon_switch_list)
                        } else {
                            ivControl.setImageResource(R.drawable.icon_switch_map)
                        }
                        tvTitle.text = tv2.text.toString()
                        ivControl.visibility = View.VISIBLE
                        iv1.setImageResource(R.drawable.tab_icon_oneoff)
                        iv2.setImageResource(R.drawable.tab_icon_twoon)
                        iv3.setImageResource(R.drawable.tab_icon_threeoff)
                        tv1.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        tv2.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.yellow))
                        tv3.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    }
                    2 -> {
                        if (!BROADCAST_ACTION_NAME.contains(PdfListFragment::class.java.name)) {
                            val intent = Intent()
                            intent.action = PdfListFragment::class.java.name
                            sendBroadcast(intent)
                            BROADCAST_ACTION_NAME += PdfListFragment::class.java.name
                        }
                        tvTitle.text = tv3.text.toString()
                        ivControl.visibility = View.GONE
                        iv1.setImageResource(R.drawable.tab_icon_oneoff)
                        iv2.setImageResource(R.drawable.tab_icon_twooff)
                        iv3.setImageResource(R.drawable.tab_icon_threeon)
                        tv1.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        tv2.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        tv3.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.yellow))
                    }
                }
            }
        })
    }

    /**
     * @ClassName: MyOnClickListener
     */
    private class MyOnClickListener internal constructor(i: Int, viewPager: ViewPager) : View.OnClickListener {
        private var index = 0
        private var viewPager : ViewPager? = null
        override fun onClick(v: View?) {
            if (viewPager != null) {
                viewPager!!.currentItem = index
            }
        }
        init {
            index = i
            this.viewPager = viewPager
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按一次退出" + getString(R.string.app_name), Toast.LENGTH_SHORT).show()
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.ivSetting -> startActivity(Intent(this, MyActivity::class.java))
            R.id.ivControl -> {
                when(selectPager) {
                    0 -> {
                        isShowObserveMap = !isShowObserveMap
                        if (isShowObserveMap) {
                            ivControl.setImageResource(R.drawable.icon_switch_list)
                        } else {
                            ivControl.setImageResource(R.drawable.icon_switch_map)
                        }
                        val intent = Intent()
                        intent.action = "broadcast_observe"
                        val bundle = Bundle()
                        bundle.putBoolean("isShowObserveMap", isShowObserveMap)
                        intent.putExtras(bundle)
                        sendBroadcast(intent)
                    }
                    1 -> {
                        isShowReportMap = !isShowReportMap
                        if (isShowReportMap) {
                            ivControl.setImageResource(R.drawable.icon_switch_list)
                        } else {
                            ivControl.setImageResource(R.drawable.icon_switch_map)
                        }
                        val intent = Intent()
                        intent.action = "broadcast_report"
                        val bundle = Bundle()
                        bundle.putBoolean("isShowReportMap", isShowReportMap)
                        intent.putExtras(bundle)
                        sendBroadcast(intent)
                    }
                }
            }
        }
    }

    /**
     * 申请多个权限
     */
    private fun checkMultiAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            init()
        } else {
            AuthorityUtil.deniedList.clear()
            for (i in AuthorityUtil.allPermissions.indices) {
                if (ContextCompat.checkSelfPermission(this, AuthorityUtil.allPermissions[i]) !== PackageManager.PERMISSION_GRANTED) {
                    AuthorityUtil.deniedList.add(AuthorityUtil.allPermissions[i])
                }
            }
            if (AuthorityUtil.deniedList.isEmpty()) { //所有权限都授予
                init()
            } else {
                val permissions = AuthorityUtil.deniedList.toTypedArray() //将list转成数组
                ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_MULTI)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AuthorityUtil.AUTHOR_MULTI -> init()
        }
    }

}
