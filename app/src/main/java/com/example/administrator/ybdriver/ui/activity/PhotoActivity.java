package com.example.administrator.ybdriver.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.kaidongyuan.app.basemodule.utils.nomalutils.BitmapUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.senab.photoview.PhotoView;


/**
 * Created by Administrator on 2016/7/5.
 */
public class PhotoActivity extends BaseActivity {

    private String  mpicturePath;
    private String  mstrUrl;
    private SlidingTitleView slidingTitleView;
    /**　照片文件宽度 单位（px）　*/
    private final int mBitmapWidth = 400;
    private PhotoView matrixphoto;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initview();
        initdata();
    }
    @Override
    public void initWindow() {
        //重写为空，针对满屏页面取消沉浸式状态栏
    }
    private void initdata() {
        Intent intent=getIntent();
        mpicturePath=intent.getStringExtra("picturePath");
        mstrUrl=intent.getStringExtra("strUrl");
        if (mpicturePath!=null&&mpicturePath.length()>0) {
            Uri uri = Uri.fromFile(new File(mpicturePath));
            matrixphoto.setImageURI(uri);
            return;
        }else if (mstrUrl!=null&&mstrUrl.length()>0) {
                Glide.with(getMContext()).load(mstrUrl).error(R.drawable.ic_no_record1)
                        .override(DensityUtil.dip2px(720f),DensityUtil.dip2px(720)).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter().into(matrixphoto);
            return;
        } else {
            showToastMsg("图片数据丢失，请重新上传");
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    private void initview() {
        slidingTitleView = (SlidingTitleView) findViewById(R.id.slidingTitelView_photoActivity);
        slidingTitleView.setText("查看大图");
        slidingTitleView.setMode(SlidingTitleView.MODE_BACK);
        matrixphoto= (PhotoView) findViewById(R.id.iv_martrix_photo);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
