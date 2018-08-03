package com.example.administrator.ybdriver.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.adapter.OrderChooseAdapter;
import com.example.administrator.ybdriver.bean.Order;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.ui.base.BaseFragmentActivity;
import com.example.administrator.ybdriver.ui.widget.DividerGridItemDecoration;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.ui.fragmentactivity.BaseInputFragmentActivity;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DateUtil;
import com.kaidongyuan.app.basemodule.widget.DateTimePicker.SlideDateTimeListener;
import com.kaidongyuan.app.basemodule.widget.DateTimePicker.SlideDateTimePicker;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/25.
 * 选择订单来查看订单路线轨迹
 */
public class OrderChooseActivtiy extends BaseFragmentActivity implements AsyncHttpCallback{
    private RecyclerView unpayedOrdersRecyclerView,payedOrdersRecyclerView;
    private SlidingTitleView tilteView;
   // private EditText ed_ordersearch;
   // private TextView tv_search;
    private List<Order>unpayedOrderlist;
    private List<Order>payedOrderlist;
    private final String TAG_TRANSIT = "get_unpayed_order";
    private final String TAG_FINISH = "get_payed_order";
    private int unpayedpage ;// 用于存储当前未交付订单的页数
    private int payedpage;// 用于存储当前已交付订单的页数
    private int pagesize=6;//每次加载的订单数
    private OrderAsyncHttpClient mHttpClient;
    private Map<String,String> params;
    private OrderChooseAdapter unpayedorderlistAdapter,payedorderlistAdapter;
    private boolean unpayhasmore=true,payhasmore=true;
    private Date startTime,endTime;
    private TextView tv_startTime,tv_endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderchoose);
        initView();
        initData();
    }

    @Override
    public void initWindow() {
        //重写为空，针对满屏页面取消沉浸式状态栏
    }

    private void initData() {
        long oneMonth =1000L*60*60*24*365;
        startTime= DateUtil.getDateTime(System.currentTimeMillis()-oneMonth);//默认取当前时间的10天前为开始时间
        endTime=DateUtil.getDateTime(System.currentTimeMillis());//默认取当前时间为结束时间
        params=new HashMap<>();
        mHttpClient=new OrderAsyncHttpClient(this,this);
        unpayedOrderlist=new ArrayList<>();
        unpayedorderlistAdapter=new OrderChooseAdapter(OrderChooseActivtiy.this,unpayedOrderlist);
        unpayedOrdersRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        unpayedOrdersRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        unpayedOrdersRecyclerView.setAdapter(unpayedorderlistAdapter);
       // unpayedOrdersRecyclerView.setBackgroundColor(getResources().getColor(R.color.light_yellow));
        unpayedpage=1;
        unpayhasmore=true;
        loadunpayedData(unpayedpage);
        unpayedOrdersRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                StaggeredGridLayoutManager manager= (StaggeredGridLayoutManager) unpayedOrdersRecyclerView.getLayoutManager();
                if (unpayhasmore&&newState==RecyclerView.SCROLL_STATE_IDLE&&(unpayedOrderlist.size()-1)==findMax(manager.findLastVisibleItemPositions(null))) {
                    loadunpayedData(unpayedpage);
                }
            }
        });
        payedOrderlist=new ArrayList<>();
        payedorderlistAdapter=new OrderChooseAdapter(OrderChooseActivtiy.this,payedOrderlist);
        payedOrdersRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        payedOrdersRecyclerView.setAdapter(payedorderlistAdapter);
      //  payedOrdersRecyclerView.setBackgroundColor(getResources().getColor(R.color.light_yellow));
        payedpage=1;
        payhasmore=true;
        loadpayedData(payedpage);
        payedOrdersRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                StaggeredGridLayoutManager manager= (StaggeredGridLayoutManager) payedOrdersRecyclerView.getLayoutManager();
                if (payhasmore&&newState==RecyclerView.SCROLL_STATE_IDLE&&(payedOrderlist.size()-1)==findMax(manager.findLastVisibleItemPositions(null))) {
                    loadpayedData(payedpage);
                }
            }
        });

    }

    private void loadpayedData(int payedpage) {
        if (payedpage==1&&payedOrderlist!=null&&payedOrderlist.size()>0){
            payedOrderlist=new ArrayList<>();
            payedorderlistAdapter.setMorderList(payedOrderlist);
        }
        params = new HashMap<String,String>();
        //	AppContext appContext=AppContext.getInstance();
        //	User user=appContext.getUser();
        params.put("strUserIdx", SharedPreferencesUtils.getUserId());
        params.put("strIsPay", "Y");
        params.put("strPage", payedpage + "");
        params.put("strPageCount", pagesize +"");
        params.put("strLicense", "");
        params.put("startDate",DateUtil.formateWithTime(startTime));
        params.put("endDate",DateUtil.formateWithTime(endTime));
        mHttpClient.sendRequest(Constants.URL.GetDriverOrderDate, params, TAG_FINISH);
    }

    private void loadunpayedData(int unpayed_page) {
        if (unpayed_page==1&&unpayedOrderlist!=null&&unpayedOrderlist.size()>0){
            unpayedOrderlist=new ArrayList<>();
            unpayedorderlistAdapter.setMorderList(unpayedOrderlist);

        }
        params = new HashMap<String,String>();
        params.put("strUserIdx", SharedPreferencesUtils.getUserId());
        params.put("strIsPay", "N");
        params.put("strPage", unpayed_page + "");
        params.put("strPageCount", pagesize +"");
        params.put("strLicense", "");
        mHttpClient.sendRequest(Constants.URL.GetDriverOrderList, params, TAG_TRANSIT);
    }
    private void initView() {
        tilteView= (SlidingTitleView) findViewById(R.id.slidingTitelView_OrderChooseActivity);
        tilteView.setMode(SlidingTitleView.MODE_BACK);
        tilteView.setText("选订单看路线");
        tv_startTime= (TextView) findViewById(R.id.tv_start_time);
        tv_endTime= (TextView) findViewById(R.id.tv_end_time);

      // tv_search=tilteView.getGoods_managment();
      //  ed_ordersearch=tilteView.getEd_center();
      //  ed_ordersearch.setHint("\t请输要查询的发货单号");
        unpayedOrdersRecyclerView= (RecyclerView) findViewById(R.id.unpayedorders_recyclerView_OrderChooseActivity);
        payedOrdersRecyclerView= (RecyclerView) findViewById(R.id.payedorders_recyclerView_OrderChooseActivity);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mHttpClient.cancleRequest(TAG_FINISH,TAG_TRANSIT);
        super.onDestroy();
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")&&request_tag.equals(TAG_TRANSIT)&&unpayedOrderlist.size()<=0){
           // unpayedOrdersRecyclerView.setVisibility(View.GONE);
            return;
        }else if (msg.equals("error")&&request_tag.equals(TAG_FINISH)&&payedOrderlist.size()<=0){
            //payedOrdersRecyclerView.setVisibility(View.GONE);
            return;
        }else if (msg.equals("error")){
           //
            return;
        }
        else if (request_tag.equals(TAG_TRANSIT)){
            unpayedpage++;
            JSONObject jo= JSON.parseObject(msg);
            List<Order> currentorders= JSON.parseArray(jo.getString("result"),Order.class);
            if (unpayedOrderlist!=null&&!unpayedOrderlist.containsAll(currentorders)){
                unpayedOrderlist.addAll(currentorders);
                unpayedorderlistAdapter.setMorderList(unpayedOrderlist);
            }
            if (currentorders.size()<pagesize){
                showToastMsg("已滑到末端");
                unpayhasmore=false;
            }
            return;
        }
        else if (request_tag.equals(TAG_FINISH)){
            if (payedOrderlist==null){
                showToastMsg("请重新进入轨迹查询界面");
                finish();
                return;
            }
            if (payedpage==1){
                if (payedOrderlist.size()==0) {
                    tv_startTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                    .setListener(new DateHandler(tv_startTime.getId()))
                                    .setInitialDate(startTime)
                                    // .setMinDate(new Date()) 这样就导致取时间最小为当前时间
                                    .setMaxDate(DateUtil.getDateTime(System.currentTimeMillis()))
                                    .build()
                                    .show();
                        }
                    });

                    tv_endTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                    .setListener(new DateHandler(tv_endTime.getId()))
                                    .setInitialDate(endTime)
                                    .build()
                                    .show();
                        }
                    });
                }else {
                    payedOrderlist.clear();
                }

            }
            payedpage++;
            JSONObject jo=JSON.parseObject(msg);
            List<Order>currentorders=JSON.parseArray(jo.getString("result"),Order.class);
            if (!payedOrderlist.containsAll(currentorders)){

                payedOrderlist.addAll(currentorders);
                payedorderlistAdapter.setMorderList(payedOrderlist);
            }if (currentorders.size()<pagesize){
                showToastMsg("已滑到末端");
                payhasmore=false;
            }
            return;
        }
    }

    class DateHandler extends SlideDateTimeListener {

        TextView tv;
        DateHandler(int which) {
            tv= (TextView) findViewById(which);
        }

        @Override
        public void onDateTimeCancel() {
            super.onDateTimeCancel();

            tv.setText(null);
        }
        @Override
        public void onDateTimeSet(Date date) {
            if (date != null) {
               // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if (tv.getId()==R.id.tv_start_time){
                    date.setHours(0);
                    date.setMinutes(0);
                    date.setSeconds(0);
                    tv.setText(df.format(date));
                    tv.setTextColor(getResources().getColor(R.color.white));
                    startTime=date;//开始时间设置为XXXX-XX-XX 00:00:00
                    payedpage=1;
                    payhasmore=true;
                    loadpayedData(payedpage);
                }else if (tv.getId()==R.id.tv_end_time){
                    date.setHours(23);
                    date.setMinutes(59);
                    date.setSeconds(59);
                    tv.setText(df.format(date));
                    tv.setTextColor(getResources().getColor(R.color.white));
                    endTime=date;//结束时间设置为XXXX-XX-XX 23:59:59
                    payedpage=1;
                    payhasmore=true;
                    loadpayedData(payedpage);
                }
            }
        }
    }

}
