package com.cxwl.shawn.thunder

import android.os.Bundle
import com.cxwl.shawn.thunder.adapter.OnlinePictureAdapter
import com.cxwl.shawn.thunder.dto.PhotoDto
import kotlinx.android.synthetic.main.fragment_disater_report.*

class OnlinePictureActivity : ShawnBaseActivity() {

    private val dataList: ArrayList<String>? = ArrayList() //存放图片的list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_picture)
        initGridview()
    }

    private fun initGridview() {
        if (intent.hasExtra("data")) {
            val data: PhotoDto = intent.extras.getParcelable("data")
            dataList!!.clear()
            dataList.addAll(data.getUrlList())
        }
        val mAdapter = OnlinePictureAdapter(this, dataList)
        gridView.adapter = mAdapter
    }
    
}