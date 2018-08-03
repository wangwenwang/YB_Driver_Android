package com.example.administrator.ybdriver.ui.base;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppManager;
import com.kaidongyuan.app.basemodule.ui.activity.BaseInputActivity;
import com.umeng.message.PushAgent;


/**
 * baseActivity 封装一些通用方法 所有的activity继承自该Activity
 *
 * @author ke
 */
public abstract class BaseActivity extends BaseInputActivity {
    private final String prefName = "account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        AppManager.getAppManager().addActivity(this);
        //友盟推送统计应用启动数据
        PushAgent.getInstance(getMContext()).onAppStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

	/*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
		public void onReceive(Context context, Intent intent) {
			unregisterReceiver(this);
			((Activity) context).finish();
		}
	};*/

    public void onResume() {
        super.onResume();
//		StatService.onResume(this);//百度统计页面
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("exit");
//		registerReceiver(this.broadcastReceiver, filter);
    }

    protected void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();

    }

    @Override
    public void onPause() {
        super.onPause();
//		StatService.onPause(this);//百度统计页面
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//		unregisterReceiver(broadcastReceiver);
//		AppManager.getAppManager().finishActivity(this);
    }


}
