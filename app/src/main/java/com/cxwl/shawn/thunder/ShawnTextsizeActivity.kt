package com.cxwl.shawn.thunder

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.SeekBar
import com.cxwl.shawn.thunder.util.CommonUtil
import kotlinx.android.synthetic.main.shawn_activity_textsize.*

class ShawnTextsizeActivity : ShawnBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shawn_activity_textsize)
        initSeekbar()
    }

    private fun initSeekbar() {
        val textSize = CommonUtil.getTextSize(this)
        tvFact.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        tvThunder.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        val progress = CommonUtil.getProgress(this)
        seekBar.progress = progress.toInt()
        seekBar.min = 10
        seekBar.max = 20
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tvFact.textSize = p1.toFloat()
                tvThunder.textSize = p1.toFloat()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        CommonUtil.saveTextSize(this@ShawnTextsizeActivity, tvFact.textSize, seekBar.progress)
        val intent = Intent()
        intent.action = "broadcast_textsize"
        sendBroadcast(intent)
    }

}