package com.example.administrator.ybdriver.ui.activity;



import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.adapter.MainViewpageAdapter;
import com.example.administrator.ybdriver.app.AppManager;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.servers.TrackingService;
import com.example.administrator.ybdriver.ui.base.BaseFragmentActivity;
import com.example.administrator.ybdriver.ui.fragment.FinishedFragment;
import com.example.administrator.ybdriver.ui.fragment.IndexFragment;
import com.example.administrator.ybdriver.ui.fragment.MineFragment;
import com.example.administrator.ybdriver.ui.fragment.UnpayedFragment;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.MPermissionsUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.NetworkUtils;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/1.
 */
public class MainActivity extends BaseFragmentActivity implements AsyncHttpCallback {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private OrderAsyncHttpClient mClient;
    private List<String> tablist;
    private List<Integer>imagesrcs;
    private FragmentManager mfragmentManager;
    private List<Fragment>fragments;
    //退出应用
    private long firstTime;
    private long secondTime;
    private long spaceTime;
    //检测版本更新
    private final String TAG_CHECKVERSION = "check_version";
    private final String DestFileName="ybdriver.apk";
    private AlertDialog mUpdataVersionDialog;
    private NotificationManager mNotificationManager;
    private Notification mUpdataNotification;
    private Handler mHandler;
    private MineFragment mineFragment;
    private Intent mLocationIntent;
    private RemoteViews remoteView;
    private final  int REQUESTLOCATION_STATUS_CODE=8888;//android6.0 查询定位授权的标志码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        // 实例化轨迹服务客户端
//        client=new LBSTraceClient(getApplicationContext());

        initview();
        initHandler();
        checkVersion();
        if (Build.VERSION.SDK_INT>=23) {
            MPermissionsUtil.checkAndRequestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION},REQUESTLOCATION_STATUS_CODE);
        }
        //开启定位上传位置的后台服务
        if (mLocationIntent == null) {
            mLocationIntent = new Intent(this, TrackingService.class);
        }
      // getApplicationContext().startService(mLocationIntent);
      //  startService(new Intent(MainActivity.this, TrackingService.class));
        startUmengPush();
      //  startTrace();
    }

    //启动和配置友盟推送
    private void startUmengPush() {
        PushAgent mPushAgent=PushAgent.getInstance(getMContext());
        mPushAgent.enable(new IUmengRegisterCallback() {
            @Override
            public void onRegistered(final String s) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        //获取设备注册的友盟唯一ID:Device Token
                        MLog.i("UMengID:\tdevice_token="+s);
                    }
                });
            }
        });
        mPushAgent.setMessageChannel("YBDriver");
    }

    public void checkVersion() {
        Map<String, String> params = new HashMap<>();
        params.put("strLicense", "");
        mClient.sendRequest(Constants.URL.CheckVersion, params, TAG_CHECKVERSION);
    }
    /**
     * 根据下载的进度调整 notification 中的进度，如果是下载完成就安装
     */
    private void initHandler(){
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                int percent = msg.arg1;
                //msg 中的 what 值如果是 -1 的话表示下载完成，开启安装界面，否则改变 dialog 中的状态
                if (percent==100){
                    createNotifaction(percent);
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),DestFileName);
                    if (!file.exists()) {
                        Toast.makeText(getMContext(), "升级包不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = Uri.fromFile(file);
                        String type = "application/vnd.android.package-archive";//.apk 的 mime 名
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, type);
                        startActivity(intent);
                    }
                    mNotificationManager.cancel(0);
                } else if (percent==-1) {
                    Toast.makeText(MainActivity.this, "下载更新失败", Toast.LENGTH_LONG).show();
                    mNotificationManager.cancel(0);
                } else{
                    createNotifaction(percent);
                }
                return false;
            }
        });
    }

    private void initview() {
        viewPager= (ViewPager) findViewById(R.id.viewPager_mainActivity);
        tablist = new ArrayList<>();
        fragments=new ArrayList<>();
        tablist.add(getString(R.string.index_page));
        tablist.add(getString(R.string.unpayed_order));
        tablist.add(getString(R.string.payed_order));
        tablist.add(getString(R.string.mine));
        fragments.add(new IndexFragment());
        //模版分块开发
        fragments.add(new UnpayedFragment());
        fragments.add(new FinishedFragment());
        mineFragment = new MineFragment();
        fragments.add(mineFragment);
        tabLayout= (TabLayout) findViewById(R.id.tablayout_mainAcitivity);
        for (int i=0;i<tablist.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(tablist.get(i)));
        }
        imagesrcs=new ArrayList<>();
        imagesrcs.add(R.drawable.tab_index_selector);
        imagesrcs.add(R.drawable.tab_unpayed_order_selector);
        imagesrcs.add(R.drawable.tab_payed_order_selector);
        imagesrcs.add(R.drawable.tab_mine_selector);
        mfragmentManager=getSupportFragmentManager();
        MainViewpageAdapter mainViewpageAdapter=new MainViewpageAdapter(mfragmentManager,fragments,tablist,imagesrcs,MainActivity.this);
        viewPager.setAdapter(mainViewpageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int j=0;j<tabLayout.getTabCount();j++){
            TabLayout.Tab tab=tabLayout.getTabAt(j);
            tab.setCustomView(mainViewpageAdapter.getTabView(tablist.get(j),imagesrcs.get(j)));
        }
       // tabLayout.getTabAt(0);
        mClient=new OrderAsyncHttpClient(this,this);
        mNotificationManager = (NotificationManager) getMContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 创建下载进度的 notification
     * @param percent 下载进度
     */
    private void createNotifaction(int percent){
        //自定义 Notification 布局
        if (mUpdataNotification==null) {
            mUpdataNotification = new Notification();
            mUpdataNotification.icon =R.mipmap.ic_launcher;
            mUpdataNotification.tickerText =getResources().getText(R.string.app_name);
        }
        if (remoteView==null) {
            remoteView = new RemoteViews(getMContext().getPackageName(), R.layout.dialog_download);
        }
        remoteView.setTextViewText(R.id.textView_dialog_download, percent+"%");
        remoteView.setProgressBar(R.id.progressBar_dialog_download, 100, percent, false);
        mUpdataNotification.contentView = remoteView;
        mNotificationManager.notify(0, mUpdataNotification);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            firstTime = System.currentTimeMillis();
            spaceTime=firstTime-secondTime;
            secondTime=firstTime;
            if (spaceTime>2000){
                showToastMsg("再按一次退出"+getString(R.string.app_name));
                return false;
            }else {
                goHomeActivity();
                //AppManager.getAppManager().exitAllActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goHomeActivity() {
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.cancleRequest(TAG_CHECKVERSION);
    }

    @Override
    public void setProgressBarLoading(int progress) {
       // super.setProgressBarLoading(progress);
        Message message=mHandler.obtainMessage();
        message.arg1=progress;
        message.sendToTarget();
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")){
            if (request_tag.equals(DestFileName)){
                Message message=mHandler.obtainMessage();
                message.arg1=-1;
                message.sendToTarget();
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(getMContext())){
                NetworkUtils.setContactNetDialog(getApplication());
                return;
            }
            MLog.i(TAG_CHECKVERSION+":版本检测失败!");
        }else if (request_tag.equals(TAG_CHECKVERSION)){
            com.alibaba.fastjson.JSONObject object= JSON.parseObject(msg);
            JSONArray arr = object.getJSONArray("result");
            int size = arr.size();
            String version = null;
            String downUrl = null;
            com.alibaba.fastjson.JSONObject obj;
            String netDownUrl;
            int startIndex;
            for (int i=0; i<size; i++){
                obj = arr.getJSONObject(i);
                netDownUrl = obj.getString("DownAddress");
                startIndex = netDownUrl.indexOf('/')+1;
                String appName = netDownUrl.substring(startIndex, netDownUrl.length());
                if (DestFileName.equals(appName)){
                    version = obj.getString("VersionCode");
                    downUrl = Constants.URL.Load_Url + netDownUrl;
                    break;
                }
            }
            if (version!=null && downUrl!=null) {
                try {
                    String currentVersion = getMContext().getPackageManager().getPackageInfo(getMContext().getPackageName(), 0).versionName;
                    MLog.w( "version:"+version+"\tcurrentVersion:"+currentVersion);
                    if (!currentVersion.equals(version)) {
                        createUpdateDialog(currentVersion, version, downUrl);
                        mineFragment.isupdate=true;
                    } else {
                        Toast.makeText(getMContext(), "当前版本是最新的版本", Toast.LENGTH_SHORT).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public void createUpdateDialog(String currentVersion, String version, final String downUrl) {
        if (mUpdataVersionDialog==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getMContext());
            builder.setMessage("当前版本：" + currentVersion + "\n最新版本：" + version);
            builder.setCancelable(false);
            final int current = (int) Float.parseFloat(currentVersion);
            final int net = (int) Float.parseFloat(version);
            if (current==net) {
                builder.setTitle("更新版本");
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdataVersionDialog.cancel();
                    }
                });
            } else {
                builder.setTitle("当前版本过低，需下载新版本");
            }
            builder.setNegativeButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mUpdataVersionDialog.cancel();
                    MLog.w("update.url:" + downUrl);
                    //以存储文件名为Tag名
                    mClient.sendFileRequest(downUrl, DestFileName);
                    if (current != net) {
                        goHomeActivity();
                    }
                }
            });
            mUpdataVersionDialog = builder.create();
        }
        mUpdataVersionDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUESTLOCATION_STATUS_CODE:
                if (grantResults.length>0){
                    for (int code:grantResults){
                        if (code==PackageManager.PERMISSION_DENIED){
                            showToastMsg("请授权应用网络定位和GPS定位权限",5000);
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                            break;
                        }
                    }
                }
        }
    }
}
