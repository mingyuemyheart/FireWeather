package com.cxwl.shawn.thunder;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.thunder.util.CommonUtil;

/**
 * 使用说明
 */
public class ShawnIntroActivity extends ShawnBaseActivity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_intro);
		initWidget();
	}
	
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("使用说明");
		TextView tvVersion = findViewById(R.id.tvContent);
		tvVersion.setText("1、在正常情况下，占用流量非常小，不影响您的使用。\n" +
				"2、在进行天气雷达图形产品以及动画展示时，建议利用WIFI环境。APP给您提供了相关设置。\n" +
				"3、为了强调雷电预报，其它天气信息显示文字会缩小，请您谅解。\n" +
				"4、雷电预报文字进行了简化和精练。更多信息请点击APP相关内容查看。\n" +
				"5、雷电的历史统计是基于气象部门覆盖全国的闪电定位观测进行统计，受观测站点空间密度和地形遮挡影响，在偏远和地形复杂地区雷电的探测率会下降。\n" +
				"6、科普内容还有待完善，不足之处敬请谅解，我们在持续努力改进。\n" +
				"7、本APP的雷电预报结果仅供用户参考，在雷电多发季节，还请注意接收气象部门发布的雷电预警信息。");
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
