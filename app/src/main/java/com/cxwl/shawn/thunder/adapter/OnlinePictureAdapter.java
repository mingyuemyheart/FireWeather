package com.cxwl.shawn.thunder.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.util.CommonUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OnlinePictureAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<String> mArrayList;

	private final class ViewHolder{
		ImageView imageView;
	}

	public OnlinePictureAdapter(Context context, ArrayList<String> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.adapter_online_picture, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		String imgUrl = mArrayList.get(position);
		if (!TextUtils.isEmpty(imgUrl)) {
			Picasso.get().load(imgUrl).into(mHolder.imageView);
			LayoutParams params = mHolder.imageView.getLayoutParams();
			params.width = CommonUtil.widthPixels(context)/3;
			params.height = params.width*3/4;
			mHolder.imageView.setLayoutParams(params);
		}

		return convertView;
	}

}
