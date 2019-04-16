package com.cxwl.shawn.thunder.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cxwl.shawn.thunder.ShawnPDFActivity;
import com.cxwl.shawn.thunder.R;
import com.cxwl.shawn.thunder.ShawnSurfaceViewActivity;
import com.cxwl.shawn.thunder.adapter.PictureLibraryAdapter;
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
 * 雷电科普
 */
public class FragmentKepu1 extends Fragment {

    private PictureLibraryAdapter picAdapter;
    private List<StrongStreamDto> picList = new ArrayList<>();
    private AVLoadingIndicatorView loadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_kepu1, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        initGridView(view);
    }

    private void initWidget(View view) {
        loadingView = view.findViewById(R.id.loadingView);

        OkHttpPictures();
    }

    private void initGridView(View view) {
        GridView gridView = view.findViewById(R.id.gridView);
        picAdapter = new PictureLibraryAdapter(getActivity(), picList);
        gridView.setAdapter(picAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                StrongStreamDto dto = picList.get(arg2);
                if (!TextUtils.isEmpty(dto.dataUrl)) {
                    Intent intent;
                    if (dto.dataUrl.endsWith(".pdf") || dto.dataUrl.endsWith(".PDF")) {
                        intent = new Intent(getActivity(), ShawnPDFActivity.class);
                        intent.putExtra("title", dto.eventContent);
                        intent.putExtra("dataUrl", dto.dataUrl);
                        startActivity(intent);
                    }else if (dto.dataUrl.endsWith(".mp4") || dto.dataUrl.endsWith(".MP4")) {
                        intent = new Intent(getActivity(), ShawnSurfaceViewActivity.class);
                        intent.putExtra("title", dto.eventContent);
                        intent.putExtra("dataUrl", dto.dataUrl);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * 获取图库数据
     */
    private void OkHttpPictures() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://lightning.app.tianqi.cn/lightning/lhdata/ldsp";
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("data")) {
                                            JSONArray array = obj.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                StrongStreamDto dto = new StrongStreamDto();
                                                JSONObject itemObj = array.getJSONObject(i);
                                                if (!itemObj.isNull("name")) {
                                                    dto.eventContent = itemObj.getString("name");
                                                }
                                                if (!itemObj.isNull("nametb")) {
                                                    dto.imgUrl = itemObj.getString("nametb");
                                                }
                                                if (!itemObj.isNull("names")) {
                                                    dto.dataUrl = itemObj.getString("names");
                                                }
                                                picList.add(dto);
                                            }

                                            if (picAdapter != null) {
                                                picAdapter.notifyDataSetChanged();
                                            }
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

}
