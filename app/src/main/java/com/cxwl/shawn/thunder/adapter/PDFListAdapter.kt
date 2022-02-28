package com.cxwl.shawn.thunder.adapter

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
import java.util.*
import kotlin.collections.ArrayList

/**
 * pdf文档列表
 */
class PDFListAdapter(context: Context, dataList: ArrayList<StrongStreamDto>) : BaseAdapter() {

	private var mContext: Context? = context
	private var mInflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
	private var mArrayList: ArrayList<StrongStreamDto>? = dataList

	private class ViewHolder {
		var tvTitle: TextView? = null
		var tvTime: TextView? = null
		var imageView: ImageView? = null
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

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
		var convertView = convertView
		val mHolder: ViewHolder
		if (convertView == null) {
			convertView = mInflater!!.inflate(R.layout.adapter_pdf_list, null)
			mHolder = ViewHolder()
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle)
			mHolder.tvTime = convertView.findViewById(R.id.tvTime)
			mHolder.imageView = convertView.findViewById(R.id.imageView)
			convertView.tag = mHolder
		} else {
			mHolder = convertView.tag as ViewHolder
		}
		val dto = mArrayList!![position]
		if (!TextUtils.isEmpty(dto.imgUrl)) {
			Picasso.get().load(dto.imgUrl).into(mHolder.imageView)
		}
		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvTitle!!.text = dto.content
		}
		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime!!.text = dto.time
		}
		return convertView
	}

}
