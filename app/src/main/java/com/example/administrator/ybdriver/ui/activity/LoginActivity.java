package com.example.administrator.ybdriver.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.app.AppManager;
import com.example.administrator.ybdriver.bean.User;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.Des3;
import com.kaidongyuan.app.basemodule.utils.nomalutils.MPermissionsUtil;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/6/2.
 */
public class LoginActivity extends BaseActivity implements AsyncHttpCallback, View.OnClickListener{
    private SlidingTitleView tilteView;
    private EditText username_edit;
    private EditText userpsw_edit;
    private Button login_bn,register_btn;
    private final String LOGIN = "login";
    private String name, pwd;
    private OrderAsyncHttpClient mClient;
    private final int REQUESTCAMERA_STATUS_CODE=888;
    private boolean isWRITE_EXTERNAL_STORAGE=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT>=23) {
            MPermissionsUtil.checkAndRequestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCAMERA_STATUS_CODE);
        }
        initView();
    }

    private void initView() {
        tilteView= (SlidingTitleView) findViewById(R.id.title_view_login);
        tilteView.setMode(SlidingTitleView.MODE_NULL);
        tilteView.setText(getString(R.string.login_now));
        username_edit= (EditText) findViewById(R.id.user_name_edit);
        userpsw_edit= (EditText) findViewById(R.id.user_psw_edit);
        if (SharedPreferencesUtils.getValueByName(Constants.BasicInfo,Constants.IsUsedApp,2)){
            username_edit.setText((CharSequence) SharedPreferencesUtils.getValueByName(Constants.BasicInfo, Constants.UserName, 0));
            String strpwd=SharedPreferencesUtils.getValueByName(Constants.BasicInfo,Constants.UserPWD,0);
            try {
                userpsw_edit.setText((CharSequence)Des3.decode(strpwd));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        login_bn= (Button) findViewById(R.id.login_btn);
        register_btn= (Button) findViewById(R.id.register_btn);
        login_bn.setOnClickListener(this);
        register_btn.setOnClickListener(this);
        mClient = new OrderAsyncHttpClient(this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn: login();
                break;
            case R.id.register_btn:Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void login() {
        if (Build.VERSION.SDK_INT>=23) {
            MPermissionsUtil.checkAndRequestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCAMERA_STATUS_CODE);
        }else {
            isWRITE_EXTERNAL_STORAGE=true;
        }
        if (isWRITE_EXTERNAL_STORAGE){
            name=username_edit.getText().toString().trim();
            pwd=userpsw_edit.getText().toString().trim();
            if (name.equals("")){
                showToastMsg("请输入用户名~");
                return;
            }
            if (pwd.equals("")){
                showToastMsg("请输入密码~");
                return;
            }
            Map<String,String>params=new HashMap<>();
            params.put("strUserName",name);
            params.put("strPassword",pwd);
            params.put("strLicense", "");
            mClient.sendRequest(Constants.URL.Login, params, LOGIN);
            return;
        }else {
            showToastMsg("请允许应用使用SD卡存储后再登录",5000);
        }
    }

    public void saveSharedData(){
        SharedPreferencesUtils.WriteSharedPreferences(Constants.BasicInfo,Constants.UserName,name);
        try {
            SharedPreferencesUtils.WriteSharedPreferences(Constants.BasicInfo,Constants.UserPWD, Des3.encode(pwd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferencesUtils.WriteSharedPreferences(Constants.BasicInfo,Constants.IsUsedApp,true);

//        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesUtils.SHARED_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
//        editor.putString("name", name);
//        editor.putString("pwd", pwd);
//        //2016.6.6 陈翔 注销掉登录的角色类型“司机”、“客户”
//        //   editor.putString("roleNames", tv_select_role.getText().toString().trim());
//        editor.apply();//提交修改
    }

    @Override
    protected void onDestroy() {
        mClient.cancleRequest(LOGIN);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUESTCAMERA_STATUS_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //已获取权限
                    isWRITE_EXTERNAL_STORAGE=true;
                }else{
                    isWRITE_EXTERNAL_STORAGE=false;
                    showToastMsg("请允许应用使用SD卡存储",3000);
//                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                    startActivity(intent);

                }
                break;
        }

    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        synchronized (LoginActivity.this) {

            if (msg.equals("error")) return;
            if (request_tag.equals(LOGIN) && !AppContext.IS_LOGIN) {
//                try {
//                    Thread.currentThread().sleep(1000*3);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                login();
//                return;
                JSONObject jo = JSON.parseObject(msg);
                try {
                    List<User> user = JSON.parseArray(jo.getString("result"), User.class);
                    MLog.e("user.size():" + user.size() + "IDX:" + user.get(0).getIDX());
                    if (user.size() > 0) {
                        user.get(0).setUSER_CODE(username_edit.getText().toString().trim());
                        //   MLog.e("User:"+user.get(0).getIDX());
                        AppContext.getInstance().setUser(user.get(0));
                        AppContext.IS_LOGIN = true;
                    }
                    saveSharedData();
                    SharedPreferencesUtils.saveUserId(AppContext.getInstance().getUser().getIDX());
                    AppManager.getAppManager().finishActivity(MainActivity.class);
                    // startActivity(new Intent(this, MainActivity.class));
                    mStartActivity(MainActivity.class);
                   // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

