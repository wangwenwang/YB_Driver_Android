package com.example.administrator.ybdriver.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bumptech.glide.Glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.adapter.OrderDetailsSimpleAdapter;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.bean.CustomerAutographAndPicture;
import com.example.administrator.ybdriver.bean.Order;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseActivity;

import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.example.administrator.ybdriver.utils.baidumapUtils.LocationPointUtil;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;

import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils;
import com.kaidongyuan.app.basemodule.utils.nomalutils.SystemUtil;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.getbase.floatingactionbutton.FloatingActionButton;
/**
 * Created by Administrator on 2016/6/20.
 */
public class OrderDetailActivity extends BaseActivity implements AsyncHttpCallback, View.OnClickListener {
    private   String orderIDX="";
    private OrderAsyncHttpClient mClient;
    private final String Get_Tms_Info = "Get_Tms_Info";
    private final String Tag_GetAutograph="GetAutograph";
    private  String strOrderId;
    private Order mOrder;
    private boolean isrefresh=false;
    private SlidingTitleView slidingTitleView;
    private ListView lv_details;
    private OrderDetailsSimpleAdapter madapter;
    private TextView tv_tms_order_no, tv_tms_shipment_no, tv_tms_date_load, tv_tms_date_issue, tv_tms_fleet_name, tv_tms_plate_number,
            tv_tms_driver_name, tv_tms_driver_tel, tv_ord_issue_qty, tv_ord_issue_weight, tv_ord_issue_volume,
            tv_ord_state, tv_ord_workflow, tv_driver_pay,tv_state,tv_city,tv_county ,tv_order_to_name, tv_order_from_name, tv_order_to_addres;
    private FloatingActionButton fab_payorder,fab_ordertrack;
    private LinearLayout ll_order_pictures;//查看已交付订单的图片LinearLayout
    private ImageView iv_orderpicture,iv_orderpicture2,iv_orderpicture3,iv_orderpicture4;
    /**
     * 订单电子回单图片集合
     */
    private List<CustomerAutographAndPicture> customerAutographAndPictures;
    private Intent intent;
    private GeoCoder msearch;
    private OnGetGeoCoderResultListener geoCoderlistener;
    private GeoCodeResult order_ToAdsGeoCodeResult;
    private BDLocation currentlocation;
    public LocationClient mLocationClient;// 百度定位客户端
    public MyLocationListener myListener;// 百度定位监听
    private String tempcoor = "bd09ll";// 百度地图的编码模式
    //高精度模式
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private boolean againBoolean=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);
        mClient=new OrderAsyncHttpClient(this,this);
        intent = getIntent();
        mClient.setShowToast(false);
        initView();
        getData();
    }

    private void initView() {
        slidingTitleView = (SlidingTitleView) findViewById(R.id.unpayed_orderdetail_titel_view);
        slidingTitleView.setText("物流详情");
        slidingTitleView.setMode(SlidingTitleView.MODE_BACK);
        tv_tms_order_no = (TextView) findViewById(R.id.tv_tms_order_no);
        tv_tms_shipment_no = (TextView) findViewById(R.id.tv_tms_shipment_no);
        tv_tms_date_load = (TextView) findViewById(R.id.tv_tms_date_load);
        tv_tms_date_issue = (TextView) findViewById(R.id.tv_tms_date_issue);
        tv_tms_fleet_name = (TextView) findViewById(R.id.tv_tms_fleet_name);
        tv_tms_plate_number = (TextView) findViewById(R.id.tv_tms_plate_number);
        tv_tms_driver_name = (TextView) findViewById(R.id.tv_tms_driver_name);
        tv_tms_driver_tel = (TextView) findViewById(R.id.tv_tms_driver_tel);
        tv_ord_issue_qty = (TextView) findViewById(R.id.tv_ord_issue_qty);
        tv_ord_issue_weight = (TextView) findViewById(R.id.tv_ord_issue_weight);
        tv_ord_issue_volume = (TextView) findViewById(R.id.tv_ord_issue_volume);
        tv_ord_state = (TextView) findViewById(R.id.tv_ord_state);
        tv_ord_workflow = (TextView) findViewById(R.id.tv_ord_workflow);
        tv_driver_pay = (TextView) findViewById(R.id.tv_driver_pay);
        tv_state= (TextView) findViewById(R.id.tv_state);
        tv_city= (TextView) findViewById(R.id.tv_city);
        tv_county= (TextView) findViewById(R.id.tv_county);
        tv_order_to_name = (TextView) findViewById(R.id.tv_order_to_name);
        tv_order_from_name = (TextView) findViewById(R.id.tv_order_from_name);
        tv_order_to_addres = (TextView) findViewById(R.id.tv_order_to_address);
        lv_details= (ListView) findViewById(R.id.lv_details);
        madapter=new OrderDetailsSimpleAdapter(OrderDetailActivity.this);
        lv_details.setAdapter(madapter);
        fab_payorder= (FloatingActionButton) findViewById(R.id.fab_payorder);
        fab_ordertrack= (FloatingActionButton) findViewById(R.id.fab_orderTrack);
        ll_order_pictures= (LinearLayout) findViewById(R.id.ll_order_pictures);
        iv_orderpicture= (ImageView) findViewById(R.id.iv_order_picture);
        iv_orderpicture2= (ImageView) findViewById(R.id.iv_order_picture2);
       // iv_orderAutograph= (ImageView) findViewById(R.id.iv_order_autograph);
        iv_orderpicture3= (ImageView) findViewById(R.id.iv_order_picture3);
        iv_orderpicture4= (ImageView) findViewById(R.id.iv_order_picture4);
    }


    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        if (intent.hasExtra("order_id")) {
            strOrderId = intent.getStringExtra("order_id");
            fab_ordertrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2=new Intent(OrderDetailActivity.this,OrderTrackActivity.class);
                    intent2.putExtra("order_IDX",strOrderId);
                    startActivity(intent2);
                }
            });
        } else {
            showToastMsg("货单ID不能为空");
            finish();
            return;
        }

        params.put("strTmsOrderId", strOrderId);
        params.put("strLicense", "");
        mClient.sendRequest(Constants.URL.GetOrderTmsInfo, params, Get_Tms_Info);
    }

    private void setData(Order order) {
        tv_tms_order_no.setText(order.getORD_NO());
        tv_tms_shipment_no.setText(order.getTMS_SHIPMENT_NO());
        tv_tms_date_load.setText(order.getTMS_DATE_LOAD());
        tv_tms_date_issue.setText(order.getTMS_DATE_ISSUE());
        tv_tms_fleet_name.setText(order.getTMS_FLLET_NAME());
        tv_tms_plate_number.setText(order.getTMS_PLATE_NUMBER());
        tv_tms_driver_name.setText(order.getTMS_DRIVER_NAME());
        tv_tms_driver_tel.setText(order.getTMS_DRIVER_TEL());
        tv_ord_issue_qty.setText(order.getORD_QTY()+ "件");
        tv_ord_issue_weight.setText(order.getORD_WEIGHT() + "吨");
        tv_ord_issue_volume.setText(order.getORD_ISSUE_VOLUME() + "m³");
        tv_ord_state.setText(order.getTMS_TYPE_TRANSPORT());//yb 发运方式
        tv_state.setText(StringUtils.strNoDataSet(order.getSTATE()) );
        tv_city.setText(StringUtils.strNoDataSet(order.getCITY()));
        tv_county.setText(StringUtils.strNoDataSet(order.getCOUNTY()));
      //  tv_ord_workflow.setText(StringUtils.getOrderState(order.getORD_WORKFLOW()));
        tv_order_to_name.setText(order.getORD_TO_NAME());
        tv_order_from_name.setText(order.getORD_FROM_NAME());
        tv_order_to_addres.setText(order.getORD_TO_ADDRESS());
        madapter.resetData(order.getOrderDetails());
        mLocationClient=new LocationClient(this);
        myListener=new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        initLocationClient();
        if (mOrder.getDRIVER_PAY().equals("N")) {
            tv_driver_pay.setText("未交付");
            fab_ordertrack.setColorNormalResId(R.color.blue);
            fab_ordertrack.setIcon(R.drawable.fab_ordernavi);
            fab_ordertrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (currentlocation!=null&&currentlocation.getAddress().city!=null&&!currentlocation.getAddress().city.isEmpty()){
                            msearch.geocode(new GeoCodeOption().city(currentlocation.getAddress().city).address(StringUtils.subAllTargetCharSequence(mOrder.getORD_TO_ADDRESS(),"*")));
                        }else {
                            msearch.geocode(new GeoCodeOption().city("深圳市").address(StringUtils.subAllTargetCharSequence(mOrder.getORD_TO_ADDRESS(),"*")));
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });
            startLocate();
            msearch = GeoCoder.newInstance();
            geoCoderlistener = new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                    if (currentlocation==null){
                        startLocate();
                        showToastMsg("网络定位失败，请稍后重试~");
                        return;
                    }
                    if (geoCodeResult==null||geoCodeResult.error!= SearchResult.ERRORNO.NO_ERROR){
                        //没有检索到结果
                        showToastMsg("检索目的地坐标失败，请手动点选目的地");
                        Intent intent = new Intent(OrderDetailActivity.this, OrderTrackCheckActivity.class);
                        intent.putExtra("ORD_TO_ADDRESS", StringUtils.subAllTargetCharSequence(mOrder.getORD_TO_ADDRESS(),"*"));
                        intent.putExtra("currentLocation_longitude", currentlocation.getLongitude());
                        intent.putExtra("currentLocation_latitude", currentlocation.getLatitude());
                        startActivity(intent);
                    }else {
                        order_ToAdsGeoCodeResult=geoCodeResult;
                        MLog.e("目的地：latitude"+geoCodeResult.getLocation().latitude+"\tlongitude"+geoCodeResult.getLocation().longitude);
                        if (SystemUtil.isInstalled(OrderDetailActivity.this,"com.autonavi.minimap")){
                            //跳转到高德导航
                            if (currentlocation!=null&&order_ToAdsGeoCodeResult!=null){
                                HashMap<String,Double>stlatlonmap= LocationPointUtil.bd_decrypt(currentlocation.getLatitude(),currentlocation.getLongitude());
                                HashMap<String,Double>edlatlonmap=LocationPointUtil.bd_decrypt(order_ToAdsGeoCodeResult.getLocation().latitude,order_ToAdsGeoCodeResult.getLocation().longitude);
                                Intent autoIntent=new Intent();
                                autoIntent.setData(Uri
                                        .parse("androidamap://route?" +
                                                "sourceApplication="+getResources().getString(R.string.app_name)+
                                                "&slat=" + stlatlonmap.get("gcjlat") +
                                                "&slon=" +stlatlonmap.get("gcjlon") +
                                                "&dlat=" + edlatlonmap.get("gcjlat") +
                                                "&dlon=" + edlatlonmap.get("gcjlon") +
                                                "&dname=" + order_ToAdsGeoCodeResult.getAddress()+
                                                "&dev=0" +
                                                "&m=0" +
                                                "&t=2"));
                                startActivity(autoIntent);
                            }else {
                                showToastMsg("起始点、目的地坐标不完整，请补全后重新导航");
                            }
                            return;
                        }else  if (SystemUtil.isInstalled(OrderDetailActivity.this,"com.baidu.BaiduMap")){
                            if (currentlocation!=null&&order_ToAdsGeoCodeResult!=null){
                                //跳转到百度导航
                                try {
                                    Intent  baiduintent = Intent.parseUri("intent://map/direction?" +
                                            "origin=latlng:" + currentlocation.getLatitude() + "," + currentlocation.getLongitude() +
                                            "|name:" +currentlocation.getAddrStr() +
                                            "&destination=latlng:" + order_ToAdsGeoCodeResult.getLocation().latitude + "," + order_ToAdsGeoCodeResult.getLocation().longitude+
                                            "|name:" +order_ToAdsGeoCodeResult.getAddress() +
                                            "&mode=driving" +
                                            "&src=Name|AppName" +
                                            "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
                                    startActivity(baiduintent);
                                } catch (URISyntaxException e) {
                                    MLog.d("URISyntaxException : " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }else {
                                showToastMsg("起始点或目的地坐标不完整，请补全后重新导航");
                            }
                            return;
                        } else {
                            showToastMsg("未检索到本机已安装‘百度地图’或‘高德地图’App");
                            return;
                        }
                    }
                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                }
            };
            msearch.setOnGetGeoCodeResultListener(geoCoderlistener);
            fab_payorder.setColorNormalResId(R.color.really_yellow);
            fab_payorder.setIcon(R.drawable.fab_orderarrive);
            fab_payorder.setVisibility(View.VISIBLE);
            fab_payorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOrder != null && mOrder.getIDX() != null&&AppContext.getInstance().getUser().getUSER_TYPE()!=null&&AppContext.getInstance().getUser().getUSER_TYPE().equals("司机")) {
                    // 测试订单交付功能 20170908 陈翔
                    //if (true){
                        Intent intent = new Intent(OrderDetailActivity.this, OrderPayActivity.class);
                        intent.putExtra("order_id", mOrder.getIDX());
                        intent.putExtra("order_driver_pay",mOrder.getDRIVER_PAY());
                        intent.putExtra("ORD_TO_ADDRESS", mOrder.getORD_TO_ADDRESS());
                        if (currentlocation!=null){
                            intent.putExtra("latitude",currentlocation.getLatitude());
                            intent.putExtra("longitude",currentlocation.getLongitude());
                        }
                        isrefresh=true;
                        startActivity(intent);
                    } else if (mOrder != null && mOrder.getIDX() != null){
                       showToastMsg("仅司机本人可提交订单!");
                    }else {
                        showToastMsg("订单信息重载");
                        getData();
                    }
                }
            });
        }else if (mOrder.getDRIVER_PAY().equals("S")){
            tv_driver_pay.setText("已到达");
            fab_ordertrack.setColorNormalResId(R.color.yb_light_green);
            fab_ordertrack.setIcon(R.drawable.fab_ordertrack);
            fab_ordertrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2=new Intent(OrderDetailActivity.this,OrderTrackActivity.class);
                    intent2.putExtra("order_IDX",strOrderId);
                    startActivity(intent2);
                }
            });
            fab_payorder.setColorNormalResId(R.color.yb_yellow);
            fab_payorder.setIcon(R.drawable.fab_payorder);
            fab_payorder.setVisibility(View.VISIBLE);
            //   fab_payorder.setImageResource(R.drawable.fab_payorder);

            fab_payorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOrder != null && mOrder.getIDX() != null&&AppContext.getInstance().getUser().getUSER_TYPE()!=null&&AppContext.getInstance().getUser().getUSER_TYPE().equals("司机")) {
                   // 测试订单交付功能 20170908 陈翔
                   // if (true){
                        Intent intent = new Intent(OrderDetailActivity.this, OrderPayActivity.class);
                        intent.putExtra("order_id", mOrder.getIDX());
                        intent.putExtra("order_driver_pay",mOrder.getDRIVER_PAY());
                        intent.putExtra("ORD_TO_ADDRESS", mOrder.getORD_TO_ADDRESS());
                        startActivity(intent);
                    }  else if (mOrder != null && mOrder.getIDX() != null){
                        showToastMsg("仅司机本人可提交订单!");
                    }else {
                        showToastMsg("订单信息重载");
                        getData();
                    }
                }
            });
        }else if (mOrder.getDRIVER_PAY().equals("Y")){
            fab_ordertrack.setColorNormalResId(R.color.yb_light_green);
            fab_ordertrack.setIcon(R.drawable.fab_ordertrack);
            fab_ordertrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2=new Intent(OrderDetailActivity.this,OrderTrackActivity.class);
                    intent2.putExtra("order_IDX",strOrderId);
                    startActivity(intent2);
                }
            });
            fab_payorder.setVisibility(View.VISIBLE);
            fab_payorder.setIcon(R.drawable.fab_checkpicture);
            //   fab_payorder.setImageResource(R.drawable.fab_checkpicture);
            fab_payorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOrder!=null&&mOrder.getIDX()!=null){
                        Map<String, String> params = new HashMap<>();
                        params.put("strOrderIdx", mOrder.getIDX());
                        params.put("strLicense", "");
                        mClient.sendRequest(Constants.URL.GETAUTOGRAPH,params,Tag_GetAutograph);
                    }else {
                        showToastMsg("订单信息重载");
                        getData();
                    }
                }
            });
           tv_driver_pay.setText("已交付");
        }else {
            tv_driver_pay.setText((order.getDRIVER_PAY()));
        }

        MLog.i(order.toString());
    }

    public void startLocate() {

        if (mLocationClient!=null){
            mLocationClient.start();
        }else {
            mLocationClient=new LocationClient(this);
            myListener=new MyLocationListener();
            mLocationClient.registerLocationListener(myListener);
            initLocationClient();
            mLocationClient.start();
        }
    }
    /**
     * 初始化定位客户端
     */
    public void initLocationClient() {
        LocationClientOption option = new LocationClientOption();
        option.setProdName(this.getPackageName());
        MLog.w("ProdName:" + this.getPackageName());
        option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
        option.setLocationMode(tempMode);// 设置定位模式
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIgnoreKillProcess(false);
        option.setTimeOut(10 * 1000); // 网络定位的超时时间
        mLocationClient.setLocOption(option);
    }

    /**
     * 判断是否会定位失败 ，errorCode是百度定位返回的错误代码
     *
     * @param errorCode
     * @return
     */
    public boolean isLocateAvailable(int errorCode) {
        return (errorCode == 161 ||errorCode == 61||errorCode==66||errorCode==65||errorCode==68);
        //2016.08.30 陈翔 注销了65 ： 定位缓存的结果；68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
        //return (errorCode == 161 ||errorCode == 61||errorCode==66);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            MLog.i("MyLocationListener:\t"+location.getLocType());
            if (location == null) {
                //定位返回空值时，重新定位
                if (againBoolean){
                    try {
                        Thread.sleep(3*1000);
                        againBoolean=false;
                        int r=mLocationClient.requestLocation();
                        MLog.w("定位返回空值时，重新定位:" + r);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }

            // 判断定位是否失败应该依据error code的值更加可靠, 上传坐标信息
            if (!isLocateAvailable(location.getLocType())) {
                //定位返回错误码时，重新定位
                MLog.w("定位返回错误码"+againBoolean);
                if (againBoolean){
                    try {
                        Thread.sleep(3*1000);
                        againBoolean=false;
                        int j=mLocationClient.requestLocation();
                        MLog.w("定位返回错误码时，重新定位:" + j);
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                showToast(location.getLocType()+"网络状态不良，请重试", Toast.LENGTH_SHORT);
                return;
            }
            currentlocation=location;
            mLocationClient.stop();
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isrefresh){
            getData();
            isrefresh=false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        mClient.cancleRequest(Tag_GetAutograph,Get_Tms_Info);
        if (msearch!=null) {
            msearch.destroy();
        }
        if (mLocationClient!=null){
            mLocationClient.stop();
        }
        super.onDestroy();
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")) return;
        if (request_tag.equals(Get_Tms_Info)) {
            try {
                JSONObject jo = JSON.parseObject(msg);
                JSONArray ja = JSON.parseArray(jo.getString("result"));
                jo = JSON.parseObject(ja.get(0).toString());
                mOrder = JSON.parseObject(jo.getString("order"), Order.class);
                if (mOrder != null) {
                    setData(mOrder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (request_tag.equals(Tag_GetAutograph)){
            JSONObject jo=JSON.parseObject(msg);
            int type=jo.getInteger("type");
            if (type==1){
            customerAutographAndPictures=JSON.parseArray(jo.getString("result"),CustomerAutographAndPicture.class);
            setll_order_pictures();
                return;
            }
        }
    }

    private void setll_order_pictures() {
//        Glide.with(mcontext).load(Constants.URL.Base_Glide_Url + photoTBs.get(position).getUrl())
//                .error(R.drawable.ic_no_record1)
//                .override(DensityUtil.dip2px(300), DensityUtil.dip2px(300))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .crossFade(100).centerCrop().into(holder.imageView);
//        Glide.with(getMContext()).load(getPictureUrl(0)).error(R.drawable.ic_imageview_default_bg).override(DensityUtil.dip2px(400),DensityUtil.dip2px(400))
//                .diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().fitCenter().into(iv_orderAutograph);
        Glide.with(getMContext()).load(getPictureUrl(1)).error(R.drawable.ic_no_record1).override(DensityUtil.dip2px(400),DensityUtil.dip2px(400))
                .diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().fitCenter().into(iv_orderpicture);
        Glide.with(getMContext()).load(getPictureUrl(2)).error(R.drawable.ic_no_record1).override(DensityUtil.dip2px(400),DensityUtil.dip2px(400))
                .diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().fitCenter().into(iv_orderpicture2);
        Glide.with(getMContext()).load(getPictureUrl(3)).error(R.drawable.ic_no_record1).override(DensityUtil.dip2px(400),DensityUtil.dip2px(400))
                .diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().fitCenter().into(iv_orderpicture3);
        Glide.with(getMContext()).load(getPictureUrl(4)).error(R.drawable.ic_no_record1).override(DensityUtil.dip2px(400),DensityUtil.dip2px(400))
                .diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().fitCenter().into(iv_orderpicture4);
        ll_order_pictures.setVisibility(View.VISIBLE);
        iv_orderpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderDetailActivity.this,PhotoActivity.class);
                intent.putExtra("strUrl",getPictureUrl(1));
                startActivity(intent);
            }
        });
        iv_orderpicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderDetailActivity.this,PhotoActivity.class);
                intent.putExtra("strUrl",getPictureUrl(2));
                startActivity(intent);
            }
        });
        iv_orderpicture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderDetailActivity.this,PhotoActivity.class);
                intent.putExtra("strUrl",getPictureUrl(3));
                startActivity(intent);
            }
        });
        iv_orderpicture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderDetailActivity.this,PhotoActivity.class);
                intent.putExtra("strUrl",getPictureUrl(4));
                startActivity(intent);
            }
        });

    }
    /**
     * 获取图片的 url
     * @param remarkInt 标记
     *                  0 客户签名
     *                  1 现场图片1
     *                  2 现场图片2
     * @return 图片的url 路径
     */
    private String getPictureUrl(int remarkInt) {
        try {
            if (customerAutographAndPictures == null) {
                return "";
            }
            int i = 1;
            int j=1;
            for (CustomerAutographAndPicture customerAutographAndPicture : customerAutographAndPictures) {
                try {
                    if (customerAutographAndPicture != null) {
                        String remark = customerAutographAndPicture.getREMARK();
                        if ("Autograph".equals(remark) && remarkInt == 0) {//为客户签名图片
                            return Constants.URL.Load_Url + "Uploadfile/" + customerAutographAndPicture.getPRODUCT_URL();
                        } else if ("pricture".equals(remark)) {
                            String pictureUrl = Constants.URL.Load_Url + "Uploadfile/" + customerAutographAndPicture.getPRODUCT_URL();
                            if (i == 1 && remarkInt == 1) {
                                return pictureUrl;
                            }
                            if (i == 2 && remarkInt == 2) {
                                return pictureUrl;
                            }
                            i++;
                        }else if ("prictureS".equals(remark)){
                           String picturesUrl=Constants.URL.Load_Url+"Uploadfile/"+customerAutographAndPicture.getPRODUCT_URL();
                            if (j==1&&remarkInt==3){
                                return picturesUrl;
                            }
                            if (j==2&&remarkInt==4){
                                return picturesUrl;
                            }
                            j++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onClick(View v) {

    }
}
