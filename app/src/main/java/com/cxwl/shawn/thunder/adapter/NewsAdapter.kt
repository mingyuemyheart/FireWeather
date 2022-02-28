package com.cxwl.shawn.thunder.adapter;

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cxwl.shawn.thunder.R
import com.cxwl.shawn.thunder.dto.StrongStreamDto
import com.squareup.picasso.Picasso

/**
 * 消息
 */
class NewsAdapter(context: Context?, dataList: ArrayList<StrongStreamDto>) : BaseAdapter() {

	private var mInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
	private var mArrayList: ArrayList<StrongStreamDto>? = dataList

	private class ViewHolder {
		var imageView: ImageView? = null
		var tvType: TextView? = null
		var tvTime: TextView? = null
		var tvContent: TextView? = null
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

	override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
		var convertView = view
		val mHolder: ViewHolder
		if (convertView == null) {
			convertView = mInflater!!.inflate(R.layout.adapter_news, null)
			mHolder = ViewHolder()
			mHolder.imageView = convertView.findViewById(R.id.imageView)
			mHolder.tvType = convertView.findViewById(R.id.tvType)
			mHolder.tvTime = convertView.findViewById(R.id.tvTime)
			mHolder.tvContent = convertView.findViewById(R.id.tvContent)
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}
		val dto = mArrayList!![position]
		if (!TextUtils.isEmpty(dto.msgType)) {
			mHolder.tvType!!.text = dto.msgType
		}
		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime!!.text = dto.time
		}
		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent!!.text = dto.content
		}
		if (!TextUtils.isEmpty(dto.imgUrl)) {
			Picasso.get().load(dto.imgUrl).error(R.drawable.shawn_icon_news).into(mHolder.imageView)
		} else {
			mHolder.imageView!!.setImageResource(R.drawable.shawn_icon_news)
		}
		return convertView
	}
	
}
