package com.example.administrator.ybdriver.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.adapter.NotifyAdapter;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.bean.Notify;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.activity.NotifyActivity;
import com.example.administrator.ybdriver.ui.activity.OrderChooseActivtiy;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.ui.fragment.BaseLifecyclePrintFragment;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页 Fragment
 *
 * @author ke
 */
public class IndexFragment extends BaseLifecyclePrintFragment implements OnClickListener, AsyncHttpCallback {

    private View parent;
    private EditText edittext_serch_track;
    private LinearLayout ll_points;
    private Context mContext;
    private ViewPager vp_ad;
    private List<ImageView> images = new ArrayList<ImageView>();
    private List<ImageView> points = new ArrayList<ImageView>();
    private SlidingTitleView titleView;
    private LinearLayout ll_no_record;
    private int current_position;
    private boolean isContinue;
    private ListView lv_notify;
    private final String Tag_Notify = "tag_notify";
    private List<Notify> notifies;
    private NotifyAdapter notifyAdapter;
    private TextView btn_search_track;
    private Thread cycleThread;//图片轮播线程
    private Boolean iscycleThread=true;//图片轮播线程开关


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        parent = inflater.inflate(R.layout.fragment_index, null);
        mContext = getActivity();
        iscycleThread=true;
        findViews();
        getImages();
        return parent;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  getInformation();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

//    private static IndexFragment mFragment;
//
//    public static IndexFragment getInstance() {
//        if (mFragment == null) {
//            mFragment = new IndexFragment();
//        }
//        return mFragment;
//    }


    private void findViews() {
        vp_ad = (ViewPager) parent.findViewById(R.id.viewpager_ad);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                DensityUtil.getWidth(), DensityUtil.getWidth() / 2);
        vp_ad.setLayoutParams(params);
        ll_points = (LinearLayout) parent.findViewById(R.id.ll_points);
        titleView = (SlidingTitleView) parent.findViewById(R.id.info_title_view);
        titleView.setText(getString(R.string.index));
        titleView.setMode(SlidingTitleView.MODE_NULL);
        ll_no_record = (LinearLayout) parent.findViewById(R.id.ll_no_record);
        ll_no_record.setOnClickListener(this);
        lv_notify = (ListView) parent.findViewById(R.id.lv_notify);
        notifyAdapter = new NotifyAdapter(getActivity());
        lv_notify.setAdapter(notifyAdapter);
        lv_notify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NotifyActivity.class);
                intent.putExtra("title", notifies.get(position).getITitle());
                intent.putExtra("id", notifies.get(position).getIID());
                mStartActivity(intent);
            }
        });
        btn_search_track = (TextView) parent.findViewById(R.id.btn_search_track);
        btn_search_track.setOnClickListener(this);
    }

    //获取到图片列表信息
    private void getImages() {
        images.clear();
        for (int i = 0; i < 4; i++) {
            ImageView image = new ImageView(getActivity());
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            images.add(image);
        }
        images.get(0).setImageResource(R.drawable.ad_pic_0);
        images.get(1).setImageResource(R.drawable.ad_pic_1);
        images.get(2).setImageResource(R.drawable.ad_pic_2);
        images.get(3).setImageResource(R.drawable.ad_pic_3);
        initPager();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        iscycleThread=false;
        ll_points.removeAllViews();
        ll_points = null;
        points.clear();
//        points = null;
    }

    /**
     * 在获取图片信息后，初始化viewpager
     * 这样会有一个问题，在页面初始化时不能第一时间显示出图片信息，优化方案：
     * 本地保存图片地址信息，这样imageloader能获取到缓存
     */
    private void initPager() {
        vp_ad.setAdapter(pagerAdapter);

        ImageView imageView;
//		points = new ImageView[images.size()];
        // 广告栏的小圆点图标
        for (int i = 0; i < images.size(); i++) {
            // 创建一个ImageView, 并设置宽高. 将该对象放入到数组中
            imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DensityUtil.dip2px(10), DensityUtil.dip2px(10));
            params.setMargins(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5),
                    0);
            imageView.setLayoutParams(params);
            //imageViews[i] = imageView;
            points.add(imageView);
            // 初始值, 默认第0个选中
            if (i == 0) {
                points.get(i).setBackgroundResource(R.drawable.point_unfocused);
            } else {
                points.get(i).setBackgroundResource(R.drawable.point_focused);
            }
            // 将小圆点放入到布局中
            ll_points.addView(points.get(i));
        }
        vp_ad.setOnPageChangeListener(pageChangeListener);
        vp_ad.setCurrentItem(points.size() * 100);
//		cycleThread.start();
        cycleThread = new Thread(runnable);
        cycleThread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        isContinue = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isContinue = false;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (iscycleThread) {
                if (isContinue) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(1);

                }

            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                vp_ad.setCurrentItem(current_position + 1);
            }
        }
    };

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//			if (position == points.size() - 1 && )
        }

        @Override
        public void onPageSelected(int position) {
            current_position = position;
            for (int i = 0; i < points.size(); i++) {
                points.get(i).setBackgroundResource(i == position % points.size() ? R.drawable.point_unfocused : R.drawable.point_focused);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            return Integer.MAX_VALUE;
    //		return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
            try {
                container.removeView(images.get(position % images.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            try {
                container.addView(images.get(position % images.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return images.get(position % images.size());
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_track:
               Intent intent = new Intent(mContext,OrderChooseActivtiy.class);
                startActivity(intent);
                break;
            case R.id.ll_no_record:
                getInformation();
                break;
            default:
                break;
        }
    }


    /**
     *@auther: Tom
     *created at 2016/6/12 17:24
     *通过司机的IDX 查找对应的订单消息资讯列表
     */
    private void getInformation() {
        if (notifies == null) {
            notifies = new ArrayList<>();
        }
        OrderAsyncHttpClient client = new OrderAsyncHttpClient(this, this);
        Map<String, String> params = new HashMap<>();
        params.put("strLicense", "");
        params.put("strUserIdx", AppContext.getInstance().getUser().getIDX());
     // 2016.06.13 测试
        client.sendRequest(Constants.URL.Information, params, Tag_Notify);
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")){
            ll_no_record.setVisibility(View.VISIBLE);
            return;
        }else {
            ll_no_record.setVisibility(View.GONE);
        }
        if (request_tag.equals(Tag_Notify)) {
            try {
                JSONObject jo = JSON.parseObject(msg);
                notifies = JSON.parseArray(jo.getString("result"), Notify.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (notifies != null){

            notifyAdapter.setData(notifies);
        }

    }
}
