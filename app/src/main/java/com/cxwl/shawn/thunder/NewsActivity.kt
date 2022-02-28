package com.cxwl.shawn.thunder;

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import com.cxwl.shawn.thunder.adapter.NewsAdapter
import com.cxwl.shawn.thunder.common.CONST
import com.cxwl.shawn.thunder.dto.StrongStreamDto
import com.cxwl.shawn.thunder.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

/**
 * 消息
 */
class NewsActivity : ShawnBaseActivity(), View.OnClickListener {

    private var adapter: NewsAdapter? = null
    private val dataList: ArrayList<StrongStreamDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        initRefreshLayout()
        initWidget()
        initGridView()
    }

    private fun initRefreshLayout() {
        refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4)
        refreshLayout.setProgressViewEndTarget(true, 300)
        refreshLayout.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        dataList.clear()
        OkHttpList()
    }

    private fun initWidget() {
        llBack.setOnClickListener(this)
        tvTitle.text = "系统消息"
        refresh()
    }

    private fun initGridView() {
        adapter = NewsAdapter(this, dataList)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //                StrongStreamDto dto = dataList.get(position);
//                if (!TextUtils.isEmpty(dto.link)) {
//                    Intent intent = new Intent(mContext, WebviewActivity.class);
//                    intent.putExtra("url", dto.link);
//                    startActivity(intent);
//                }
        }
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
    }

    private fun OkHttpList() {
        showDialog()
        val url = "http://decision-admin.tianqi.cn/Home/work2019/fhjy_getAppMessages"
        Thread(Runnable {
            OkHttpUtil.enqueue(Request.Builder().url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        cancelDialog()
                        refreshLayout!!.isRefreshing = false
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val array = JSONArray(result)
                                for (i in 0 until array.length()) {
                                    val dto = StrongStreamDto()
                                    val itemObj = array.getJSONObject(i)
                                    if (!itemObj.isNull("icon")) {
                                        dto.imgUrl = itemObj.getString("icon")
                                    }
                                    if (!itemObj.isNull("type")) {
                                        dto.msgType = itemObj.getString("type")
                                    }
                                    if (!itemObj.isNull("time")) {
                                        dto.time = itemObj.getString("time")
                                    }
                                    if (!itemObj.isNull("content")) {
                                        dto.content = itemObj.getString("content")
                                    }
                                    if (!itemObj.isNull("link")) {
                                        dto.link = itemObj.getString("link")
                                    }
                                    dataList.add(dto)
                                }
                                if (adapter != null) {
                                    adapter!!.notifyDataSetChanged()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llBack -> finish()
        }
    }

}
