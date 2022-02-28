package com.cxwl.shawn.thunder.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.cxwl.shawn.thunder.R
import com.cxwl.shawn.thunder.adapter.TitleListviewAdapter
import com.cxwl.shawn.thunder.common.CONST
import com.cxwl.shawn.thunder.dto.DisasterSceneDto
import com.cxwl.shawn.thunder.dto.StationDto
import com.cxwl.shawn.thunder.dto.WindData
import com.cxwl.shawn.thunder.dto.WindDto
import com.cxwl.shawn.thunder.util.*
import com.cxwl.shawn.thunder.view.*
import kotlinx.android.synthetic.main.fragment_disater_scene.*
import kotlinx.android.synthetic.main.marker_icon.view.*
import kotlinx.android.synthetic.main.marker_info.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 救灾现场
 */
class DisaterSceneFragment : Fragment(), View.OnClickListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, AMap.OnCameraChangeListener {

    private var aMap : AMap? = null
    private var isShowWind = false
    private var isShowScope = false
    private var isShowStation = true
    private var isShowObserve = true
    private val dataList : ArrayList<DisasterSceneDto> = ArrayList()
    private val disasterMarkers : ArrayList<Marker> = ArrayList()
    private var inflater : LayoutInflater? = null
    private var locationLat : Double = 39.904030
    private var locationLng : Double = 116.407526
    private var shiziMarker : Marker? = null
    private var selectedSpot : DisasterSceneDto? = null//当前选中的灾害点

    private val markerTypeDisaster = "markerTypeDisaster"
    private val markerTypeStation = "markerTypeStation"
    private val markerTypeObserve = "markerTypeObserve"

    private var mReceiver: MyBroadCastReceiver? = null

    //风场
    private var windDataGFS: WindData? = null
    private var waitWindView: WaitWindView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_disater_scene, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkMultiAuthority()
        initBroadCast()
        initWidget()
        initAmap(savedInstanceState)
    }


    private fun initBroadCast() {
        mReceiver = MyBroadCastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(DisaterSceneFragment::class.java.name)
        intentFilter.addAction("broadcast_observe")
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
            if (TextUtils.equals(intent.action, DisaterSceneFragment::class.java.name)) {

            } else if (TextUtils.equals(intent.action, "broadcast_observe")) {
                val isShowObserveMap = intent.extras.getBoolean("isShowObserveMap", true)
                if (isShowObserveMap) {
                    AnimationUtil.toBottomAnimation(clList)
                } else {
                    AnimationUtil.toTopAnimation(clList)
                    initList()
                }
            }
        }
    }

    private fun initWidget() {
        ivWindField.setOnClickListener(this)
        ivScope.setOnClickListener(this)
        ivStation.setOnClickListener(this)
        ivObserve.setOnClickListener(this)
        ivClose.setOnClickListener(this)

        inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        val mLocationOption = AMapLocationClientOption() //初始化定位参数
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.isNeedAddress = true //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isOnceLocation = true //设置是否只定位一次,默认为false
        mLocationOption.isMockEnable = false //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.interval = 2000 //设置定位间隔,单位毫秒,默认为2000ms
        val mLocationClient = AMapLocationClient(activity) //初始化定位
        mLocationClient.setLocationOption(mLocationOption) //给定位客户端对象设置定位参数
        mLocationClient.startLocation() //启动定位
        mLocationClient.setLocationListener { aMapLocation ->
            if (aMapLocation != null && aMapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
                locationLat = aMapLocation.latitude
                locationLng = aMapLocation.longitude
            }
        }
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
        aMap!!.setOnMapClickListener(this)
        aMap!!.setOnMarkerClickListener(this)
        aMap!!.setInfoWindowAdapter(this)
        aMap!!.setOnCameraChangeListener(this)
//        aMap!!.setOnInfoWindowClickListener(this)
        aMap!!.setOnMapLoadedListener { okHttpDister() }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker != shiziMarker) {
            if (TextUtils.equals(markerTypeDisaster, marker!!.snippet)) {
                addShiziMarker(marker!!.position)
            }
            aMap!!.moveCamera(CameraUpdateFactory.newLatLng(marker!!.position))
            if (marker!!.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
        }
        return true
    }

    override fun onMapClick(p0: LatLng?) {

    }

    override fun getInfoContents(marker: Marker?): View? {
        val view = inflater!!.inflate(R.layout.marker_info, null)
        if (TextUtils.equals(markerTypeDisaster, marker!!.snippet)) {
            for (i in 0 until dataList.size) {
                val dto = dataList[i]
                if (TextUtils.equals(marker!!.title, dto.id)) {
                    tvLatlng.text = "东经："+dto.lng+"度，北纬："+dto.lat+"度发生灾情"
                    view.tvTitle.text = dto.title
                    val position = "位置：东经"+dto.lng+"度，北纬"+dto.lat+"度\n"
                    val time = "时间："+dto.time+"\n"
                    val form = "来源："+dto.form+"\n"
                    val detail = "详情："+dto.eventDescribe
                    view.tvInfo.text = position+time+form+detail
                    break
                }
            }
        } else if (TextUtils.equals(markerTypeStation, marker!!.snippet)) {
            for (i in 0 until stationList.size) {
                val dto = stationList[i]
                if (TextUtils.equals(marker!!.title, dto.id)) {
                    view.tvTitle.text = dto.stationName
                    val position = "位置：东经"+dto.lng+"度，北纬"+dto.lat+"度\n"
                    val wind = "风向风速："+dto.windSpeed+"m/s "+dto.windDir+"度\n"
                    val temp = "温度："+dto.temp+"℃\n"
                    val pressure = "气压："+dto.pressure+"hPa"
                    view.tvInfo.text = position+wind+temp+pressure
                    clickStationOrObserve(true)
                    okHttp24H("http://decision-171.tianqi.cn/weather/rgwst/OneDayStatistics?stationids=${dto.id}", true)
                    break
                }
            }
        } else if (TextUtils.equals(markerTypeObserve, marker!!.snippet)) {
            for (i in 0 until observeList.size) {
                val dto = observeList[i]
                if (TextUtils.equals(marker!!.title, dto.id)) {
                    view.tvTitle.text = dto.stationName
                    val position = "位置：东经"+dto.lng+"度，北纬"+dto.lat+"度\n"
                    val wind = "风向风速："+dto.windSpeed+"m/s "+dto.windDir+"度\n"
                    val temp = "温度："+dto.temp+"℃\n"
                    val pressure = "气压："+dto.pressure+"hPa"
                    view.tvInfo.text = position+wind+temp+pressure
                    clickStationOrObserve(true)
                    okHttp24H("http://fhjyapp.tianqi.cn/Home/other/getMiniStationMinData?sid=${dto.id}&dataType=1", false)
                    break
                }
            }
        }
        return view
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.ivWindField -> {
                isShowWind = !isShowWind
                if (isShowWind) {
                    ivWindField.setImageResource(R.drawable.icon_windfield_press)
                    if (windDataGFS == null) {
                        okHttpWind()
                    } else {
                        reloadWind()
                    }
                } else {
                    ivWindField.setImageResource(R.drawable.icon_windfield)
                    container.removeAllViews()
                    container2!!.removeAllViews()
                }
            }
            R.id.ivScope -> {
                isShowScope = !isShowScope
                if (isShowScope) {
                    ivScope.setImageResource(R.drawable.icon_scope_press)
                    drawScope()
                } else {
                    ivScope.setImageResource(R.drawable.icon_scope)
                    removeScope()
                }
            }
            R.id.ivStation -> {
                isShowStation = !isShowStation
                if (isShowStation) {
                    ivStation.setImageResource(R.drawable.icon_station_press)
                    addStationMarkers()
                } else {
                    ivStation.setImageResource(R.drawable.icon_station)
                    removeMarkers(stationMarkers)
                }
            }
            R.id.ivObserve -> {
                isShowObserve = !isShowObserve
                if (isShowObserve) {
                    ivObserve.setImageResource(R.drawable.icon_observe_press)
                    addObserveMarkers()
                } else {
                    ivObserve.setImageResource(R.drawable.icon_observe)
                    removeMarkers(observeMarkers)
                }
            }
            R.id.ivClose -> clickStationOrObserve(false)
        }
    }

    private val circle10s : ArrayList<Circle> = ArrayList()
    private val text10s : ArrayList<Text> = ArrayList()
    private val circle20s : ArrayList<Circle> = ArrayList()
    private val text20s : ArrayList<Text> = ArrayList()
    private fun removeScope() {
        for (i in 0 until circle10s.size) {
            circle10s[i].remove()
        }
        circle10s.clear()

        for (i in 0 until text10s.size) {
            text10s[i].remove()
        }
        text10s.clear()

        for (i in 0 until circle20s.size) {
            circle20s[i].remove()
        }
        circle20s.clear()

        for (i in 0 until text20s.size) {
            text20s[i].remove()
        }
        text20s.clear()
    }

    /**
     * 绘制影响范围
     */
    private fun drawScope() {
        for (i in 0 until dataList.size) {
            val dto = dataList[i]
            val circle10 = CircleOptions()
            circle10.center(LatLng(dto.lat, dto.lng))
            circle10.strokeColor(Color.RED)
            circle10.strokeWidth(5.0f)
            circle10.radius(10000.0)
            circle10s.add(aMap!!.addCircle(circle10))
            val text10 = addCircleText(LatLng(dto.lat, dto.lng), 10000, Color.RED, "10km")
            text10s.add(aMap!!.addText(text10))

            val circle20 = CircleOptions()
            circle20.center(LatLng(dto.lat, dto.lng))
            circle20.strokeColor(Color.YELLOW)
            circle20.strokeWidth(5.0f)
            circle20.radius(20000.0)
            circle20s.add(aMap!!.addCircle(circle20))
            val text20 = addCircleText(LatLng(dto.lat, dto.lng), 20000, Color.YELLOW, "20km")
            text10s.add(aMap!!.addText(text20))
        }
    }

    /**
     * 添加影响范围
     * @param center
     * @param radius
     * @param color
     * @param distance
     * @return
     */
    private fun addCircleText(center: LatLng, radius: Int, color: Int, distance: String): TextOptions? {
        val r = 6371000.79
        val numpoints = 360
        val phase = 2 * Math.PI / numpoints
        val dx = radius * Math.cos(numpoints * 3 / 4 * phase)
        val dy = radius * Math.sin(numpoints * 3 / 4 * phase) //乘以1.6 椭圆比例
        val dlng = dx / (r * Math.cos(center.latitude * Math.PI / 180) * Math.PI / 180)
        val dlat = dy / (r * Math.PI / 180)
        val textOptions = TextOptions()
        textOptions.backgroundColor(Color.TRANSPARENT)
        textOptions.fontSize(40)
        textOptions.fontColor(color)
        textOptions.text(distance)
        textOptions.position(LatLng(center.latitude + dlat, center.longitude + dlng))
        return textOptions
    }

    /**
     * 获取灾害点信息
     */
    private fun okHttpDister() {
        Thread(Runnable {
            val url = "http://fhjyapp.tianqi.cn/Home/other/xts_all_data"
            OkHttpUtil.enqueue(Request.Builder().url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        val array = JSONArray(result)
                        dataList.clear()
                        for (i in 0 until array.length()) {
                            val itemObj = array.getJSONObject(i)
                            val dto = DisasterSceneDto()
                            if (!itemObj.isNull("id")) {
                                dto.id = itemObj.getString("id")
                            }
                            if (!itemObj.isNull("title")) {
                                dto.title = itemObj.getString("title")
                            }
                            if (!itemObj.isNull("latlon")) {
                                val latlng = itemObj.getString("latlon")
                                if (!TextUtils.isEmpty(latlng) && latlng.contains(",")) {
                                    val arr = latlng.split(",")
                                    dto.lat = arr[1].toDouble()
                                    dto.lng = arr[0].toDouble()
                                }
                            }
                            if (!itemObj.isNull("time")) {
                                dto.time = itemObj.getString("time")
                            }
                            if (!itemObj.isNull("form")) {
                                dto.form = itemObj.getString("form")
                            }
                            if (!itemObj.isNull("eventDescribe")) {
                                dto.eventDescribe = itemObj.getString("eventDescribe")
                            }
                            dataList.add(dto)
                        }

                        removeMarkers(disasterMarkers)
                        addDisasterMarkers()
                    }
                }
            })
        }).start()
    }

    /**
     * 清除标记点
     */
    private fun removeMarkers(markers : ArrayList<Marker>) {
        for (i in 0 until markers.size) {
            val marker = markers[i]
            marker.remove()
        }
        markers.clear()
    }

    /**
     * 添加灾害点
     */
    private fun addDisasterMarkers() {
        var minDistance = 100000000000000.0
        for (i in 0 until dataList.size) {
            val dto = dataList[i]
            val options = MarkerOptions()
            options.title(dto.id)
            options.snippet(markerTypeDisaster)
            options.anchor(0.5f, 0.5f)
            val latLng = LatLng(dto.lat, dto.lng)
            options.position(latLng)
            val view: View = inflater!!.inflate(R.layout.marker_icon, null)
            view.ivMarker.setImageResource(R.drawable.icon_disaster_spot)
            options.icon(BitmapDescriptorFactory.fromView(view))
            val marker = aMap!!.addMarker(options)
            disasterMarkers.add(marker)

            val distance = sqrt((locationLat - dto.lat).pow(2.0) + (locationLng - dto.lng).pow(2.0))
            if (minDistance >= distance) {
                minDistance = distance
                selectedSpot = dto
                //默认
                marker.showInfoWindow()
            }
        }

        addShiziMarker(LatLng(selectedSpot!!.lat, selectedSpot!!.lng))
    }

    private fun addShiziMarker(latLng: LatLng) {
        if (shiziMarker != null) {
            shiziMarker!!.remove()
            shiziMarker = null
        }
        val options = MarkerOptions()
        options.anchor(0.5f, 0.5f)
        options.position(latLng)
        val view: View = inflater!!.inflate(R.layout.marker_icon_shizi, null)
        view.ivMarker.setImageResource(R.drawable.icon_disaster_spot_bg)
        options.icon(BitmapDescriptorFactory.fromView(view))
        options.zIndex(-1.0f)
        shiziMarker = aMap!!.addMarker(options)

        okHttpStation(latLng.latitude, latLng.longitude)
        okHttpObserve(latLng.latitude, latLng.longitude)
    }

    /**
     * 获取灾害点附近的10个站点
     */
    private val stationList : ArrayList<StationDto> = ArrayList()
    private val stationMarkers : ArrayList<Marker> = ArrayList()
    private fun okHttpStation(lat: Double, lng: Double) {
        removeMarkers(stationMarkers)
        Thread(Runnable {
            val url = "http://scapi.weather.com.cn/weather/getlonlat?lonlat=$lng,$lat&num=10&test=ncg"
            OkHttpUtil.enqueue(Request.Builder().url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        stationList.clear()
                        val array = JSONArray(result)
                        for (i in 0 until array.length()) {
                            val dto = StationDto()
                            val itemObj = array.getJSONObject(i)
                            if (!itemObj.isNull("id")) {
                                dto.id = itemObj.getString("id")
                            }
                            if (!itemObj.isNull("full_name")) {
                                dto.stationName = itemObj.getString("full_name")
                            }
                            if (!itemObj.isNull("lat")) {
                                dto.lat = itemObj.getDouble("lat")
                            }
                            if (!itemObj.isNull("lon")) {
                                dto.lng = itemObj.getDouble("lon")
                            }
                            stationList.add(dto)
                        }
                        addStationMarkers()
                    }
                }
            })
        }).start()
    }

    /**
     * 添加气象站
     */
    private fun addStationMarkers() {
        if (!isShowStation) {
            return
        }
        for (i in 0 until stationList.size) {
            val dto = stationList[i]
            val options = MarkerOptions()
            options.title(dto.id)
            options.snippet(markerTypeStation)
            options.anchor(0.5f, 0.5f)
            val latLng = LatLng(dto.lat, dto.lng)
            options.position(latLng)
            val view: View = inflater!!.inflate(R.layout.marker_icon, null)
            view.ivMarker.setImageResource(R.drawable.icon_marker_station)
            options.icon(BitmapDescriptorFactory.fromView(view))
            val marker = aMap!!.addMarker(options)
            stationMarkers.add(marker)

            okHttpStationInfo(dto)
        }
    }

    /**
     * 获取站点信息
     * @param stationIds
     */
    private fun okHttpStationInfo(dto: StationDto) {
        val formBodyBuilder = FormBody.Builder()
        formBodyBuilder.add("ids", dto.id!!)
        val requestBody: RequestBody = formBodyBuilder.build()
        Thread(Runnable {
            OkHttpUtil.enqueue(Request.Builder().url(SecretUrlUtil.stationsInfo(dto.id)).post(requestBody).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        if (!TextUtils.isEmpty(result)) {
                            val array = JSONArray(result)
                            if (array.length() > 0) {
                                val obj = array.getJSONObject(0)
                                if (!obj.isNull("windspeed")) {
                                    dto.windSpeed = obj.getString("windspeed")+"m/s"
                                } else {
                                    dto.windSpeed = CONST.noValue
                                }
                                if (!obj.isNull("winddir")) {
                                    dto.windDir = obj.getString("winddir")+"度"
                                } else {
                                    dto.windDir = CONST.noValue
                                }
                                if (!obj.isNull("balltemp")) {
                                    dto.temp = obj.getString("balltemp")+"℃"
                                } else {
                                    dto.temp = CONST.noValue
                                }
                                if (!obj.isNull("airpressure")) {
                                    dto.pressure = obj.getString("airpressure")+"hPa"
                                } else {
                                    dto.pressure = CONST.noValue
                                }
                                if (!obj.isNull("humidity")) {
                                    dto.humidity = obj.getString("humidity")+"%"
                                } else {
                                    dto.humidity = CONST.noValue
                                }
                            }
                        }
                    }
                }
            })
        }).start()
    }

    /**
     * 获取灾害点附近的手持观测点
     */
    private val observeList : ArrayList<StationDto> = ArrayList()
    private val observeMarkers : ArrayList<Marker> = ArrayList()
    private fun okHttpObserve(lat: Double, lng: Double) {
        removeMarkers(observeMarkers)
        Thread(Runnable {
            val url = "http://fhjyapp.tianqi.cn/Home/other/getMiniStationData?lat=$lat&lon=$lng"
            OkHttpUtil.enqueue(Request.Builder().url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        observeList.clear()
                        val array = JSONArray(result)
                        for (i in 0 until array.length()) {
                            val dto = StationDto()
                            val itemObj = array.getJSONObject(i)
                            if (!itemObj.isNull("sid")) {
                                dto.id = itemObj.getString("sid")
                            }
                            if (!itemObj.isNull("name")) {
                                dto.stationName = itemObj.getString("name")
                            }
                            if (!itemObj.isNull("lat")) {
                                dto.lat = itemObj.getDouble("lat")
                            }
                            if (!itemObj.isNull("lon")) {
                                dto.lng = itemObj.getDouble("lon")
                            }
                            if (!itemObj.isNull("d_winddir")) {
                                dto.windDir = itemObj.getString("d_winddir")+"度"
                            } else {
                                dto.windDir = CONST.noValue
                            }
                            if (!itemObj.isNull("d_windspeed")) {
                                dto.windSpeed = itemObj.getString("d_windspeed")+"m/s"
                            } else {
                                dto.windSpeed = CONST.noValue
                            }
                            if (!itemObj.isNull("d_temp")) {
                                dto.temp = itemObj.getString("d_temp")+"℃"
                            } else {
                                dto.temp = CONST.noValue
                            }
                            if (!itemObj.isNull("d_airpressure")) {
                                dto.pressure = itemObj.getString("d_airpressure")+"hPa"
                            } else {
                                dto.pressure = CONST.noValue
                            }
                            if (!itemObj.isNull("d_humidity")) {
                                dto.humidity = itemObj.getString("d_humidity")+"%"
                            } else {
                                dto.humidity = CONST.noValue
                            }
                            observeList.add(dto)
                        }
                        addObserveMarkers()
                    }
                }
            })
        }).start()
    }

    /**
     * 添加手持观测点
     */
    private fun addObserveMarkers() {
        if (!isShowObserve) {
            return
        }
        for (i in 0 until observeList.size) {
            val dto = observeList[i]
            val options = MarkerOptions()
            options.title(dto.id)
            options.snippet(markerTypeObserve)
            options.anchor(0.5f, 0.5f)
            val latLng = LatLng(dto.lat, dto.lng)
            options.position(latLng)
            val view: View = inflater!!.inflate(R.layout.marker_icon, null)
            view.ivMarker.setImageResource(R.drawable.icon_marker_observe)
            options.icon(BitmapDescriptorFactory.fromView(view))
            val marker = aMap!!.addMarker(options)
            observeMarkers.add(marker)
        }
    }

    /**
     * 获取手持观测24小时数据
     */
    private fun okHttp24H(url: String?, isStation: Boolean) {
        Thread(Runnable {
            OkHttpUtil.enqueue(Request.Builder().url(url!!).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        if (!TextUtils.isEmpty(result)) {
                            val array = JSONArray(result)
                            if (array.length() > 0) {
                                val obj = array.getJSONObject(0)
                                if (!obj.isNull("24H")) {
                                    val list : ArrayList<StationDto> = ArrayList()
                                    val itemArray = obj.getJSONArray("24H")
                                    for (i in 0 until itemArray.length()) {
                                        val itemObj = itemArray.getJSONObject(i)
                                        val dto = StationDto()
                                        if (!itemObj.isNull("balltemp")) {
                                            dto.temp = itemObj.getString("balltemp")
                                        }
                                        if (!itemObj.isNull("windspeed")) {
                                            dto.windSpeed = itemObj.getString("windspeed")
                                        }
                                        if (!itemObj.isNull("humidity")) {
                                            dto.humidity = itemObj.getString("humidity")
                                        }
                                        if (!itemObj.isNull("airpressure")) {
                                            dto.pressure = itemObj.getString("airpressure")
                                        }
                                        if (!itemObj.isNull("precipitation1h")) {
                                            dto.precipitation1h = itemObj.getString("precipitation1h")
                                        }
                                        if (!itemObj.isNull("datatime")) {
                                            dto.datatime = itemObj.getString("datatime")
                                        }
                                        list.add(dto)
                                    }

                                    if (isStation) {
                                        tvChartRain.text = "降水"
                                    } else {
                                        tvChartRain.text = "湿度"
                                    }

                                    //默认绘制温度
                                    tvChartTemp.setBackgroundResource(R.drawable.bg_corner_left_selected)
                                    tvChartRain.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                    tvChartWind.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                    tvChartPressure.setBackgroundResource(R.drawable.bg_corner_right_unselected)
                                    tvChartTemp.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                                    tvChartRain.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                    tvChartWind.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                    tvChartPressure.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                    val tempView = ChartTempView(activity)
                                    tempView.setData(list)
                                    llContainer2.removeAllViews()
                                    llContainer2.addView(tempView, CommonUtil.widthPixels(activity)-CommonUtil.dip2px(activity, 20f).toInt(), CommonUtil.dip2px(activity, 200f).toInt())

                                    tvChartTemp.setOnClickListener {
                                        tvChartTemp.setBackgroundResource(R.drawable.bg_corner_left_selected)
                                        tvChartRain.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                        tvChartWind.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                        tvChartPressure.setBackgroundResource(R.drawable.bg_corner_right_unselected)
                                        tvChartTemp.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                                        tvChartRain.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartWind.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartPressure.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        val tempView = ChartTempView(activity)
                                        tempView.setData(list)
                                        llContainer2.removeAllViews()
                                        llContainer2.addView(tempView, CommonUtil.widthPixels(activity)-CommonUtil.dip2px(activity, 20f).toInt(), CommonUtil.dip2px(activity, 200f).toInt())
                                    }
                                    tvChartRain.setOnClickListener {
                                        tvChartTemp.setBackgroundResource(R.drawable.bg_corner_left_unselected)
                                        tvChartRain.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
                                        tvChartWind.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                        tvChartPressure.setBackgroundResource(R.drawable.bg_corner_right_unselected)
                                        tvChartTemp.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartRain.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                                        tvChartWind.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartPressure.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        if (isStation) {
                                            val rainView = ChartRainView(activity)
                                            rainView.setData(list)
                                            llContainer2.removeAllViews()
                                            llContainer2.addView(rainView, CommonUtil.widthPixels(activity)-CommonUtil.dip2px(activity, 20f).toInt(), CommonUtil.dip2px(activity, 200f).toInt())
                                        } else {
                                            val humidityView = ChartHumidityView(activity)
                                            humidityView.setData(list)
                                            llContainer2.removeAllViews()
                                            llContainer2.addView(humidityView, CommonUtil.widthPixels(activity)-CommonUtil.dip2px(activity, 20f).toInt(), CommonUtil.dip2px(activity, 200f).toInt())
                                        }
                                    }
                                    tvChartWind.setOnClickListener {
                                        tvChartTemp.setBackgroundResource(R.drawable.bg_corner_left_unselected)
                                        tvChartRain.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                        tvChartWind.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
                                        tvChartPressure.setBackgroundResource(R.drawable.bg_corner_right_unselected)
                                        tvChartTemp.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartRain.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartWind.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                                        tvChartPressure.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        val windView = ChartWindView(activity)
                                        windView.setData(list)
                                        llContainer2.removeAllViews()
                                        llContainer2.addView(windView, CommonUtil.widthPixels(activity)-CommonUtil.dip2px(activity, 20f).toInt(), CommonUtil.dip2px(activity, 200f).toInt())
                                    }
                                    tvChartPressure.setOnClickListener {
                                        tvChartTemp.setBackgroundResource(R.drawable.bg_corner_left_unselected)
                                        tvChartRain.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                        tvChartWind.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                                        tvChartPressure.setBackgroundResource(R.drawable.bg_corner_right_selected)
                                        tvChartTemp.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartRain.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartWind.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                                        tvChartPressure.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                                        val pressureView = ChartPressureView(activity)
                                        pressureView.setData(list)
                                        llContainer2.removeAllViews()
                                        llContainer2.addView(pressureView, CommonUtil.widthPixels(activity)-CommonUtil.dip2px(activity, 20f).toInt(), CommonUtil.dip2px(activity, 200f).toInt())
                                    }

                                }
                            }
                        }
                    }
                }
            })
        }).start()
    }

    /**
     * 获取风场数据
     */
    private fun okHttpWind() {
        if (windDataGFS != null) {
            return
        }
        Thread(Runnable {
            OkHttpUtil.enqueue(Request.Builder().url(SecretUrlUtil.windGFS("1000")).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            val obj = JSONObject(result)
                            if (windDataGFS == null) {
                                windDataGFS = WindData()
                            }
                            if (!obj.isNull("gridHeight")) {
                                windDataGFS!!.height = obj.getInt("gridHeight")
                            }
                            if (!obj.isNull("gridWidth")) {
                                windDataGFS!!.width = obj.getInt("gridWidth")
                            }
                            if (!obj.isNull("x0")) {
                                windDataGFS!!.x0 = obj.getDouble("x0")
                            }
                            if (!obj.isNull("y0")) {
                                windDataGFS!!.y0 = obj.getDouble("y0")
                            }
                            if (!obj.isNull("x1")) {
                                windDataGFS!!.x1 = obj.getDouble("x1")
                            }
                            if (!obj.isNull("y1")) {
                                windDataGFS!!.y1 = obj.getDouble("y1")
                            }
                            if (!obj.isNull("filetime")) {
                                windDataGFS!!.filetime = obj.getString("filetime")
                            }
                            if (!obj.isNull("field")) {
                                windDataGFS!!.dataList.clear()
                                val array = JSONArray(obj.getString("field"))
                                var i = 0
                                while (i < array.length()) {
                                    val dto2 = WindDto()
                                    dto2.initX = array.optDouble(i).toFloat()
                                    dto2.initY = array.optDouble(i + 1).toFloat()
                                    windDataGFS!!.dataList.add(dto2)
                                    i += 2
                                }
                            }
                            activity!!.runOnUiThread { reloadWind() }
                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }
                    }
                }
            })
        }).start()
    }

    override fun onCameraChange(arg0: CameraPosition?) {
        container.removeAllViews()
        container2.removeAllViews()
    }

    override fun onCameraChangeFinish(arg0: CameraPosition?) {
        reloadWind()
    }

    var t = Date().time

    /**
     * 重新加载风场
     */
    private fun reloadWind() {
        t = Date().time - t
        if (t < 1000) {
            return
        }
        val latLngStart = aMap!!.projection.fromScreenLocation(Point(0, 0))
        val latLngEnd = aMap!!.projection.fromScreenLocation(Point(CommonUtil.widthPixels(activity), CommonUtil.heightPixels(activity)))
        windDataGFS!!.latLngStart = latLngStart
        windDataGFS!!.latLngEnd = latLngEnd
        if (waitWindView == null) {
            waitWindView = WaitWindView(activity)
            waitWindView!!.init(activity, container2)
            waitWindView!!.setData(windDataGFS)
            waitWindView!!.start()
            waitWindView!!.invalidate()
        } else {
            waitWindView!!.setData(windDataGFS)
        }
        container2.removeAllViews()
        container.removeAllViews()
        container.addView(waitWindView)
    }

    /**
     * 点击气象站、手持观测点
     */
    private fun clickStationOrObserve(isShow : Boolean) {
        if (isShow) {
            AnimationUtil.toTopAnimation(llChart)
        } else {
            AnimationUtil.toBottomAnimation(llChart)
        }
    }

    /**
     * 初始化列表
     */
    private fun initList() {
        titleBar.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.light_gray))
        val dataList: ArrayList<String> = ArrayList()
        dataList.add("气象站检测")
        dataList.add("手持观测站")
        llContainer.removeAllViews()
        llContainer1.removeAllViews()
        for (i in dataList.indices) {
            val name = dataList[i]
            val tvName = TextView(activity)
            tvName.gravity = Gravity.CENTER
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            tvName.setPadding(0, CommonUtil.dip2px(activity, 8.0f).toInt(), 0, CommonUtil.dip2px(activity, 8.0f).toInt())
            if (i == 0) {
                tvName.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                initListview(stationList)
            } else {
                tvName.setTextColor(ContextCompat.getColor(activity!!, R.color.text_color3))
            }
            tvName.text = name
            tvName.tag = i
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.weight = 1.0f
            params.width = CommonUtil.widthPixels(activity) / 2
            tvName.layoutParams = params
            llContainer.addView(tvName, i)
            val tvBar = TextView(activity)
            tvBar.gravity = Gravity.CENTER
            tvBar.tag = i
            if (i == 0) {
                tvBar.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.blue))
            } else {
                tvBar.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
            }
            val params1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params1.weight = 1.0f
            params1.width = CommonUtil.widthPixels(activity) / 2 - CommonUtil.dip2px(activity, 80f).toInt()
            params1.height = CommonUtil.dip2px(activity, 2f).toInt()
            params1.setMargins(CommonUtil.dip2px(activity, 40f).toInt(), 0, CommonUtil.dip2px(activity, 40f).toInt(), 0)
            tvBar.layoutParams = params1
            llContainer1.addView(tvBar, i)

            tvName.setOnClickListener {
                if (llContainer != null) {
                    for (j in 0 until llContainer.childCount) {
                        val tvItemName = llContainer.getChildAt(j) as TextView
                        if (j == tvName.tag) {
                            tvItemName.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                            if (j == 0) {
                                initListview(stationList)
                            } else {
                                initListview(observeList)
                            }
                        } else {
                            tvItemName.setTextColor(ContextCompat.getColor(activity!!, R.color.text_color3))
                        }
                    }
                }
                if (llContainer1 != null) {
                    for (j in 0 until llContainer1.childCount) {
                        val tvItemBar = llContainer1.getChildAt(j) as TextView
                        if (j == tvBar.tag) {
                            tvItemBar.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.blue))
                        } else {
                            tvItemBar.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
                        }
                    }
                }
            }
        }
    }

    private fun initListview(list: ArrayList<StationDto>) {
        val mAdapter = TitleListviewAdapter(activity!!, list)
        listView.adapter = mAdapter
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
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    )

    //拒绝的权限集合
    var deniedList: ArrayList<String> = ArrayList()

    /**
     * 申请多个权限
     */
    private fun checkMultiAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            startLocation()
        } else {
            deniedList.clear()
            for (i in allPermissions.indices) {
                if (ContextCompat.checkSelfPermission(activity!!, allPermissions[i]) !== PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(AuthorityUtil.allPermissions[i])
                }
            }
            if (deniedList.isEmpty()) { //所有权限都授予
                startLocation()
            } else {
                val permissions = deniedList.toTypedArray() //将list转成数组
                ActivityCompat.requestPermissions(activity!!, permissions, AuthorityUtil.AUTHOR_MULTI)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AuthorityUtil.AUTHOR_MULTI -> startLocation()
        }
    }

}
