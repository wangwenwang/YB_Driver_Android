package com.example.administrator.ybdriver.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.utils.StringUtils;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.network.BaseAsyncHttpClient;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册新司机帐号
 */
public class RegisterActivity extends BaseActivity implements AsyncHttpCallback {

	private EditText user_phone_edit;
	private EditText user_psw_edit;
	private EditText repeat_psw_edit;
	private String phone;
	private BaseAsyncHttpClient mClient;
	private final String TAG_REGISTER = "tag_register";
	private String newPassword;
	private String repeatPassword;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		SlidingTitleView titleView;
		titleView = (SlidingTitleView) findViewById(R.id.register_title_view);
		titleView.setText("新用户注册");
		titleView.setMode(SlidingTitleView.MODE_BACK);
		user_phone_edit = (EditText) findViewById(R.id.user_phone_edit);
		user_psw_edit = (EditText) findViewById(R.id.user_psw_edit);
		repeat_psw_edit = (EditText) findViewById(R.id.repeat_psw_edit);
		mClient = new OrderAsyncHttpClient(this, this);

		findViewById(R.id.btn_register_driver).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkInput()){
					newUserRegister();
				}
			}
		});
	}

	/**
	 * 根据已有的密码重设密码，需要输入原有密码
	 */
	private boolean checkInput(){
		phone=user_phone_edit.getText().toString().trim();
		//oldPassword = oldPswEditText.getText().toString();
		newPassword = user_psw_edit.getText().toString().trim();
		repeatPassword = repeat_psw_edit.getText().toString().trim();
		if (StringUtils.isBlank(phone)){
			showToast("请输入用户手机号码，用于登录帐号和关联物流订单",100);
			return false;
		}
		if (!com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils.isMobileNO(phone)){
			showToast("请输入正确的手机号码！",100);
			return false;
		}
		if (StringUtils.isBlank(newPassword)) {
			showToast("请输入密码", 100);
			return false;
		}
		if (StringUtils.isBlank(repeatPassword)) {
			showToast("请再输入一次密码", 100);
			return false;
		}
		if (!repeatPassword.equals(newPassword)) {
			showToast("两次输入密码不一致", 100);
			return false;
		}
		if (newPassword.length() < 6) {
			showToast("密码不能少于6位数字和字母", 100);
			return false;
		}
		return true;
	}

	public void newUserRegister() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("user", phone.trim());//传入的是用户手机号，而非用户名
		params.put("pwd", newPassword.trim());
		params.put("strLicense", "");
		mClient.sendRequest(Constants.URL.Register, params, TAG_REGISTER, true);
	}

	@Override
	protected void onDestroy() {
		mClient.cancleRequest(TAG_REGISTER);
		super.onDestroy();
	}

	@Override
	public void postSuccessMsg(String msg, String request_tag) {
		if (msg.equals("error")){
			showToast("网络请求失败", Toast.LENGTH_SHORT);
			return;
		}
		if (request_tag.equals(TAG_REGISTER)){
			JSONObject jo= JSON.parseObject(msg);
			//修改密码成功则销毁updatePwdActivity
			if (jo.getString("type").equals("1")){
				showToast("用户帐号："+phone+"，注册成功!", Toast.LENGTH_SHORT);
				RegisterActivity.this.finish();
			}else {
				showToast("注册失败，"+msg,Toast.LENGTH_SHORT);
			}
		}
	}
}
