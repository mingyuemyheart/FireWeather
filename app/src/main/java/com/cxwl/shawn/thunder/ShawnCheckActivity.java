package com.cxwl.shawn.thunder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.adapter.ShawnCheckAdapter;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;
import com.cxwl.shawn.thunder.swipemenulistview.SwipeMenu;
import com.cxwl.shawn.thunder.swipemenulistview.SwipeMenuCreator;
import com.cxwl.shawn.thunder.swipemenulistview.SwipeMenuItem;
import com.cxwl.shawn.thunder.swipemenulistview.SwipeMenuListView;
import com.cxwl.shawn.thunder.util.CommonUtil;
import com.cxwl.shawn.thunder.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 审核
 */
public class ShawnCheckActivity extends ShawnBaseActivity implements View.OnClickListener {

	private Context mContext;
	private ShawnCheckAdapter mAdapter;
	private List<StrongStreamDto> dataList = new ArrayList<>();
	private int page = 1;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_check);
		mContext = this;
		initWidget();
		initListView();
	}

	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("图片审核");

		page = 1;
		dataList.clear();
		OkHttpPictures();
	}

	/**
	 * 初始化listview
	 */
	private void initListView() {
		SwipeMenuListView mListView = findViewById(R.id.listView);
		mAdapter = new ShawnCheckAdapter(this, dataList);
		mListView.setAdapter(mAdapter);
		
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
				case 0:
					createMenu1(menu, "审核通过", "审核拒绝");
					break;
				case 1:
					createMenu1(menu, "未审核", "审核拒绝");
					break;
				case 2:
					createMenu1(menu, "未审核", "审核通过");
					break;
				}
			}

			private void createMenu1(SwipeMenu menu, String name1, String name2) {
				SwipeMenuItem item1 = new SwipeMenuItem(mContext);
				item1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				item1.setWidth((int) CommonUtil.dip2px(mContext, 50));
				item1.setTitle(name1);
				item1.setTitleColor(getResources().getColor(R.color.white));
				item1.setTitleSize(14);
				menu.addMenuItem(item1);
				SwipeMenuItem item2 = new SwipeMenuItem(mContext);
				item2.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
				item2.setWidth((int) CommonUtil.dip2px(mContext, 50));
				item2.setTitle(name2);
				item2.setTitleColor(getResources().getColor(R.color.white));
				item2.setTitleSize(14);
				menu.addMenuItem(item2);
			}
		};
		mListView.setMenuCreator(creator);
		
		mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				StrongStreamDto dto = dataList.get(position);
				switch (index) {
				case 0:
					if (menu.getViewType() == 0) {
						dto.status = "1";
						OkHttpCheck(dto.eventId, dto.status);
					}else if (menu.getViewType() == 1) {
						dto.status = "0";
						OkHttpCheck(dto.eventId, dto.status);
					}else if (menu.getViewType() == 2) {
						dto.status = "0";
						OkHttpCheck(dto.eventId, dto.status);
					}
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				case 1:
					if (menu.getViewType() == 0) {
						dto.status = "-1";
						OkHttpCheck(dto.eventId, dto.status);
					}else if (menu.getViewType() == 1) {
						dto.status = "-1";
						OkHttpCheck(dto.eventId, dto.status);
					}else if (menu.getViewType() == 2) {
						dto.status = "1";
						OkHttpCheck(dto.eventId, dto.status);
					}
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		});
		
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
					OkHttpPictures();
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
		
	}
	
	/**
	 * 视频审核
	 */
	private void OkHttpCheck(String id, String status) {
		final String url = "http://decision-admin.tianqi.cn/Home/other/light_check_img";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("id", id);
		builder.add("review", status);
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						String result = response.body().string();
						if (result != null) {
							try {
								JSONObject object = new JSONObject(result);
								if (!object.isNull("status")) {
									int status  = object.getInt("status");
									if (status == 1) {//审核成功

									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		}).start();
	}

	/**
	 * 获取图库数据
	 * review：审核状态（1：审核通过，0：未审核，-1：审核拒绝）
	 */
	private void OkHttpPictures() {
		loadingView.setVisibility(View.VISIBLE);
		final String url = String.format("http://decision-admin.tianqi.cn/Home/other/light_get_upload_imgs/page/%s/pageSize/%s", page, 30);
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONArray array = new JSONArray(result);
										for (int i = 0; i < array.length(); i++) {
											StrongStreamDto dto = new StrongStreamDto();
											JSONObject itemObj = array.getJSONObject(i);
											if (!itemObj.isNull("review_status")) {
												dto.status = itemObj.getString("review_status");
											}
											if (!itemObj.isNull("id")) {
												dto.eventId = itemObj.getString("id");
											}
											if (!itemObj.isNull("content")) {
												dto.eventContent = itemObj.getString("content");
											}
											if (!itemObj.isNull("imgs")) {
												JSONArray imgs = itemObj.getJSONArray("imgs");
												if (imgs.length() > 0) {
													dto.imgUrl = imgs.getString(0);
												}
											}
											dataList.add(dto);
										}

										if (mAdapter != null) {
											mAdapter.notifyDataSetChanged();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								loadingView.setVisibility(View.GONE);
							}
						});
					}
				});
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llBack:
				finish();
				break;
		}
	}

}
