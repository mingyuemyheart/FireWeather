package com.cxwl.shawn.thunder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.dto.PhotoDto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 灾情报告列表
 */
public class DisasterReportListviewAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<PhotoDto> mArrayList;

	private final class ViewHolder{
		ImageView imageView;
		TextView tvTitle, tvContent, tvTime;
	}

	public DisasterReportListviewAdapter(Context context, List<PhotoDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.adapter_disaster_listview_report, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		PhotoDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.url)) {
			Picasso.get().load(dto.url).into(mHolder.imageView);
		}

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}
		if (!TextUtils.isEmpty(dto.location)) {
			mHolder.tvContent.setText("拍摄地点："+dto.location);
		}
		if (!TextUtils.isEmpty(dto.createTime)) {
			mHolder.tvTime.setText("拍摄时间："+dto.createTime);
		}
		
		return convertView;
	}

}
