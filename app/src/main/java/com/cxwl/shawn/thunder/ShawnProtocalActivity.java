package com.cxwl.shawn.thunder;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 使用说明
 */
public class ShawnProtocalActivity extends ShawnBaseActivity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_protocal);
		initWidget();
	}
	
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("用户协议");
		TextView tvVersion = findViewById(R.id.tvContent);
		tvVersion.setText(
				"本《最终用户许可协议》（以下简称“本协议”）是您（个人、公司、或其他组织）与华风创新网络技术有限公司（以下简称“创新网络”）签订的关于本软件的最终用户使用许可协议。创新网络在此特别提示您仔细阅读本协议中各条款，包括但不限于用户使用须知、法律责任与免责等。您的安装或使用行为将视为接受本协议。如果您不同意本协议的条款，则不能获得安装和使用本软件的权利。\n" +
				"本协议是您与创新网络就下载、安装、使用本软件所订立的协议。本协议约定与您关于本软件使用许可方面的权利义务。\n" +
				"1. 知识产权声明\n" +
				"本软件由创新网络开发，创新网络拥有本软件的知识产权以及与本软件相关的所有信息内容，包括但不限于：文字表述及其组合、图标、图饰、图表、色彩、界面设计、版面框架、有关数据、电子文档，均受著作权法和国际著作权条约以及其他知识产权法律、法规的保护，其知识产权均归创新网络及其许可人所有。\n" +
				"2. 保护个人隐私\n" +
				"本软件尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更有个性化的服务，本软件会按照本隐私权政策的规定使用和披露您的个人信息。但本软件将以高度的勤勉、审慎义务对待这些信息。除本隐私权政策另有规定外，在未征得您事先许可的情况下，本软件不会将这些信息对外披露或向第三方提供。本软件会不时更新本隐私权政策。您在同意本软件服务使用协议之时，即视为您已经同意本隐私权政策全部内容。本隐私权政策属于本软件服务使用协议不可分割的一部分。\n" +
				"在您使用本软件网络服务，本软件自动接收并记录的您的手机上的信息，包括但不限于您的位置数据、使用的语言、访问日期和时间、软硬件特征信息及您需求的热点记录等数据。\n" +
				"在获得您的数据之后，本软件会将其上传至服务器，以生成您的使用习惯数据，以便您能够更好地使用服务。关于信息披露：本软件不会将您的信息披露给不受信任的第三方；根据法律的有关规定，或者行政或司法机构的要求，向第三方或者行政、司法机构披露；如您出现违反中国有关法律、法规或者相关规则的情况，需要向第三方披露。 \n" +
				"3. 账号与信息安全\n" +
				"在使用本软件时，请您妥善保护自己的个人信息，仅在必要的情形下向他人提供。创新网络有权审查您注册所提供的身份信息是否真实、有效，并将采取专业加密存储与传输方式等合理措施保障用户帐号安全、有效；您有义务妥善保管您的账号及密码，并正确、安全地使用您的账号及密码。任何一方未尽上述义务导至账号密码遗失、账号被盗等情形而给您和他人的民事权利造成损害的，应当承担由此产生的法律责任。\n" +
				"您对登录后所持账号产生的行为依法享有权利和承担责任。\n" +
				"如果您发现您账号或密码被他人非法使用或有使用异常的情况的，应及时根据创新网络公布的处理方式通知创新网络，并有权通知创新网络采取措施暂停该帐号的登录和使用。\n" +
				"4. 使用权限\n" +
				"您在遵守国家法律、法规、政策及本协议的前提下，可依本协议使用本软件。您无权也不得实施包括但不限于下列行为：\n" +
				"（1）删除本软件中的任何版权申明或提示以及任何其它信息、内容。\n" +
				"（2）对本软件进行反向工程、反向汇编、反向编译等。\n" +
				"（3）在本协议规定的条款之外，使用、复制、修改、租赁或转让本软件或其中的任一部份。\n");
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
