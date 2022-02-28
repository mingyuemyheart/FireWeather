package com.cxwl.shawn.thunder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.dto.PhotoDto;
import com.cxwl.shawn.thunder.util.CommonUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 灾情报告列表
 */
public class DisasterReportAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;

	private final class ViewHolder{
		ImageView imageView;
		TextView tvName;
	}

	public DisasterReportAdapter(Context context, List<PhotoDto> mArrayList) {
		this.context = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_disaster_report, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		PhotoDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.url)) {
			Picasso.get().load(dto.url).into(mHolder.imageView);
			LayoutParams params = mHolder.imageView.getLayoutParams();
			params.width = (int)(CommonUtil.widthPixels(context)/2-CommonUtil.dip2px(context, 10f));
			params.height = params.width*3/4;
			mHolder.imageView.setLayoutParams(params);
		}

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvName.setText(dto.title);
		}
		
		return convertView;
	}

}
