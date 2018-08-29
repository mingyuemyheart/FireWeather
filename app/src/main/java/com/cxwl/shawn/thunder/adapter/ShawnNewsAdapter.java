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
import com.cxwl.shawn.thunder.dto.StrongStreamDto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 消息
 */
public class ShawnNewsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<StrongStreamDto> mArrayList;

	private final class ViewHolder{
		ImageView imageView;
		TextView tvType,tvTime,tvContent;
	}

	public ShawnNewsAdapter(Context context, List<StrongStreamDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_news, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvType = convertView.findViewById(R.id.tvType);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		StrongStreamDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.msgType)) {
			mHolder.tvType.setText(dto.msgType);
		}

		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText(dto.time);
		}

		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent.setText(dto.content);
		}

		if (!TextUtils.isEmpty(dto.imgUrl)) {
			Picasso.get().load(dto.imgUrl).error(R.drawable.shawn_icon_news).into(mHolder.imageView);
		}else {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_news);
		}

		return convertView;
	}
	
}
