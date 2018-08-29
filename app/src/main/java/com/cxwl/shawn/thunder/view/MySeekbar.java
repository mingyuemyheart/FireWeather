package com.cxwl.shawn.thunder.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;
import com.cxwl.shawn.thunder.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 雷电进度条
 */
public class MySeekbar extends View {

    private Context mContext;
    private Paint lineP,textP;//画线画笔
    private Bitmap playBit,pauseBit;//播放按钮
    private List<StrongStreamDto> dataList = new ArrayList<>();//雷达图层数据
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private boolean isDraggingThumb = false;//是否拖拽滑块
    private int currentIndex = 0;
    private String currentTime;
    public int playingIndex = 0;//线程播放时索引
    public String playingTime;
    private String BROAD_CLICKMENU = "broad_clickMenu";//点击播放或暂停
    private final int STATE_NONE = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSE = 2;
    private int STATE = STATE_NONE;

    public MySeekbar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MySeekbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MySeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        lineP = new Paint();
        lineP.setStyle(Paint.Style.STROKE);
        lineP.setAntiAlias(true);

        textP = new Paint();
        textP.setAntiAlias(true);

        playBit = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.shawn_icon_play),
                (int)(CommonUtil.dip2px(mContext, 15)), (int)(CommonUtil.dip2px(mContext, 20)));

        pauseBit = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.shawn_icon_pause),
                (int)(CommonUtil.dip2px(mContext, 15)), (int)(CommonUtil.dip2px(mContext, 20)));
    }

    /**
     * 设置数据
     * @param radarMap
     */
    public void setData(Map<String, StrongStreamDto> radarMap) {
        if (radarMap.isEmpty()) {
            return;
        }

        dataList.clear();
        for (String startTime : radarMap.keySet()) {
            if (radarMap.containsKey(startTime)) {
                StrongStreamDto dto = radarMap.get(startTime);
                dataList.add(dto);
            }
        }

        for (int i = 0; i < dataList.size(); i++) {
            StrongStreamDto dto = dataList.get(i);
            if (TextUtils.equals(dto.tag, dto.startTime)) {
                currentTime = dto.startTime;
                currentIndex = i;
                playingIndex = i;
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = canvas.getWidth();
        float h = canvas.getHeight();
        float leftMargin = CommonUtil.dip2px(mContext, 10);//左边距
        float rightMargin = CommonUtil.dip2px(mContext, 20);//右边距
        float margin1 = CommonUtil.dip2px(mContext, 10);//滑块距离播放按钮距离
        float margin2 = leftMargin+playBit.getWidth()+margin1;//seekbar距离左边长度
        float seekBarWidth = w-margin2-rightMargin;//seekbar宽度
        float itemWidth = seekBarWidth/(dataList.size()-1);//每个刻度对应的宽度

        canvas.drawColor(Color.TRANSPARENT);

        //绘制seekbar实况
        lineP.setColor(0xffC9CEF1);
        lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 15));
        canvas.drawLine(margin2, h/2, margin2+itemWidth*currentIndex, h/2, lineP);

        //绘制seekbar预报
        lineP.setColor(0xffE5E5E5);
        lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 15));
        canvas.drawLine(margin2+itemWidth*currentIndex, h/2, w-rightMargin, h/2, lineP);


        //绘制时间
        textP.setColor(getResources().getColor(R.color.text_color2));
        textP.setTextSize(CommonUtil.dip2px(mContext, 10));
        for (int i = 0; i < dataList.size(); i+=6) {
            StrongStreamDto dto = dataList.get(i);
            try {
//                if (!TextUtils.isEmpty(dto.startTime)) {
//                    if (TextUtils.equals(dto.tag, dto.startTime)) {
//                        float startTimeWidth = textP.measureText("现在");
//                        canvas.drawText("现在", margin2+itemWidth*i-startTimeWidth/2, h/2+CommonUtil.dip2px(mContext, 23), textP);
//                    }else {
//                        if (i == 0 || i == dataList.size()-1) {
//                            String startTime = sdf2.format(sdf1.parse(dto.startTime));
//                            float startTimeWidth = textP.measureText(startTime);
//                            canvas.drawText(startTime, margin2+itemWidth*i-startTimeWidth/2, h/2+CommonUtil.dip2px(mContext, 23), textP);
//                        }
//                    }
//                }
                String startTime = sdf2.format(sdf1.parse(dto.startTime));
                float startTimeWidth = textP.measureText(startTime);
                canvas.drawText(startTime, margin2+itemWidth*i-startTimeWidth/2, h/2+CommonUtil.dip2px(mContext, 23), textP);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //绘制播放按钮
        if (STATE == STATE_NONE) {
            canvas.drawBitmap(playBit, leftMargin, h/2-playBit.getHeight()/2, lineP);

            if (!isDraggingThumb) {
                //线程暂停时，绘制当前时间对应滑块
                lineP.setColor(getResources().getColor(R.color.text_color3));
                lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
                canvas.drawLine(margin2+itemWidth*playingIndex, h/2-(int)CommonUtil.dip2px(mContext, 7.5f),
                        margin2+itemWidth*playingIndex, h/2+(int)CommonUtil.dip2px(mContext, 7.5f), lineP);

                //绘制滑块滑动对应的时间
                if (!TextUtils.isEmpty(currentTime)) {
                    try {
                        textP.setColor(getResources().getColor(R.color.text_color2));
                        textP.setTextSize(CommonUtil.dip2px(mContext, 10));
                        float timeWidth = textP.measureText(sdf2.format(sdf1.parse(currentTime)));
                        canvas.drawText(sdf2.format(sdf1.parse(currentTime)), margin2+itemWidth*playingIndex-timeWidth/2, h/2-CommonUtil.dip2px(mContext, 15), textP);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (STATE == STATE_PLAYING){
            canvas.drawBitmap(pauseBit, leftMargin, h/2-playBit.getHeight()/2, lineP);

            if (!isDraggingThumb) {
                //线程播放时，绘制变动时间对应滑块
                lineP.setColor(Color.BLACK);
                lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
                canvas.drawLine(margin2+itemWidth*playingIndex, h/2-(int)CommonUtil.dip2px(mContext, 8f),
                        margin2+itemWidth*playingIndex, h/2+(int)CommonUtil.dip2px(mContext, 8f), lineP);

                //绘制滑块滑动对应的时间
                if (!TextUtils.isEmpty(playingTime)) {
                    try {
                        textP.setColor(getResources().getColor(R.color.text_color2));
                        textP.setTextSize(CommonUtil.dip2px(mContext, 10));
                        float timeWidth = textP.measureText(sdf2.format(sdf1.parse(playingTime)));
                        canvas.drawText(sdf2.format(sdf1.parse(playingTime)), margin2+itemWidth*playingIndex-timeWidth/2, h/2-CommonUtil.dip2px(mContext, 15), textP);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (STATE == STATE_PAUSE){
            canvas.drawBitmap(playBit, leftMargin, h/2-playBit.getHeight()/2, lineP);

            if (!isDraggingThumb) {
                //线程播放时，绘制变动时间对应滑块
                lineP.setColor(Color.BLACK);
                lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
                canvas.drawLine(margin2+itemWidth*playingIndex, h/2-(int)CommonUtil.dip2px(mContext, 8f),
                        margin2+itemWidth*playingIndex, h/2+(int)CommonUtil.dip2px(mContext, 8f), lineP);

                //绘制滑块滑动对应的时间
                if (!TextUtils.isEmpty(playingTime)) {
                    try {
                        textP.setColor(getResources().getColor(R.color.text_color2));
                        textP.setTextSize(CommonUtil.dip2px(mContext, 10));
                        float timeWidth = textP.measureText(sdf2.format(sdf1.parse(playingTime)));
                        canvas.drawText(sdf2.format(sdf1.parse(playingTime)), margin2+itemWidth*playingIndex-timeWidth/2, h/2-CommonUtil.dip2px(mContext, 15), textP);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float lastX = event.getX();
        float lastY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //判断是否点击了播放、暂停按钮
                if (lastX > CommonUtil.dip2px(mContext, 10) && lastX < CommonUtil.dip2px(mContext, 50)
                        && lastY > CommonUtil.dip2px(mContext, 10) && lastY < CommonUtil.dip2px(mContext, 50)) {
                    isDraggingThumb = false;
                    if (STATE == STATE_NONE) {//暂停-->播放状态
                        STATE = STATE_PLAYING;
                    }else if (STATE == STATE_PLAYING){//播放-->暂停状态
                        STATE = STATE_PAUSE;
                        postInvalidate();
                    }else if (STATE == STATE_PAUSE) {
                        STATE = STATE_PLAYING;
                        postInvalidate();
                    }
                    Intent intent = new Intent();
                    intent.setAction(BROAD_CLICKMENU);
                    intent.putExtra("currentIndex", playingIndex);
                    intent.putParcelableArrayListExtra("radarList", (ArrayList<? extends Parcelable>)dataList);
                    mContext.sendBroadcast(intent);
                    Log.e("xy", lastX+"--"+lastY);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
