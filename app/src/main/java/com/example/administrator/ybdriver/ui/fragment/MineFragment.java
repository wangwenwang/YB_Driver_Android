package com.example.administrator.ybdriver.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.app.AppManager;
import com.example.administrator.ybdriver.bean.User;
import com.example.administrator.ybdriver.ui.activity.AboutActivity;
import com.example.administrator.ybdriver.ui.activity.FactoryHorizontalChartActivity;
import com.example.administrator.ybdriver.ui.activity.FeedBackActivity;
import com.example.administrator.ybdriver.ui.activity.LoginActivity;
import com.example.administrator.ybdriver.ui.activity.MainActivity;
import com.example.administrator.ybdriver.ui.activity.ManageChartActivity;
import com.example.administrator.ybdriver.ui.activity.ManageHorizontalChartActivity;
import com.example.administrator.ybdriver.ui.activity.ScanCodeActivity;
import com.example.administrator.ybdriver.ui.activity.UpdatePwdActivity;
import com.example.administrator.ybdriver.utils.StringUtils;
import com.google.zxing.WriterException;
import com.google.zxing.encoding.EncodingHandler;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.ui.fragment.BaseLifecyclePrintFragment;
import com.kaidongyuan.app.basemodule.utils.nomalutils.BitmapUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

/**
 * Created by Administrator on 2016/6/23.
 */
public class MineFragment extends BaseLifecyclePrintFragment implements AsyncHttpCallback {

    private   Context mcontext;
    private View parent;
    TextView versionName;
    public boolean isupdate;
    /**
     * 司机登录手机号的二维码
     */
    /** 保存二维码图片的文件夹 */
    private final String mFileName="phone.jpg";
    private final String mCacheFileName = "ybdriver";
    /** 二维码图片的质量 */
    private final int mPictureQuity = 100;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_mine, container, false);
        mcontext=getActivity();
        return parent;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview();
    }

    private void initview() {
        SlidingTitleView titleview= (SlidingTitleView) parent.findViewById(R.id.slidingtilteview_title_view);
        titleview.setMode(SlidingTitleView.MODE_NULL);
        titleview.setText("个人中心");
        if (AppContext.getInstance().getUser() != null) {
            user = AppContext.getInstance().getUser();
            ((TextView) parent.findViewById(R.id.tv_name)).setText(AppContext.getInstance().getUser().getUSER_NAME());
            ((TextView) parent.findViewById(R.id.tv_phone)).setText(StringUtils.getRoleName(AppContext.getInstance().getUser().getUSER_CODE()));
            //显示二维码
            parent.findViewById(R.id.rl_scan_code).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String phone= user.getUSER_CODE();
                        if (phone!=null&&!phone.isEmpty()){
                            Bitmap mBitmap = EncodingHandler.createQRCode(phone+","+System.currentTimeMillis(), DensityUtil.getHeight());
                            String picturePath= BitmapUtil.writeBimapToPngFile(mBitmap,mFileName,mCacheFileName,mPictureQuity);
                            Intent intent=new Intent(getActivity(),ScanCodeActivity.class);
                            intent.putExtra("picturePath",picturePath);
                            startActivity(intent);
                        }else {
                            showToastMsg("此回瓶单异常，未匹配到装运号~");
                        }
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (user.getUSER_TYPE()!=null){
                switch (user.getUSER_TYPE()){
                    case "ADMIN":
                        parent.findViewById(R.id.rl_manage_info).setVisibility(View.VISIBLE);
                        parent.findViewById(R.id.rl_factory_report).setVisibility(View.VISIBLE);
                        break;
                    case  "WLS":
                        parent.findViewById(R.id.rl_manage_info).setVisibility(View.VISIBLE);
                        parent.findViewById(R.id.rl_factory_report).setVisibility(View.GONE);
                        break;
                    case  "司机":
                        parent.findViewById(R.id.rl_manage_info).setVisibility(View.GONE);
                        parent.findViewById(R.id.rl_factory_report).setVisibility(View.GONE);
                        break;
                    default:
                        parent.findViewById(R.id.rl_manage_info).setVisibility(View.GONE);
                        parent.findViewById(R.id.rl_factory_report).setVisibility(View.GONE);
                }

            }
            //物流管理信息
            parent.findViewById(R.id.rl_manage_info).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ManageHorizontalChartActivity.class));
                }
            });
            //工厂管理信息
            parent.findViewById(R.id.rl_factory_report).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), FactoryHorizontalChartActivity.class));
                }
            });

        }
        //保护定位服务设置
        parent.findViewById(R.id.rl_check_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                showToastMsg("请保护本应用后台运行自启动！", 5000);
            }
        });

        //修改密码
        parent.findViewById(R.id.rl_update_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdatePwdActivity.class));
            }
        });


        //反馈信息
        parent.findViewById(R.id.rl_feed_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FeedBackActivity.class);
                startActivity(intent);
            }
        });


        versionName= (TextView) parent.findViewById(R.id.version_name_text);
        versionName.setText("v"+StringUtils.getVersionName(mcontext));

        parent.findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改时间 2016-03-12 用户点击退出按钮后跳转到登陆界面
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                AppContext.IS_LOGIN=false;
                startActivity(intent1);
                AppManager.getAppManager().AppExit(getActivity());
                try{
                    AppManager.getAppManager().finishAllActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        parent.findViewById(R.id.rl_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        if (isupdate){
            final MainActivity activity= (MainActivity) getActivity();
            versionName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.checkVersion();
                }
            });
            versionName.setText("有新版本！");
            versionName.setTextColor(getResources().getColor(R.color.red));
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {

    }
}
