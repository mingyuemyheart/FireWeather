package com.cxwl.shawn.thunder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.thunder.adapter.EventTypeAdapter;
import com.cxwl.shawn.thunder.dto.StrongStreamDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件类型
 */
public class EventTypeActivity extends ShawnBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_event_type);
        initWidget();
        initListView();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("事件类型");
    }

    private void initListView() {
        final List<StrongStreamDto> list = new ArrayList<>();
        StrongStreamDto dto = new StrongStreamDto();
        dto.eventType = "人员";
        list.add(dto);
        dto = new StrongStreamDto();
        dto.eventType = "电器";
        list.add(dto);
        dto = new StrongStreamDto();
        dto.eventType = "电力设施";
        list.add(dto);
        dto = new StrongStreamDto();
        dto.eventType = "建筑";
        list.add(dto);
        dto = new StrongStreamDto();
        dto.eventType = "航空航天飞行器";
        list.add(dto);
        dto = new StrongStreamDto();
        dto.eventType = "通信设备";
        list.add(dto);

        ListView listView = findViewById(R.id.listView);
        EventTypeAdapter mAdapter = new EventTypeAdapter(this, list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StrongStreamDto dto = list.get(position);
                Intent intent = new Intent();
                intent.putExtra("eventType", dto.eventType);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
