package com.example.administrator.ybdriver.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;

import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.adapter.FinishedOrdersAdapter;
import com.example.administrator.ybdriver.adapter.UnpayedOrdersAdapter;
import com.example.administrator.ybdriver.bean.Order;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.widget.DividerListItemDecoration;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.example.administrator.ybdriver.utils.StringUtils;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.ui.fragment.BaseLifecyclePrintFragment;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DateUtil;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/14.
 */
public class UnpayedFragment extends BaseLifecyclePrintFragment implements AsyncHttpCallback {
    private RecyclerView unpayedReclerView;
    private FinishedOrdersAdapter myorderlistAdapter;
    private  ArrayList<Order>orderlist;
    private SlidingTitleView mtitelView;
    private Context mContext;
    private int page = 1;// 用于存储当前数据的页数
    private final int pageSize = 6;// 每页的数据条数
    private OrderAsyncHttpClient mHttpClient;
    private Map<String,String> params;
    private final String TAG_TRANSIT = "get_unpayed_order";
    public boolean loaded = false;
    private LinearLayout ll_no_record;
    public static boolean isrefresh=false;
    private View parent;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        parent=inflater.inflate(R.layout.fragment_unpayed, null);
        mContext=getActivity();
        page=1;
        return parent;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadData(page);
    }

    private void loadData(int page) {
        loaded=true;
        if (page==1&&orderlist!=null&&orderlist.size()>0){
                orderlist=new ArrayList<>();
            myorderlistAdapter.loadState= UnpayedOrdersAdapter.LOADING_MORE;
        }
        params = new HashMap<String, String>();
        //	AppContext appContext=AppContext.getInstance();
        //	User user=appContext.getUser();
        params.put("strUserIdx", SharedPreferencesUtils.getUserId());
        params.put("strIsPay", "N");
        params.put("strPage", page + "");
        params.put("strPageCount", pageSize +"");
        params.put("strLicense", "");
      //  String datestr=StringUtils.formatDate(DateUtil.getDateTime(System.currentTimeMillis()),"yyyy/MM/dd HH:mm:ss");
      //  params.put("startDate", datestr);
      //  params.put("endDate",datestr);
        mHttpClient.sendRequest(Constants.URL.GetDriverOrderList, params, TAG_TRANSIT);

    }

    private void initView() {
        mContext=getActivity();
        isrefresh=false;
        mtitelView= (SlidingTitleView) parent.findViewById(R.id.unpayed_titel_view);
        mtitelView.setText(getString(R.string.unpayed_order));
        mtitelView.setMode(SlidingTitleView.MODE_NULL);
        mswipeRefreshLayout= (SwipeRefreshLayout) parent.findViewById(R.id.swipeRefreshLayout_orderlist_unpayed);
        mswipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_orange_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        unpayedReclerView= (RecyclerView) parent.findViewById(R.id.recyclerView_orderlist_unpayed);
        orderlist=new ArrayList<>();
        myorderlistAdapter=new FinishedOrdersAdapter(orderlist,UnpayedFragment.this);
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        unpayedReclerView.setLayoutManager(manager);
        unpayedReclerView.addItemDecoration(new DividerListItemDecoration(getMContext(), DividerListItemDecoration.VERTICAL_LIST));
        unpayedReclerView.setAdapter(myorderlistAdapter);
        mHttpClient=new OrderAsyncHttpClient(this,this);
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                loadData(page);
            }
        });
        unpayedReclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (manager.findLastVisibleItemPosition() + 1) == myorderlistAdapter.getItemCount()) {
                   if (myorderlistAdapter.loadState!=UnpayedOrdersAdapter.NO_MORE) {
                       myorderlistAdapter.loadState = UnpayedOrdersAdapter.LOADING_MORE;
                       loadData(++page);
                   }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        ll_no_record= (LinearLayout) parent.findViewById(R.id.ll_no_record);
        ll_no_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page=1;
                mswipeRefreshLayout.setVisibility(View.VISIBLE);
                ll_no_record.setVisibility(View.GONE);
                loadData(page);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果有订单提交则通过改变isrefresh值为true来刷新orderlist数据
        if (isrefresh==true){
            page=1;
            loadData(page);
            isrefresh=false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mHttpClient.cancleRequest(TAG_TRANSIT);
        super.onDestroyView();
    }


    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        mswipeRefreshLayout.setRefreshing(false);
        if (msg.equals("error")){
            MLog.w("postSuccessMsg error");

            if (page == 1){
                if (orderlist == null || orderlist.size() == 0){
                    mswipeRefreshLayout.setVisibility(View.GONE);
                    ll_no_record.setVisibility(View.VISIBLE);
                }else {
                    mswipeRefreshLayout.setVisibility(View.VISIBLE);
                    ll_no_record.setVisibility(View.GONE);
                }
            }else {
                myorderlistAdapter.loadState= UnpayedOrdersAdapter.NO_MORE;
                myorderlistAdapter.notifyItemChanged(manager.findLastVisibleItemPosition());
            }
            return;
        }
        if (request_tag.equals(TAG_TRANSIT)){
            com.alibaba.fastjson.JSONObject object= JSON.parseObject(msg);
            List<Order>tmsOrderList=JSON.parseArray(object.getString("result"),Order.class);
            MLog.i("tmsOrderlist.size:" + tmsOrderList.size());
            if (orderlist!=null){
                orderlist.addAll(tmsOrderList);
                if (orderlist.size()<pageSize){
                    myorderlistAdapter.loadState=UnpayedOrdersAdapter.NO_MORE;
                }
                myorderlistAdapter.setMorderlist(orderlist);
            }
        }
    }
}
