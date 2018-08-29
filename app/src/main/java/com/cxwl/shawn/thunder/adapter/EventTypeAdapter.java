package com.cxwl.shawn.thunder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;

import java.util.List;

/**
 * 事件类型
 */
public class EventTypeAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<StrongStreamDto> mArrayList;

	private final class ViewHolder{
		TextView tvEventType;
	}

	public EventTypeAdapter(Context context, List<StrongStreamDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_event_type, null);
			mHolder = new ViewHolder();
			mHolder.tvEventType = convertView.findViewById(R.id.tvEventType);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

        StrongStreamDto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.eventType)) {
		    mHolder.tvEventType.setText(dto.eventType);
        }

		return convertView;
	}

}
