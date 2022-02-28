package com.cxwl.shawn.thunder.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.cxwl.shawn.thunder.CameraActivity
import com.cxwl.shawn.thunder.OnlinePictureActivity
import com.cxwl.shawn.thunder.OnlineVideoActivity
import com.cxwl.shawn.thunder.R
import com.cxwl.shawn.thunder.adapter.DisasterReportAdapter
import com.cxwl.shawn.thunder.adapter.DisasterReportListviewAdapter
import com.cxwl.shawn.thunder.dto.PhotoDto
import com.cxwl.shawn.thunder.util.AnimationUtil
import com.cxwl.shawn.thunder.util.AuthorityUtil
import com.cxwl.shawn.thunder.util.CommonUtil
import com.cxwl.shawn.thunder.util.OkHttpUtil
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.fragment_disater_report.*
import kotlinx.android.synthetic.main.fragment_disater_scene.mapView
import kotlinx.android.synthetic.main.marker_icon.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * 灾情报告
 */
class DisaterReportFragment : Fragment(), View.OnClickListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    private var aMap : AMap? = null
    private val dataList : ArrayList<PhotoDto> = ArrayList()
    private val markers : ArrayList<Marker> = ArrayList()
    private var inflater : LayoutInflater? = null
    private var mReceiver: MyBroadCastReceiver? = null
    private val sdf5 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val dateTime = 60 * 60 * 24 * 2 //咨询、直报获取数据天数

    private var mAdapter: DisasterReportListviewAdapter? = null
    private val selectList : ArrayList<PhotoDto> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_disater_report, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBroadCast()
        initAmap(savedInstanceState)
    }

    private fun initBroadCast() {
        mReceiver = MyBroadCastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(DisaterReportFragment::class.java.name)
        intentFilter.addAction("broadcast_report")
        activity!!.registerReceiver(mReceiver, intentFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mReceiver != null) {
            activity!!.unregisterReceiver(mReceiver)
        }
    }

    private inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (TextUtils.equals(intent.action, DisaterReportFragment::class.java.name)) {
                initWidget()
            } else if (TextUtils.equals(intent.action, "broadcast_report")) {
                val isShowReportMap = intent.extras.getBoolean("isShowReportMap", true)
                if (isShowReportMap) {
                    AnimationUtil.toBottomAnimation(gridView)
                } else {
                    AnimationUtil.toTopAnimation(gridView)
                }
            }
        }
    }

    private fun initWidget() {
        ivCamera.setOnClickListener(this)
        inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        initListview()
    }

    /**
     * 初始化高德地图
     */
    private fun initAmap(savedInstanceState : Bundle?) {
        mapView.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = mapView.map
        }
        aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(35.926628, 105.178100), 4.0f))
        aMap!!.mapType = AMap.MAP_TYPE_SATELLITE
        aMap!!.uiSettings.isMyLocationButtonEnabled = false// 设置默认定位按钮是否显示
        aMap!!.uiSettings.isZoomControlsEnabled = false
        aMap!!.uiSettings.isRotateGesturesEnabled = false
        aMap!!.setOnMarkerClickListener(this)
        aMap!!.setOnMapClickListener(this)
        aMap!!.setOnMapLoadedListener { okHttpList() }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        selectList.clear()
        for (i in dataList.indices) {
            val dto: PhotoDto = dataList[i]
            val latLng = marker!!.title.split(",").toTypedArray()
            if (TextUtils.equals(latLng[0], dto.lat) && TextUtils.equals(latLng[1], dto.lng)) {
                selectList.add(dto)
            }
        }
        if (mAdapter != null) {
            mAdapter!!.notifyDataSetChanged()
            setListViewHeight(listView, selectList.size, 70f, 140f, 140f)
        }
        if (listView.visibility == View.GONE) {
            AnimationUtil.toTopAnimation(listView)
        }
        return true
    }

    override fun onMapClick(p0: LatLng?) {
        if (listView.visibility == View.VISIBLE) {
            AnimationUtil.toBottomAnimation(listView)
        }
    }

    /**
     * 设置listview高度
     * @param listView
     * @param size
     */
    private fun setListViewHeight(listView: ListView, size: Int, height1: Float, height2: Float, height3: Float) {
        val params = listView.layoutParams
        when {
            size == 1 -> {
                params.height = CommonUtil.dip2px(activity, height1).toInt()
            }
            size == 2 -> {
                params.height = CommonUtil.dip2px(activity, height2).toInt()
            }
            size > 2 -> {
                params.height = CommonUtil.dip2px(activity, height3).toInt()
            }
        }
        listView.layoutParams = params
    }

    /**
     * 获取直报
     */
    private fun okHttpList() {
        Thread(Runnable {
            val url = "http://channellive2.tianqi.cn/weather//Work2019/GetWork"
            val builder = FormBody.Builder()
            builder.add("appid", "31")
            builder.add("pagesize", "100")
            val body: RequestBody = builder.build()
            OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val `object` = JSONObject(result)
                                if (!`object`.isNull("status")) {
                                    val status = `object`.getInt("status")
                                    if (status == 1) { //成功
                                        if (!`object`.isNull("info")) {
                                            dataList.clear()
                                            val array = JSONArray(`object`.getString("info"))
                                            for (i in 0 until array.length()) {
                                                val obj = array.opt(i) as JSONObject
                                                val dto = PhotoDto()
                                                if (!obj.isNull("id")) {
                                                    dto.setVideoId(obj.getString("id"))
                                                }
                                                if (!obj.isNull("uid")) {
                                                    dto.uid = obj.getString("uid")
                                                }
                                                if (!obj.isNull("title")) {
                                                    dto.setTitle(obj.getString("title"))
                                                }
                                                if (!obj.isNull("create_time")) {
                                                    dto.setCreateTime(obj.getString("create_time"))
                                                }
                                                if (!obj.isNull("latlon")) {
                                                    val latlon = obj.getString("latlon")
                                                    if (!TextUtils.isEmpty(latlon) && !TextUtils.equals(latlon, ",")) {
                                                        val latLngArray = latlon.split(",").toTypedArray()
                                                        dto.lat = latLngArray[0]
                                                        dto.lng = latLngArray[1]
                                                    }
                                                }
                                                if (!obj.isNull("location")) {
                                                    dto.setLocation(obj.getString("location"))
                                                }
                                                if (!obj.isNull("nickname")) {
                                                    dto.nickName = obj.getString("nickname")
                                                }
                                                if (!obj.isNull("username")) {
                                                    dto.setUserName(obj.getString("username"))
                                                }
                                                if (!obj.isNull("picture")) {
                                                    dto.portraitUrl = obj.getString("picture")
                                                }
                                                if (!obj.isNull("phonenumber")) {
                                                    dto.phoneNumber = obj.getString("phonenumber")
                                                }
                                                if (!obj.isNull("praise")) {
                                                    dto.setPraiseCount(obj.getString("praise"))
                                                }
                                                if (!obj.isNull("comments")) {
                                                    dto.setCommentCount(obj.getString("comments"))
                                                }
                                                if (!obj.isNull("work_time")) {
                                                    dto.setWorkTime(obj.getString("work_time"))
                                                }
                                                if (!obj.isNull("workstype")) {
                                                    dto.setWorkstype(obj.getString("workstype"))
                                                }
                                                if (!obj.isNull("videoshowtime")) {
                                                    dto.showTime = obj.getString("videoshowtime")
                                                }
                                                if (!obj.isNull("worksinfo")) {
                                                    val worksinfo = obj.getString("worksinfo")
                                                    if (!TextUtils.isEmpty(worksinfo)) {
                                                        val workObj = JSONObject(worksinfo)
                                                        //视频
                                                        if (!workObj.isNull("video")) {
                                                            val video = workObj.getJSONObject("video")
                                                            if (!video.isNull("ORG")) { //腾讯云结构解析
                                                                val ORG = video.getJSONObject("ORG")
                                                                if (!ORG.isNull("url")) {
                                                                    dto.videoUrl = ORG.getString("url")
                                                                }
                                                                if (!video.isNull("SD")) {
                                                                    val SD = video.getJSONObject("SD")
                                                                    if (!SD.isNull("url")) {
                                                                        dto.sd = SD.getString("url")
                                                                    }
                                                                }
                                                                if (!video.isNull("HD")) {
                                                                    val HD = video.getJSONObject("HD")
                                                                    if (!HD.isNull("url")) {
                                                                        dto.hd = HD.getString("url")
                                                                        dto.videoUrl = HD.getString("url")
                                                                    }
                                                                }
                                                                if (!video.isNull("FHD")) {
                                                                    val FHD = video.getJSONObject("FHD")
                                                                    if (!FHD.isNull("url")) {
                                                                        dto.fhd = FHD.getString("url")
                                                                    }
                                                                }
                                                            } else {
                                                                dto.videoUrl = video.getString("url")
                                                            }
                                                        }
                                                        if (!workObj.isNull("thumbnail")) {
                                                            val imgObj = JSONObject(workObj.getString("thumbnail"))
                                                            if (!imgObj.isNull("url")) {
                                                                dto.setUrl(imgObj.getString("url"))
                                                            }
                                                        }
                                                        //图片
                                                        val urlList: MutableList<String> = java.util.ArrayList()
                                                        if (!workObj.isNull("imgs1")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs1"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                                dto.setUrl(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs2")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs2"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs3")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs3"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs4")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs4"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs5")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs5"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs6")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs6"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs7")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs7"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs8")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs8"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        if (!workObj.isNull("imgs9")) {
                                                            val imgObj = JSONObject(workObj.getString("imgs9"))
                                                            if (!imgObj.isNull("url")) {
                                                                urlList.add(imgObj.getString("url"))
                                                            }
                                                        }
                                                        dto.setUrlList(urlList)
                                                    }
                                                }
                                                dataList.add(dto)
//                                                try {
//                                                    if (!TextUtils.isEmpty(dto.workTime)) {
//                                                        val currentDate = System.currentTimeMillis() / 1000
//                                                        val beforeDate: Long = currentDate - dateTime
//                                                        val workDate: Long = sdf5.parse(dto.workTime).time / 1000
//                                                        if (workDate in beforeDate..currentDate) {
//                                                            if (!TextUtils.isEmpty(dto.lat) && !TextUtils.isEmpty(dto.lng)) {
//                                                                dataList.add(dto)
//                                                            }
//                                                        }
//                                                    }
//                                                } catch (e: ParseException) {
//                                                    e.printStackTrace()
//                                                }
                                            }
                                        }
                                        if (dataList.size > 0) {
                                            removeMarkers(markers)
                                            addZhibaoMarkers()
                                            initGridview()
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

    private fun removeMarkers(markers: ArrayList<Marker>) {
        for (i in markers.indices) {
            val marker = markers[i]
            marker.remove()
        }
        markers.clear()
    }

    /**
     * 添加直报markers
     */
    private fun addZhibaoMarkers() {
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in dataList.indices) {
            val dto: PhotoDto = dataList[i]
            ImageLoader.getInstance().loadImage(dto.url, object : ImageLoadingListener {
                override fun onLoadingComplete(p0: String?, p1: View?, bitmap: Bitmap?) {
                    val lat: Double = java.lang.Double.valueOf(dto.lat)
                    val lng: Double = java.lang.Double.valueOf(dto.lng)
                    val optionsTemp = MarkerOptions()
                    optionsTemp.title(dto.lat.toString() + "," + dto.lng)
                    optionsTemp.anchor(0.5f, 0.5f)
                    optionsTemp.position(LatLng(lat, lng))
                    val mView: View = inflater.inflate(R.layout.marker_icon_report, null)
                    if (bitmap != null) {
                        mView.ivMarker.setImageBitmap(bitmap)
                    }
                    optionsTemp.icon(BitmapDescriptorFactory.fromView(mView))
                    val marker = aMap!!.addMarker(optionsTemp)
                    markers.add(marker)
                }
                override fun onLoadingStarted(p0: String?, p1: View?) {
                }
                override fun onLoadingCancelled(p0: String?, p1: View?) {
                }
                override fun onLoadingFailed(p0: String?, p1: View?, p2: FailReason?) {
                }
            })

        }
    }

    private fun initListview() {
        mAdapter = DisasterReportListviewAdapter(activity!!, selectList)
        listView.adapter = mAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val dto: PhotoDto = selectList[position]
            intentDetail(dto)
        }
    }

    private fun initGridview() {
        val mAdapter = DisasterReportAdapter(activity!!, dataList)
        gridView.adapter = mAdapter
        gridView.setOnItemClickListener { parent, view, position, id ->
            val dto: PhotoDto = dataList[position]
            intentDetail(dto)
        }
    }

    private fun intentDetail(dto: PhotoDto) {
        val intent = Intent()
        if (dto.getWorkstype() == "imgs") {
            intent.setClass(activity, OnlinePictureActivity::class.java)
        } else {
            intent.setClass(activity, OnlineVideoActivity::class.java)
        }
        val bundle = Bundle()
        bundle.putParcelable("data", dto)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.ivCamera -> {
                checkAuthority()
            }
        }
    }

    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        if (mapView != null) {
            mapView.onResume()
        }
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        if (mapView != null) {
            mapView.onPause()
        }
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
        if (mapView != null) {
            mapView.onSaveInstanceState(outState)
        }
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mapView != null) {
            mapView.onDestroy()
        }
    }

    //需要申请的所有权限
    var allPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //拒绝的权限集合
    var deniedList: ArrayList<String> = ArrayList()

    /**
     * 申请相机权限
     */
    private fun checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            startActivity(Intent(activity, CameraActivity::class.java))
        } else {
            deniedList.clear()
            for (i in allPermissions.indices) {
                if (ContextCompat.checkSelfPermission(activity!!, allPermissions[i]) !== PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(allPermissions[i])
                }
            }
            if (deniedList.isEmpty()) { //所有权限都授予
                startActivity(Intent(activity, CameraActivity::class.java))
            } else {
                val permissions = deniedList.toTypedArray() //将list转成数组
                requestPermissions(permissions, AuthorityUtil.AUTHOR_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AuthorityUtil.AUTHOR_CAMERA -> if (grantResults.isNotEmpty()) {
                var isAllGranted = true //是否全部授权
                var i = 0
                while (i < grantResults.size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false
                        break
                    }
                    i++
                }
                if (isAllGranted) { //所有权限都授予
                    startActivity(Intent(activity, CameraActivity::class.java))
                } else { //只要有一个没有授权，就提示进入设置界面设置
                    AuthorityUtil.intentAuthorSetting(activity, "\"" + getString(R.string.app_name) + "\"" + "需要使用您的相机权限、存储权限，是否前往设置？")
                }
            } else {
                var i = 0
                while (i < permissions.size) {
                    if (!activity!!.shouldShowRequestPermissionRationale(permissions[i])) {
                        AuthorityUtil.intentAuthorSetting(activity, "\"" + getString(R.string.app_name) + "\"" + "需要使用您的相机权限、存储权限，是否前往设置？")
                        break
                    }
                    i++
                }
            }
        }
    }

}
