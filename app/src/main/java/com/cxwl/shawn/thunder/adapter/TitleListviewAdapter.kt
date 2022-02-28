package com.cxwl.shawn.thunder.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cxwl.shawn.thunder.R
import com.cxwl.shawn.thunder.dto.StationDto

/**
 * 气象站监测、手持观测站等列表
 */
class TitleListviewAdapter(context: Context, dataList: ArrayList<StationDto>) : BaseAdapter() {

	private var mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
	private var mArrayList: ArrayList<StationDto>? = dataList

	private class ViewHolder {
		var tvName: TextView? = null
		var tvTemp: TextView? = null
		var tvHumidity: TextView? = null
		var tvWind: TextView? = null
		var tvPressure: TextView? = null
	}

	override fun getCount(): Int {
		return mArrayList!!.size
	}

	override fun getItem(position: Int): Any? {
		return position
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getView(position: Int, convert: View?, parent: ViewGroup?): View? {
		var convertView = convert
		val mHolder: ViewHolder
		if (convertView == null) {
			convertView = mInflater!!.inflate(R.layout.adapter_title_listview, null)
			mHolder = ViewHolder()
			mHolder.tvName = convertView.findViewById(R.id.tvName)
			mHolder.tvTemp = convertView.findViewById(R.id.tvTemp)
			mHolder.tvHumidity = convertView.findViewById(R.id.tvHumidity)
			mHolder.tvWind = convertView.findViewById(R.id.tvWind)
			mHolder.tvPressure = convertView.findViewById(R.id.tvPressure)
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}
		val dto = mArrayList!![position]
		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvName!!.text = dto.stationName
		}
		if (!TextUtils.isEmpty(dto.temp)) {
			mHolder.tvTemp!!.text = dto.temp
		}
		if (!TextUtils.isEmpty(dto.humidity)) {
			mHolder.tvHumidity!!.text = dto.humidity
		}
		mHolder.tvWind!!.text = dto.windSpeed + " " + dto.windDir
		if (!TextUtils.isEmpty(dto.pressure)) {
			mHolder.tvPressure!!.text = dto.pressure
		}
		return convertView
	}

}
