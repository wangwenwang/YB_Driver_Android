package com.example.administrator.ybdriver.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.bean.Location;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.utils.baidumapUtils.DrivingRouteOverlay;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.network.BaseAsyncHttpClient;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/26.
 */
public class OrderTrackActivity extends BaseActivity implements AsyncHttpCallback {
    private SlidingTitleView titleView;
    private OrderAsyncHttpClient mClient;
    private final String Tag_Get_Locations = "Tag_Get_Locations";
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    public double distance=0;
    public  double distance0=0;
    private int agains=10;//可以重新请求规划路段的次数
    private RoutePlanSearch mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordertrack);
        mClient=new OrderAsyncHttpClient(this,this);
        mMapView= (MapView) findViewById(R.id.mapView_orderTrack);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
//        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
//            @Override
//            public void onTouch(MotionEvent motionEvent) {
//                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//            }
//        });
        initview();
        getPath();
    }
    @Override
    public void initWindow() {
        //重写为空，针对满屏页面取消沉浸式状态栏
    }
    private void getPath() {
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("order_IDX");
        if (orderId == null || orderId.equals("")) {
            showToastMsg("发货单号有误，请返回重新加载");
            finish();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("strOrderId", orderId);
        params.put("strLicense", "");
//        params.put("roleNames", tv_select_role.getText()+"");
        mClient.sendRequest(Constants.URL.GetLocaltion, params, Tag_Get_Locations);
    }

    private void initview() {
        titleView= (SlidingTitleView) findViewById(R.id.slidingtitelView_OrderTrackActivity);
        titleView.setMode(SlidingTitleView.MODE_BACK);
        titleView.setText("正绘制路线");

    }

    /**
     * 计算从起点到终点的路程
     * @param locationList 路程上所有坐标点的集合
     * @return 起点到终点的距离
     */
    private double getDistance(List<Location> locationList){
        //计算路程
        double distance = 0;
        int size = locationList.size()-1;
        for (int i = 1; i < size; i++) {
            Location nowLocation = locationList.get(i);
            Location perviousLocation = locationList.get(i-1);
            LatLng nowLatLng = new LatLng(nowLocation.CORDINATEY, nowLocation.CORDINATEX);
            LatLng perviousLatLng = new LatLng(perviousLocation.CORDINATEY, perviousLocation.CORDINATEX);
            distance += DistanceUtil.getDistance(nowLatLng , perviousLatLng);
        }
        return distance;
    }

    /**
     * 绘制起点终点标记和路线
     * @param locationList 路程中坐标点的集合
     */
    private void searchInMap(final List<Location> locationList,int j) {
        final int again=j;
        agains=agains-1;
        MLog.e("进入搜索线路方法");
        if (mBaiduMap == null) return ;
        //=============================================================================================================================
        //2016-03-08日修改前的代码，双重注释为修改前已经注释了的
//        LatLng stLatLng = new LatLng(locationList.get(0).CORDINATEX, locationList.get(0).CORDINATEY);
        LatLng stLatLng = new LatLng(locationList.get(0).CORDINATEY, locationList.get(0).CORDINATEX);
//        LatLng enLatLng = new LatLng(locationList.get(locationList.size() - 1).CORDINATEX, locationList.get(locationList.size() - 1).CORDINATEY);
        LatLng enLatLng = new LatLng(locationList.get(locationList.size() - 1).CORDINATEY, locationList.get(locationList.size() - 1).CORDINATEX);
        mSearch = RoutePlanSearch.newInstance();
        PlanNode stNode = PlanNode.withLocation(stLatLng);
//        enLatLng = new LatLng(23.629196, 115.046872); // 测试数据
        PlanNode enNode = PlanNode.withLocation(enLatLng);
        List<PlanNode> passBy = new ArrayList<>();
        for (int i = 1; i < (locationList.size() - 2); i++) {
            passBy.add(PlanNode.withLocation(new LatLng(locationList.get(i).CORDINATEY, locationList.get(i).CORDINATEX)));
        }
        DrivingRoutePlanOption drivingRoutePlanOption=new DrivingRoutePlanOption().from(stNode).passBy(passBy).policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST).to(enNode);
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                //获取步行线路规划结果
            }

            public void onGetTransitRouteResult(TransitRouteResult result) {
                //获取公交换乘路径规划结果
            }

            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                //获取驾车线路规划结果
                MLog.e("驾车线路规划结果");
                MLog.w("onGetDrivingRouteResult");
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    MLog.e("error:" + "抱歉，未找到结果");
                    if (!OrderTrackActivity.this.isFinishing()&&agains>=0){
                        searchInMap(locationList,again);
                        titleView.setText("查询线路中");
                    }
                    if (again==1){
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    }
                    return ;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    MLog.e("error:" + result.getSuggestAddrInfo().toString());
                    if (again==1){
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    }
                    return;
                }

                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    MLog.d("onGetDrivingRouteResult no error");
                    try {
                        DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
 //                       mBaiduMap.setOnMarkerClickListener(overlay);
                        //2016.3.10插入两句代码
//                        if (result.getRouteLines().get(0).getDistance()>getDistance(locationList)) {
                            distance += result.getRouteLines().get(0).getDistance();
//                        } else {
//                            distance+=getDistance(locationList);
//                        }
                        titleView.setText(distance0>distance
                                ? titleView.getText() : ("公里数：" +distance/1000 + "公里"));
                        MLog.e("驾车规划出来的数据："+distance/1000 + "公里");

                        overlay.setData(result.getRouteLines().get(0));

                        overlay.addToMap();
                        if (again==1){
                            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        }
                        overlay.zoomToSpan();
                     //   titleView.setText("路线");
                        mSearch.destroy();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (again==1){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }

        };
        //2016.3.10
        MLog.e(" mSearch.setOnGetRoutePlanResultListener(listener)");
        mSearch.setOnGetRoutePlanResultListener(listener);

        //移动节点至起点
        MLog.e(" 移动节点至起点" + "总计点数" + locationList.size());

        MLog.e(" mSearch.drivingSearch");
        titleView.setText(mSearch.drivingSearch(drivingRoutePlanOption)? "正绘制路线":"路线有误，请重新查看");

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    protected void onDestroy() {
        mClient.cancleRequest(Tag_Get_Locations);
      if (null!=mSearch){
          mSearch.destroy();
      }
        super.onDestroy();
        mMapView.onDestroy();

    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            showToastMsg("无轨迹数据，请重新加载！");
            return;
        }else if (request_tag.equals(Tag_Get_Locations)){
            JSONObject jo= JSON.parseObject(msg);
            List<Location>locationlist=JSON.parseArray(jo.getString("result"),Location.class);
            if (locationlist==null||locationlist.size()<=0){
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                return;
            }
            //绘制路线
            for (int i=0;i<locationlist.size()/100;i++){
                List<Location>locationListfd=locationlist.subList(i*100,i*100+101);
                searchInMap(locationListfd,0);
            }
            List<Location>locationListmr=locationlist.subList(locationlist.size() - locationlist.size() % 100, locationlist.size());
            searchInMap(locationListmr,1);
            //计算路程，并显示到界面上
            distance0 = getDistance(locationlist);
        //  titleView.setText(locationlist.size() == 0 ? "无数据" : ("公里数：" + distance0 / 1000 + "公里"));
            MLog.e("直接加两点距离出来的数据：" + distance0);
            //绘制起点和终点的图标
            Location startLocation = locationlist.get(0);
            Location endLocation = locationlist.get(locationlist.size()-1);
            LatLng stLatLng = new LatLng(startLocation.CORDINATEY, startLocation.CORDINATEX);
            LatLng enLatLng = new LatLng(endLocation.CORDINATEY, endLocation.CORDINATEX);
            BitmapDescriptor stbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            BitmapDescriptor enbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_car);
            MarkerOptions stOption = new MarkerOptions().position(stLatLng).icon(stbitmap).zIndex(5);
            MarkerOptions enOption = new MarkerOptions().position(enLatLng).icon(enbitmap).zIndex(5);
            mBaiduMap.addOverlay(stOption);
            mBaiduMap.addOverlay(enOption);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(stLatLng));
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));
        }
    }
    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        private  boolean useDefaultST=false;
        private  boolean useDefaultEN=false;
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        public  MyDrivingRouteOverlay(BaiduMap baiduMap,boolean stIcon,boolean enIcon){
            super(baiduMap);
            useDefaultST=stIcon;
            useDefaultEN=enIcon;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultST){
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
            return  BitmapDescriptorFactory.fromResource(R.drawable.chose_cardwhite);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultEN) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
            return  BitmapDescriptorFactory.fromResource(R.drawable.chose_cardwhite);
        }

        @Override
        public List<BitmapDescriptor> getCustomTextureList() {
            return super.getCustomTextureList();
        }

        @Override
        public int getLineColor() {
            return  Color.argb(178, 245, 20, 28);
        }
    }
}
