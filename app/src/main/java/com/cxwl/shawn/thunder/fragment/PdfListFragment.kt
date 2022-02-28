package com.cxwl.shawn.thunder.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import com.cxwl.shawn.thunder.PDFActivity
import com.cxwl.shawn.thunder.R
import com.cxwl.shawn.thunder.adapter.PDFListAdapter
import com.cxwl.shawn.thunder.common.CONST
import com.cxwl.shawn.thunder.dto.StrongStreamDto
import com.cxwl.shawn.thunder.util.OkHttpUtil
import kotlinx.android.synthetic.main.fragment_pdf_list.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*

/**
 * pdf文档列表
 */
class PdfListFragment : Fragment() {

	private var mAdapter: PDFListAdapter? = null
	private val dataList = ArrayList<StrongStreamDto>()
	private var mReceiver: MyBroadCastReceiver? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_pdf_list, null)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initBroadCast()
	}

	private fun initBroadCast() {
		mReceiver = MyBroadCastReceiver()
		val intentFilter = IntentFilter()
		intentFilter.addAction(PdfListFragment::class.java.name)
		activity!!.registerReceiver(mReceiver, intentFilter)
	}

	private inner class MyBroadCastReceiver : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			if (TextUtils.equals(intent.action, PdfListFragment::class.java.name)) {
				initRefreshLayout()
				initWidget()
				initListView()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		if (mReceiver != null) {
			activity!!.unregisterReceiver(mReceiver)
		}
	}

	/**
	 * 初始化下拉刷新布局
	 */
	private fun initRefreshLayout() {
		refreshLayout!!.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4)
		refreshLayout!!.setProgressViewEndTarget(true, 400)
		refreshLayout!!.post { refreshLayout!!.isRefreshing = true }
		refreshLayout!!.setOnRefreshListener { refresh() }
	}

	private fun initWidget() {
		refresh()
	}

	private fun refresh() {
		dataList.clear()
		okHttpList()
	}

	private fun initListView() {
		mAdapter = PDFListAdapter(activity!!, dataList)
		listView.adapter = mAdapter
		listView.onItemClickListener = OnItemClickListener { arg0, arg1, arg2, arg3 ->
			val dto = dataList[arg2]
			val intent = Intent(activity, PDFActivity::class.java)
			intent.putExtra(CONST.ACTIVITY_NAME, dto.content)
			intent.putExtra(CONST.WEB_URL, dto.link)
			startActivity(intent)
		}
	}

	private fun okHttpList() {
		val url = "https://decision-admin.tianqi.cn/Home/work2019/fhjy_getJccl"
		Thread(Runnable {
			OkHttpUtil.enqueue(Request.Builder().url(url).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {}
				@Throws(IOException::class)
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val result = response.body!!.string()
					activity!!.runOnUiThread {
						refreshLayout!!.isRefreshing = false
						if (!TextUtils.isEmpty(result)) {
							try {
								val array = JSONArray(result)
								for (i in 0 until array.length()) {
									val itemObj = array.getJSONObject(i)
									val dto = StrongStreamDto()
									if (!itemObj.isNull("url")) {
										dto.link = itemObj.getString("url")
									}
									if (!itemObj.isNull("title")) {
										dto.content = itemObj.getString("title")
									}
									if (!itemObj.isNull("time")) {
										dto.time = itemObj.getString("time")
									}
									if (!itemObj.isNull("img")) {
										dto.imgUrl = itemObj.getString("img")
									}
									dataList.add(dto)
								}
								if (mAdapter != null) {
									mAdapter!!.notifyDataSetChanged()
								}
								if (dataList.size == 0) {
									tvPrompt!!.visibility = View.VISIBLE
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
	
}
