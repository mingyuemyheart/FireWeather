package com.cxwl.shawn.thunder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextUtils;
import android.view.View;

import com.cxwl.shawn.thunder.common.CONST;
import com.cxwl.shawn.thunder.dto.StationDto;
import com.cxwl.shawn.thunder.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 降水曲线
 * @author shawn_sun
 */
public class ChartRainView extends View {

	private Context mContext;
	private ArrayList<StationDto> tempList = new ArrayList<>();
	private float maxValue = 0;
	private float minValue = 0;
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.CHINA);
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	public ChartRainView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		lineP = new Paint();
		lineP.setStyle(Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);

		textP = new Paint();
		textP.setAntiAlias(true);
	}

	/**
	 * 对cubicView进行赋值
	 */
	public void setData(ArrayList<StationDto> dataList) {
		if (!dataList.isEmpty()) {
			tempList.addAll(dataList);

			String temp = tempList.get(0).getPrecipitation1h();
			for (int i = 0; i < tempList.size(); i++) {
				StationDto dto = tempList.get(i);
				if (!TextUtils.isEmpty(dto.getPrecipitation1h()) && !TextUtils.equals(dto.getPrecipitation1h(), CONST.noValue)) {
					temp = dto.getPrecipitation1h();
					break;
				}
			}
			if (!TextUtils.isEmpty(temp) && !TextUtils.equals(temp, CONST.noValue)) {
				maxValue = Float.valueOf(temp);
				minValue = Float.valueOf(temp);
				for (int i = 0; i < tempList.size(); i++) {
					StationDto dto = tempList.get(i);
					if (!TextUtils.isEmpty(dto.getPrecipitation1h()) && !TextUtils.equals(dto.getPrecipitation1h(), CONST.noValue)) {
						if (maxValue <= Float.valueOf(dto.getPrecipitation1h())) {
							maxValue = Float.valueOf(dto.getPrecipitation1h());
						}
						if (minValue >= Float.valueOf(dto.getPrecipitation1h())) {
							minValue = Float.valueOf(dto.getPrecipitation1h());
						}
					}
				}
			}

			if (maxValue == 0 && minValue == 0) {
				maxValue = 5;
				minValue = 0;
			}else {
				int totalDivider = (int) (Math.ceil(Math.abs(maxValue))+Math.floor(Math.abs(minValue)));
				int itemDivider = 2;
				if (totalDivider <= 20) {
					itemDivider = 2;
				}else if (totalDivider <= 40) {
					itemDivider = 5;
				}else if (totalDivider <= 60) {
					itemDivider = 10;
				}else {
					itemDivider = 15;
				}

				maxValue = (float) (Math.ceil(maxValue)+itemDivider);
//				minValue = (float) (Math.floor(minValue)-itemDivider);
				minValue = 0;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempList.isEmpty()) {
			return;
		}

		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w- CommonUtil.dip2px(mContext, 40);
		float chartH = h-CommonUtil.dip2px(mContext, 20);
		float leftMargin = CommonUtil.dip2px(mContext, 20);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 5);
		float bottomMargin = CommonUtil.dip2px(mContext, 15);
		float chartMaxH = chartH * maxValue / (Math.abs(maxValue)+Math.abs(minValue));//同时存在正负值时，正值高度

		float columnWidth = chartW/(tempList.size()-1);
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < tempList.size(); i++) {
			StationDto dto = tempList.get(i);
			dto.setX(columnWidth * i + leftMargin);
			dto.setY(0);

			if (!TextUtils.isEmpty(dto.getPrecipitation1h()) && !TextUtils.equals(dto.getPrecipitation1h(), CONST.noValue)) {
				float value = Float.valueOf(dto.getPrecipitation1h());
				if (value >= 0) {
					dto.setY(chartMaxH - chartH * Math.abs(value) / (Math.abs(maxValue) + Math.abs(minValue)) + topMargin);
					if (minValue >= 0) {
						dto.setY(chartH - chartH * Math.abs(value) / (Math.abs(maxValue) + Math.abs(minValue)) + topMargin);
					}
				}else {
					dto.setY(chartMaxH + chartH * Math.abs(value) / (Math.abs(maxValue) + Math.abs(minValue)) + topMargin);
					if (maxValue < 0) {
						dto.setY(chartH * Math.abs(value) / (Math.abs(maxValue) + Math.abs(minValue)) + topMargin);
					}
				}
				tempList.set(i, dto);
			}
		}

		textP.setColor(0xff999999);
		textP.setTextSize(CommonUtil.dip2px(mContext, 13));
		canvas.drawText("(mm)", w-CommonUtil.dip2px(mContext, 40f), CommonUtil.dip2px(mContext, 10f), textP);

		for (int i = 0; i < 1; i++) {
			StationDto dto = tempList.get(i);
			//绘制分割线
			Path linePath = new Path();
			linePath.moveTo(dto.getX(), topMargin);
			linePath.lineTo(dto.getX(), h-bottomMargin);
			linePath.close();
			lineP.setColor(0xff999999);
			lineP.setStyle(Style.STROKE);
			canvas.drawPath(linePath, lineP);
		}

		//绘制刻度线
		lineP.setColor(0xfff1f1f1);
		textP.setColor(0xff999999);
		textP.setTextSize(CommonUtil.dip2px(mContext, 10));
		int totalDivider = (int) (Math.ceil(Math.abs(maxValue))+Math.floor(Math.abs(minValue)));
		int itemDivider;
		if (totalDivider <= 20) {
			itemDivider = 2;
		}else if (totalDivider <= 40) {
			itemDivider = 5;
		}else if (totalDivider <= 60) {
			itemDivider = 10;
		}else {
			itemDivider = 15;
		}
		for (int i = (int) minValue; i <= totalDivider; i+=itemDivider) {
			float dividerY;
			if (i >= 0) {
				dividerY = chartMaxH - chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				if (minValue >= 0) {
					dividerY = chartH - chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}else {
				dividerY = chartMaxH + chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				if (maxValue < 0) {
					dividerY = chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}
			canvas.drawLine(leftMargin, dividerY, w-rightMargin, dividerY, lineP);
			canvas.drawText(String.valueOf(i), CommonUtil.dip2px(mContext, 5), dividerY, textP);
		}

		//绘制曲线
		for (int i = 0; i < tempList.size()-1; i++) {
			float x1 = tempList.get(i).getX();
			float y1 = tempList.get(i).getY();
			float x2 = tempList.get(i+1).getX();
			float y2 = tempList.get(i+1).getY();

			float wt = (x1 + x2) / 2;

			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			if (y2 != 0 && y3 != 0 && y4 != 0) {
				Path linePath = new Path();
				linePath.moveTo(x1, y1);
				linePath.cubicTo(x3, y3, x4, y4, x2, y2);
				lineP.setColor(0xffef4900);
				lineP.setStyle(Style.STROKE);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 3));
				canvas.drawPath(linePath, lineP);
			}
		}

		for (int i = 0; i < tempList.size(); i++) {
			StationDto dto = tempList.get(i);

			if (dto.getX() != 0 && dto.getY() != 0) {
				//绘制白点
				lineP.setColor(Color.WHITE);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5));
				canvas.drawPoint(dto.getX(), dto.getY(), lineP);
			}

			//绘制数值、时间
			if (i % 2 == 0) {
				if (!TextUtils.isEmpty(dto.getPrecipitation1h()) && !TextUtils.equals(dto.getPrecipitation1h(), CONST.noValue)) {
					//绘制曲线上每个点的数据值
					textP.setColor(Color.WHITE);
					textP.setTextSize(CommonUtil.dip2px(mContext, 10));
					float tempWidth = textP.measureText(dto.getPrecipitation1h());
					canvas.drawText(dto.getPrecipitation1h(), dto.getX()-tempWidth/2, dto.getY()-CommonUtil.dip2px(mContext, 10), textP);
				}

				//绘制24小时
				textP.setColor(0xff999999);
				textP.setTextSize(CommonUtil.dip2px(mContext, 12));
				String time = null;
				try {
					time = sdf2.format(sdf1.parse(dto.getDatatime()));
				} catch (ParseException e) {
					e.printStackTrace();
					try {
						time = sdf2.format(sdf3.parse(dto.getDatatime()));
					} catch (ParseException ex) {
						ex.printStackTrace();
					}
				}
				if (!TextUtils.isEmpty(time)) {
					float text = textP.measureText(time+"时");
					textP.setColor(0xff999999);
					textP.setTextSize(CommonUtil.dip2px(mContext, 10));
					canvas.drawText(time+"时", dto.getX()-text/2, h-CommonUtil.dip2px(mContext, 5f), textP);
				}
			}
		}

		lineP.reset();
		textP.reset();
	}

}
