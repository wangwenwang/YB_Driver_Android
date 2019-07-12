package com.example.administrator.ybdriver.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppManager;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.ui.fragment.UnpayedFragment;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.example.administrator.ybdriver.utils.Tools;
import com.example.administrator.ybdriver.utils.baidumapUtils.DataUtil;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.AnimationUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.BitmapUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.FileUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.MPermissionsUtil;
import com.kaidongyuan.app.basemodule.widget.AutographView;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/20.
 */
public class OrderPayActivity extends BaseActivity implements AsyncHttpCallback,View.OnClickListener {
    private SlidingTitleView mslidingTitleView;
    private String orderIDX;
    private String orderDriverPay;//运输订单状态 “已到达”，“已交付”，“未交付”
    private double latitude,longitude;//订单详情在未交付订单下获取的百度经纬度
    private String currentAddress;//订单详情在未交付订单下获取的百度地址
    private final String Tag_Pay = "Tag_Pay";//交付订单Tag
    private final String Tag_isPayed="Tag_IsPayedOrder";//到达时上传到达点信息的Tag
    private ImageView imageView1,imageView2;
    private Button button1,button2,cancelbutton,submitbutton,albumbutton,camerabutton;
  //  private AutographView mautographView;
    private AlertDialog mchoicealertDialog;
    private OrderAsyncHttpClient mClient;
    /** 保存图片和签名的文件夹 */
    private final String mCacheFileName = "HuaRunYiBao";
    /** 签名保存的文件名 */
  //  private final String autographFileName="autograph.jpg";
    /**　签名保存的绝对路径　*/
 //   private  String autographFilePath;
    /** 照片1保存的文件名 */
    private final String mPictureFileName = "orderImage.jpg";
    /**　照片1调用系统相机是的请求码　*/
    private final int SystemCapture = 10;
    /**　照片保存的绝对路径　*/
    private String mPictureFilePath;
    /** 照片2保存的文件名 */
    private final String mPictureFileName2 = "orderImage2.jpg";
    /**照片2调用系统相机是的请求码2　*/
    private final int SystemCapture2 = 11;
    /**　照片2保存的绝对路径　*/
    private String mPictureFilePath2;
    /** 储存添加照片是的临时文件名 */
    private String mTempPictureFileName;
    /** 储存添加照片时的临时请求码 */
    private int mTempRequestCode;
    /** 储存添加照片时的临时文件路径 */
    private String mTempPictureFilePath;
    /** 储存添加照片时的临时Bitmap */
    private Bitmap mTempbitmap;
    /**　签名和照片文件宽度 单位（px）　*/
    private final int mBitmapWidth = 400;
    /** 签名和照片的质量 */
    private final int mPictureQuity = 80;
    private final int REQUESTCAMERA_STATUS_CODE0=8880;//android6.0 查询调用摄像头授权标识码
    private final int REQUESTCAMERA_STATUS_CODE1=8881;
    private static boolean IsCamera=false;
    private String ORD_TO_ADDRESS;//目的地址
    private String ORD_TO_lng;    //目的地址经度
    private String ORD_TO_lat;    //目的地址纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderpay);
        initview();
//        if (Build.VERSION.SDK_INT>=23) {
//            MPermissionsUtil.checkAndRequestPermissions(OrderPayActivity.this, new String[]{Manifest.permission.CAMERA},REQUESTCAMERA_STATUS_CODE0);
//        }
        if (Build.VERSION.SDK_INT>=23) {
            MPermissionsUtil.checkAndRequestPermissions(OrderPayActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCAMERA_STATUS_CODE1);
        }

        new Thread() {

            public void run() {

                geocoderToLocation(ORD_TO_ADDRESS);
            }
        }.start();
    }

    public void geocoderToLocation(String address) {

        String a = "http://api.map.baidu.com/geocoder/v2/?address=";
        String b = "&output=json&ak=dUz4AKCXgwSrbGOYRNTyy8Mya0tg6b1c&mcode=com.kaidongyuan.Geocoder";
        String urlStr = a + address + b;
        Log.d("LM", urlStr);

        HttpURLConnection conn = null;
        try {

            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {

                Log.d("LM", "地址转坐标成功");
                InputStream in = conn.getInputStream();
                String resultStr = Tools.inputStream2String(in);
                resultStr = URLDecoder.decode(resultStr, "UTF-8");

                try {
                    JSONObject jsonObj = (JSONObject) (new JSONParser().parse(resultStr));
                    Log.i("LM", jsonObj.toJSONString() + "\n" + jsonObj.getClass());
                    String status = (String) jsonObj.get("status").toString();

                    Log.d("LM", "fd:" + jsonObj.get("result") + jsonObj.get("result").getClass());
                    if (jsonObj.get("result").getClass().getName().equals(org.json.simple.JSONObject.class.getName())) {

                        org.json.simple.JSONObject dict = (org.json.simple.JSONObject) jsonObj.get("result");
                        long comprehension = (long) dict.get("comprehension");
                        long confidence = (long) dict.get("confidence");
                        String level = (String) dict.get("level").toString();
                        long precise = (long) dict.get("precise");
                        org.json.simple.JSONObject dicLocation = (org.json.simple.JSONObject) dict.get("location");

                        String lat = (String) dicLocation.get("lat").toString();
                        String lng = (String) dicLocation.get("lng").toString();
                        ORD_TO_lng = lng;
                        ORD_TO_lat = lat;

                        Log.d("LM", address);
                        Log.d("LM", ORD_TO_lng + "    " + ORD_TO_lat);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                in.close();
            } else {
                Log.i("PostGetUtil", "get请求失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    private void initview() {
        mslidingTitleView = (SlidingTitleView) findViewById(R.id.orderpay_titelview);
        mslidingTitleView.setText("拍照上传照片");
        mslidingTitleView.setMode(SlidingTitleView.MODE_BACK);
        Intent intent=getIntent();
        orderIDX=intent.getStringExtra("order_id");
        orderDriverPay=intent.getStringExtra("order_driver_pay");
        ORD_TO_ADDRESS=intent.getStringExtra("ORD_TO_ADDRESS");
        mClient=new OrderAsyncHttpClient(this,this);
        if (orderIDX==null||orderIDX.length()<=0||orderDriverPay==null||orderDriverPay.length()<=0){
            showToastMsg("订单信息丢失，请重新加载");
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
        imageView1= (ImageView) findViewById(R.id.imageView_picture);
        imageView2= (ImageView) findViewById(R.id.imageView_picture2);
        button1= (Button) findViewById(R.id.button_addPicture);
        button2= (Button) findViewById(R.id.button_addPicture2);
        imageView1.setOnClickListener(this);
        button1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        button2.setOnClickListener(this);
//        mautographView= (AutographView) findViewById(R.id.autographView_write);
//        againAutograph= (Button) findViewById(R.id.button_againAutograph);
//      againAutograph.setOnClickListener(this);
        cancelbutton= (Button) findViewById(R.id.button_cancel);
        cancelbutton.setOnClickListener(this);
        submitbutton= (Button) findViewById(R.id.button_submit);
        submitbutton.setOnClickListener(this);
        if (orderDriverPay.equals("N")){
            button1.setText("库门面照");
            button2.setText("车头牌照");
            submitbutton.setText("到达");
            if (intent.hasExtra("latitude")&&intent.hasExtra("longitude")){
                latitude=intent.getDoubleExtra("latitude",0);
                longitude=intent.getDoubleExtra("longitude",0);
                Map<String, String> params = new HashMap<>();
                String mUserId = SharedPreferencesUtils.getUserId();
                params.put("strUserIdx", mUserId);
                params.put("cordinateX", longitude + "");
                params.put("cordinateY", latitude + "");
                params.put("address", "BD到达点");
                params.put("strLicense", "");
                params.put("date", DataUtil.getStringTime(System.currentTimeMillis()) + "");
                //2016.3.25
                MLog.i("params:"+params.toString());
                mClient.sendRequest(Constants.URL.CurrentLocaltion,params,Tag_isPayed,false);
            }
        }else {
            button1.setText("卸货车照");
            button2.setText("签单回执");
            submitbutton.setText("交付");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mClient.cancleRequest(Tag_Pay);
        super.onDestroy();

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")&request_tag.equals(Tag_Pay)){
            MLog.w("postSuccessMsg error");
//            finish();
        }else if (request_tag.equals(Tag_Pay)){
            showToastMsg("提交成功");
//            if (orderDriverPay.equals("S")) {
//                Map<String, String> params = new HashMap<>();
//                params.put("strOrderIdx", orderIDX);
//                params.put("strLicense", "");
//                mClient.sendRequest(Constants.URL.IsPayedOrder, params, Tag_isPayed);
//            }
                UnpayedFragment.isrefresh = true;
            AppManager.getAppManager().finishActivity(OrderDetailActivity.class);
                finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_picture:
                if (mPictureFilePath==null){
                //showAlertDialog(mPictureFileName,SystemCapture);
                 addPictureFromCamera(mPictureFileName,SystemCapture);
            }else {
                Intent intent=new Intent(this,PhotoActivity.class);
                intent.putExtra("picturePath",mPictureFilePath);
                startActivity(intent);
            }
                break;
            case R.id.button_addPicture:
                 //showAlertDialog(mPictureFileName,SystemCapture);
                addPictureFromCamera(mPictureFileName,SystemCapture);
                break;
            case R.id.imageView_picture2:
                if (mPictureFilePath2==null){
                   // showAlertDialog(mPictureFileName2,SystemCapture2);
                addPictureFromCamera(mPictureFileName2,SystemCapture2);
                }else {
                    Intent intent2=new Intent(this,PhotoActivity.class);
                    intent2.putExtra("picturePath",mPictureFilePath2);
                    startActivity(intent2);
                }
                break;
            case R.id.button_addPicture2:
             //   showAlertDialog(mPictureFileName2,SystemCapture2);
                addPictureFromCamera(mPictureFileName2,SystemCapture2);
                break;
//            case R.id.button_choicepicture_fromalbum:
//                addPictureFromAlbum();//从相册上传照片
//                break;
//            case R.id.button_choicepicture_fromcamera:
//                addPictureFromCamera();//用摄像头拍摄照片上传
//                break;
//            case R.id.button_againAutograph:
//                mautographView.clear();
//                break;
            case R.id.button_submit:
//                if (saveAutograph()) {
//                    submitOrder();
//                }
                submitOrder();
                break;
            case R.id.button_cancel:
                finish();
                break;
        }

    }

    private void submitOrder() {
        if (mPictureFilePath!=null&&mPictureFilePath.length()>0&&mPictureFilePath2!=null&&mPictureFilePath2.length()>0) {
            Bitmap pictureBitmap1 = BitmapUtil.resizeImage(mPictureFilePath, mBitmapWidth);
            Bitmap pictureBitmap2 = BitmapUtil.resizeImage(mPictureFilePath2, 2*mBitmapWidth);
            if (pictureBitmap1!=null&&pictureBitmap2!=null){
                String strpicture1=BitmapUtil.changeBitmapToString(pictureBitmap1);
                String strpicture2=BitmapUtil.changeBitmapToString(pictureBitmap2);
                SharedPreferences readLatLng = getSharedPreferences("w_UserInfo", MODE_MULTI_PROCESS);
                String currtLongitude = readLatLng.getString("currtLongitude", "");
                String currLatitude = readLatLng.getString("currLatitude", "");


                currLatitude = "22.71011111111111";
                currtLongitude = "113.8584311111111";


                Map<String, String> params = new HashMap<>();
                params.put("strOrderIdx", orderIDX);
                params.put("strLicense", "");
                params.put("PictureFile1", strpicture1);
                params.put("PictureFile2", strpicture2);
                params.put("toLng", ORD_TO_lng);
                params.put("toLat", ORD_TO_lat);
                params.put("currentLng", currtLongitude);
                params.put("currentLat", currLatitude);

                Log.d("LM", "strOrderIdx: " + ORD_TO_lng);
                Log.d("LM", "ORD_TO_lng: " + ORD_TO_lng);
                Log.d("LM", "ORD_TO_lat: " + ORD_TO_lat);
                Log.d("LM", "currtLongitude: " + currtLongitude);
                Log.d("LM", "currLatitude: " + currLatitude);
                if (orderDriverPay.equals("N")){
                    params.put("AutographFile", "S");
                    Log.d("LM", "AutographFile: " + "S");
                }else {
                    params.put("AutographFile", "Y");
                    Log.d("LM", "AutographFile: " + "Y");
                }

                mClient.sendRequest(Constants.URL.DriverPay,params,Tag_Pay,true);
            }else {
                showToastMsg("请重新采集拍照");
                return;
            }
        }else {
            showToastMsg("请完成照片采集后提交订单");
        }
    }

//    private boolean saveAutograph() {
 //       Bitmap bitmap=mautographView.getPaintBitmap();
//        if (bitmap!=null){
//            bitmap = BitmapUtil.resizeImage(bitmap, mBitmapWidth);
//            autographFilePath=BitmapUtil.writeBimapToFile(bitmap,autographFileName,mCacheFileName,mPictureQuity);
//            if (autographFilePath == null || autographFilePath.length() <= 0) {
//                Toast.makeText(this, "签名失败,请重签", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            return true;
//        }else {
//            showToastMsg("请签名");
//
//        }
//
//        return false;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUESTCAMERA_STATUS_CODE0:

                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    //已获取权限,打开调用摄像头开关IsCamera
                    IsCamera=true;
                }else{
                    showToastMsg("请授权应用调用摄像头权限~",5000);
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
                break;
            case REQUESTCAMERA_STATUS_CODE1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //已获取权限
                }else{
                    showToastMsg("请允许应用使用SD卡存储",5000);

//                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                    startActivity(intent);
                    finish();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK){
            switch (requestCode){
              //imageView1的拍照读取模式
                case SystemCapture:
                    mTempbitmap = BitmapUtil.resizeImage(mTempPictureFilePath, mBitmapWidth);
                    getPictureResultHandle(imageView1, button1, mTempbitmap, mTempPictureFileName);
                    break;
              //imageView1的相册读取模式
                case SystemCapture*2:
                    Uri uri=data.getData();
                    mTempbitmap=BitmapUtil.getBitmap(getMContext(),uri,mBitmapWidth);
                    getPictureResultHandle(imageView1,button1,mTempbitmap,mTempPictureFileName);
                case SystemCapture2:
                    mTempbitmap = BitmapUtil.resizeImage(mTempPictureFilePath, 2*mBitmapWidth);
                    getPictureResultHandle(imageView2, button2, mTempbitmap, mTempPictureFileName);
                    break;
                case SystemCapture2*2:
                    Uri uri2=data.getData();
                    mTempbitmap=BitmapUtil.getBitmap(getMContext(),uri2,2*mBitmapWidth);
                    getPictureResultHandle(imageView2,button2,mTempbitmap,mTempPictureFileName);
            }
        }
    }
    /**
     * 从相册获取照片上传
     */
//    private void addPictureFromAlbum(){
//        if (mchoicealertDialog!=null) {
//            mchoicealertDialog.dismiss();
//        }
//        mTempRequestCode *= 2;
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, mTempRequestCode);
//    }
    /**
     * 使用相机拍摄照片上传
     */
    private void addPictureFromCamera(String picturefilename,int picturerequestcode ) {
      //  PackageManager pm=getPackageManager();
       // boolean camerapermission=(PackageManager.PERMISSION_GRANTED==pm.checkPermission("android.permission.CAMERA","com.example.administrator.ybdriver") );
//     if (!camerapermission){
//         showToastMsg("请先授权应用调用摄像头权限~",5000);
//         Intent intent = new Intent(Settings.ACTION_SETTINGS);
//         startActivity(intent);
//          return;
//       }

        if (Build.VERSION.SDK_INT>=23&&!IsCamera) {
            boolean tempIsCamera=MPermissionsUtil.checkAndRequestPermissions(OrderPayActivity.this, new String[]{Manifest.permission.CAMERA},REQUESTCAMERA_STATUS_CODE0);
            if (!tempIsCamera) {
                return;
            }else {
                IsCamera=tempIsCamera;
            }
        }

        mTempPictureFileName=picturefilename;
        mTempRequestCode=picturerequestcode;
        File dirfile= FileUtil.getCacheDirFile(mCacheFileName);
        File picturefile=new File(dirfile,mTempPictureFileName);
        mTempPictureFilePath=picturefile.getAbsolutePath();
        Uri pictureuri=Uri.fromFile(picturefile);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,pictureuri);
        startActivityForResult(intent, mTempRequestCode);
    }

//    private void getAppDetailSettingIntent(Context context) {
//        Intent localIntent = new Intent();
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 9) {
//            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            localIntent.setAction(Intent.ACTION_VIEW);
//            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
//            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
//        }
//        startActivity(localIntent);
//    }

    /**
     * 使用相机拍摄照片后的处理
     * @param imageView 要添加照片的控件
     * @param button 对应的按钮
     * @param bitmap 照片对象
     * @param fileName 保存文件名
     */
    private void getPictureResultHandle(ImageView imageView, Button button, Bitmap bitmap, String fileName){
        if (bitmap != null) {
//            if (mTempRequestCode==SystemCapture || mTempRequestCode==SystemCapture2) {
//                bitmap = BitmapUtil.rotateBitmapAngle(bitmap, 0F);
//            }
            imageView.setImageBitmap(bitmap);
            imageView.startAnimation(AnimationUtil.createScaleAnimation(0f,1f,200,true));
           // AnimationUtil.showWithScaleAnimation(imageView, mAnimationDuration * 3 / 2, 0);
            button.setText("重新上传");
            String picturePath = BitmapUtil.writeBimapToFile(bitmap, fileName, mCacheFileName, mPictureQuity);
            if (mTempRequestCode==SystemCapture ||mTempRequestCode==SystemCapture*2) {
                mPictureFilePath = picturePath;
            } else if (mTempRequestCode==SystemCapture2||mTempRequestCode==SystemCapture2*2 ) {
                mPictureFilePath2 = picturePath;
            }
        }
    }
//    private void showAlertDialog(String picturefilename,int picturerequestcode) {
//            if (mchoicealertDialog==null){
//                AlertDialog.Builder builder=new AlertDialog.Builder(OrderPayActivity.this);
//                mchoicealertDialog=builder.create();
//                mchoicealertDialog.show();
//                Window window=mchoicealertDialog.getWindow();//必须先show然后才能获取窗体进行编辑
//                window.setContentView(R.layout.dialog_show_updatachoice);
//                albumbutton= (Button) window.findViewById(R.id.button_choicepicture_fromalbum);
//                camerabutton= (Button) window.findViewById(R.id.button_choicepicture_fromcamera);
//                albumbutton.setOnClickListener(this);
//                camerabutton.setOnClickListener(this);
//
//            }else {
//                mchoicealertDialog.show();
//            }
//            mTempPictureFileName=picturefilename;
//            mTempRequestCode=picturerequestcode;
//    }

}
