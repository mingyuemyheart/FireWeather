package com.cxwl.shawn.thunder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.thunder.adapter.ShawnNewsAdapter;
import com.cxwl.shawn.thunder.common.CONST;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;
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
import okhttp3.Request;
import okhttp3.Response;

/**
 * 消息
 */
public class ShawnNewsActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private ShawnNewsAdapter adapter;
    private List<StrongStreamDto> dataList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private AVLoadingIndicatorView loadingView;
    private int p = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_news);
        mContext = this;
        initRefreshLayout();
        initWidget();
        initGridView();
    }

    private void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
        refreshLayout.setProgressViewEndTarget(true, 300);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        dataList.clear();
        OkHttpList();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("系统消息");

        refresh();
    }

    private void initGridView() {
        ListView listView = findViewById(R.id.listView);
        adapter = new ShawnNewsAdapter(mContext, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                StrongStreamDto dto = dataList.get(position);
//                if (!TextUtils.isEmpty(dto.link)) {
//                    Intent intent = new Intent(mContext, WebviewActivity.class);
//                    intent.putExtra("url", dto.link);
//                    startActivity(intent);
//                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                p++;
                OkHttpList();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void OkHttpList() {
        final String url = "http://decision-admin.tianqi.cn/Home/other/light_getAppMessages?p="+p;
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
                                            if (!itemObj.isNull("icon")) {
                                                dto.imgUrl = itemObj.getString("icon");
                                            }
                                            if (!itemObj.isNull("type")) {
                                                dto.msgType = itemObj.getString("type");
                                            }
                                            if (!itemObj.isNull("time")) {
                                                dto.time = itemObj.getString("time");
                                            }
                                            if (!itemObj.isNull("content")) {
                                                dto.content = itemObj.getString("content");
                                            }
                                            if (!itemObj.isNull("link")) {
                                                dto.link = itemObj.getString("link");
                                            }
                                            dataList.add(dto);
                                        }

                                        if (adapter != null) {
                                            adapter.notifyDataSetChanged();
                                        }
                                        loadingView.setVisibility(View.GONE);
                                        refreshLayout.setRefreshing(false);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
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
