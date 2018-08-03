package com.example.administrator.ybdriver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.app.AppManager;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;


/**
 * Created by Administrator on 2016/6/1.
 */
public class WelcomeActivity extends BaseActivity {
    private ImageView iv_kdy;
    private TextView tv_appname;
    private AnimationSet as;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initView();
        startAnimation();
        // 动画播完进入下一个界面 向导界面和主界面
        initEvent();
    }
    @Override
    public void initWindow() {
        //重写为空，针对满屏页面取消沉浸式状态栏
    }
    private void initEvent() {
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (SharedPreferencesUtils.getValueByName(Constants.BasicInfo, Constants.IsUsedApp, 2)){
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }else {

                    startActivity(new Intent(WelcomeActivity.this,GuideActivity.class));
                }
                AppManager.getAppManager().finishActivity(WelcomeActivity.class);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initView() {
        iv_kdy= (ImageView) findViewById(R.id.welcomeActivty_kdy);
        AppContext.IS_LOGIN=false;
    }

    /**
     * 开始播放动画：旋转，缩放，渐变
     */
    private void startAnimation() {
        // false 代表动画集中每种动画都采用各自的动画插入器(数学函数)
        as = new AnimationSet(false);

        // 旋转动画,锚点
        RotateAnimation ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);// 设置锚点为图片的中心点
        // 设置动画播放时间
        ra.setDuration(2000);
        ra.setFillAfter(true);// 动画播放完之后，停留在当前状态

        // 添加到动画集
        as.addAnimation(ra);

        // 渐变动画
        AlphaAnimation aa = new AlphaAnimation(0, 1);// 由完全透明到不透明
        // 设置动画播放时间
        aa.setDuration(2000);
        aa.setFillAfter(true);// 动画播放完之后，停留在当前状态

        // 添加到动画集
        as.addAnimation(aa);

        // 缩放动画
        ScaleAnimation sa = new ScaleAnimation(0.1f, 0.97f, 0.1f, 0.97f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        // 设置动画播放时间
        sa.setDuration(2000);
        sa.setFillAfter(true);// 动画播放完之后，停留在当前状态

        // 添加到动画集
        as.addAnimation(sa);

        // 播放动画
        iv_kdy.startAnimation(as);

    }
}
