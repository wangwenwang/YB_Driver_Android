package com.example.administrator.ybdriver.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.ScreenManager;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.io.File;

/**
 * ${PEOJECT_NAME}
 * Created by Administrator on 2016/10/19.
 */
public class ScanCodeActivity extends BaseActivity {
    private SlidingTitleView title;
    private ImageView photoView;
    private String  mpicturePath;
    private String  mstrUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode);
        initView();
        initData();
    }

    private void initView() {
        title= (SlidingTitleView) findViewById(R.id.slidingTitelView_photoActivity);
        title.setMode(SlidingTitleView.MODE_BACK);
        title.setText("二\t\t维\t\t码");
        photoView= (ImageView) findViewById(R.id.photoview_photoActivity);
    }
    private void initData() {
       Intent intent=getIntent();
        mpicturePath=intent.getStringExtra("picturePath");
        mstrUrl=intent.getStringExtra("strUrl");
        if (mpicturePath!=null&&mpicturePath.length()>0){
            Uri uri=Uri.fromFile(new File(mpicturePath));
            photoView.setImageURI(uri);
            ScreenManager.setActivityBrightness(1.0f,ScanCodeActivity.this);
            return;
        }else if (mstrUrl!=null&&mstrUrl.length()>0){
            Glide.with(getMContext()).load(mstrUrl).error(R.drawable.ic_no_record1)
                 .override(DensityUtil.dip2px(720f), DensityUtil.dip2px(720f)).diskCacheStrategy(DiskCacheStrategy.NONE)
                 .fitCenter().into(photoView);
            return;
        }else {
            showToastMsg("图片数据丢失，请重新上传");
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }

    }


}
