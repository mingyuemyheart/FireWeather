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
 * 审核
 */
public class ShawnCheckAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<StrongStreamDto> mArrayList;

	private final class ViewHolder{
		ImageView imageView;
		TextView tvContent,tvPosition,tvTime,tvEventType,tvStatus;
	}

	public ShawnCheckAdapter(Context context, List<StrongStreamDto> mArrayList) {
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
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
		StrongStreamDto data = mArrayList.get(position);
		if (TextUtils.equals(data.status, "0")) {//未审核
			return 0;
		}else if (TextUtils.equals(data.status, "1")) {//审核通过
			return 1;
		}else if (TextUtils.equals(data.status, "-1")) {//审核拒绝
			return 2;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_check, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
            mHolder.tvPosition = convertView.findViewById(R.id.tvPosition);
            mHolder.tvTime = convertView.findViewById(R.id.tvTime);
            mHolder.tvEventType = convertView.findViewById(R.id.tvEventType);
			mHolder.tvStatus =  convertView.findViewById(R.id.tvStatus);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		StrongStreamDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.eventContent)) {
			mHolder.tvContent.setText(dto.eventContent);
		}

        if (!TextUtils.isEmpty(dto.addr)) {
            mHolder.tvPosition.setText("上传地点："+dto.addr);
        }

        if (!TextUtils.isEmpty(dto.time)) {
            mHolder.tvTime.setText("上传时间："+dto.time);
        }

        if (!TextUtils.isEmpty(dto.eventType)) {
            mHolder.tvEventType.setText("事件类型："+dto.eventType);
        }

		if (TextUtils.equals(dto.status, "0")) {//未审核
			mHolder.tvStatus.setText("未审核");
			mHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.yellow));
		}else if (TextUtils.equals(dto.status, "1")) {//审核通过
			mHolder.tvStatus.setText("审核通过");
			mHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.green));
		}else if (TextUtils.equals(dto.status, "-1")) {//审核拒绝
			mHolder.tvStatus.setText("审核拒绝");
			mHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
		}

		if (!TextUtils.isEmpty(dto.imgUrl)) {
			Picasso.get().load(dto.imgUrl).centerCrop().resize(360, 240).error(R.drawable.shawn_icon_seat_bitmap).into(mHolder.imageView);
		}else {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_seat_bitmap);
		}

		return convertView;
	}

}
