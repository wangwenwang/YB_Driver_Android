package com.example.administrator.ybdriver.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.adapter.PieLegendAdapter;
import com.example.administrator.ybdriver.bean.FactoryBusiness;
import com.example.administrator.ybdriver.bean.LCBusiness;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseFragmentActivity;
import com.example.administrator.ybdriver.ui.widget.IntegerValueFormatter;
import com.example.administrator.ybdriver.ui.widget.XLabersAxisValueFormatter;
import com.example.administrator.ybdriver.utils.FloatArithUtil;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.example.administrator.ybdriver.utils.UIUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DateUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.ListViewUtils;
import com.kaidongyuan.app.basemodule.widget.DateTimePicker.SlideDateTimeListener;
import com.kaidongyuan.app.basemodule.widget.DateTimePicker.SlideDateTimePicker;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 20180531
 * 工厂管理信息Activity
 */
public class FactoryHorizontalChartActivity extends BaseFragmentActivity implements OnChartValueSelectedListener,AsyncHttpCallback {
    private SlidingTitleView titelView;
    private HorizontalBarChart mbarchart;
    private PieChart mpiechart,mpiechart1;
    private TextView tvStartTime,tvEndTime;
    private ArrayList<BarEntry> barEntries=new ArrayList<>();
    private BarDataSet dataSet;
    public ArrayList<String>labels=new ArrayList<>();
    private ArrayList<IBarDataSet> iBarDataSets;
    private ArrayList<PieEntry> xPieVals=new ArrayList<>();
    private ArrayList<PieEntry>yPieVals=new ArrayList<>();
    private OrderAsyncHttpClient mClient;
    private HashMap<String,String> params;
    private Date startTime,endTime;
    private String TAG_GetFactoryCount="GetFactoryCount";
    private FactoryBusiness lcBusiness;
    private List<FactoryBusiness> lcBusinesslist;
    private ListView lv_piechart1_legends;
    private PieLegendAdapter legendAdapter;
    //  private String[] labels1;

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.activity_manage_chart);
//        initview();
//        initEntries();
//        initLableData();
//        show();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory_horizontalchart);
        initview();
        //  initLableData();
        //  initEntries();
        //  initshow();
    }

    private void initBarData(int qty) {
       ViewGroup.LayoutParams layoutParams=mbarchart.getLayoutParams();
        if (barEntries.size()>7){
            layoutParams.height=barEntries.size()* UIUtils.dip2px(40);
        }else {
            layoutParams.height=UIUtils.dip2px(280);
        }

        mbarchart.setLayoutParams(layoutParams);
        //   iBarDataSets = new ArrayList<>();
        BarDataSet barDataSet=new BarDataSet(barEntries,"怡宝工厂");
        barDataSet.setColors(Constants.MPBAR_COLORS);
     //   barDataSet.setHighLightColor(getResources().getColor(R.color.red));
        //   iBarDataSets.add(barDataSet);
//        dataSet=new BarDataSet(barEntries,"#依次分别代表全国各区的销售业绩");
//    //    dataSet.setStackLabels(labels1);
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        BarData data=new BarData(dataSet);
//        BarData data1=new BarData(labels,dataSet);
//        LimitLine limitLine=new LimitLine(10f,"合格线");
        BarData data=new BarData(barDataSet);
        data.setBarWidth(0.6f);
        //设置Bar上面的显示值样式内容
        data.setValueFormatter(new IntegerValueFormatter(labels));
        data.setValueTextSize(10f);
      //  barDataSet.setDrawValues(false);
      // data.setValueTextColor(Color.YELLOW);
        mbarchart.setData(data);
        mbarchart.setOnChartValueSelectedListener(this);
        mbarchart.setDragEnabled(true);
        //mbarchart.setDrawValueAboveBar(false);//设置Bar上面的显示值在bar的上方或内侧

        mbarchart.setScaleYEnabled(false);
        //  mbarchart.setMinOffset(1f);
        mbarchart.setDrawGridBackground(false);
        mbarchart.setDrawBarShadow(false);
//        Legend mlegend=mbarchart.getLegend();
//        mlegend.setForm(Legend.LegendForm.CIRCLE);
//        mlegend.setFormSize(6f);
//        mlegend.setTextColor(Color.RED);
//        XAxis xAxis=mbarchart.getXAxis();
//        xAxis.addLimitLine(limitLine);
      //  mbarchart.setFitBars(true);

        mbarchart.animateY(2000);
        //设置X轴的标签样式内容
        XAxis xAxis=mbarchart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        //取消X轴的竖向分割线
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new XLabersAxisValueFormatter(mbarchart,labels,1-data.getBarWidth()/2));
       // xAxis.setLabelRotationAngle(90);
        xAxis.setAxisMinValue(0);
        //设置标识labers的最小间距为1f，防止重复出现
        xAxis.setGranularity(1f);
        xAxis.setAxisMaxValue(data.getXMax()+data.getBarWidth());
        YAxis yAxisleft=mbarchart.getAxisLeft();
        yAxisleft.setAxisMinValue(0);
        yAxisleft.setSpaceTop(100f);
        YAxis yAxisright=mbarchart.getAxisRight();
        yAxisright.setAxisMinValue(0);
        yAxisright.setSpaceTop(100f);
        mbarchart.setDescription("汇总发货总数："+qty);
        mbarchart.setDescriptionColor(getResources().getColor(R.color.red));
        mbarchart.setDescriptionTextSize(12);
        mbarchart.setDescriptionPosition(mbarchart.getWidth()-10,mbarchart.getViewPortHandler().offsetBottom()+10);
        mbarchart.setNoDataText("无数据，请稍后重试");
//        mbarchart.resetViewPortOffsets();
//        xAxis.setDrawGridLines(false);
//        xAxis.setXOffset(1f);
//        mbarchart.invalidate();
//        mbarchart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //在视图树监听器中绘制图表描述的位置
//                mbarchart.setDescriptionPosition(mbarchart.getWidth()-mbarchart.getViewPortHandler().offsetRight(),mbarchart.getViewPortHandler().offsetBottom());
//            }
//        });


    }

    private void initPieData(int i) {
        float Ndeliverf,Adeliverf,Arrivef;
        yPieVals=new ArrayList<>();
        if (lcBusinesslist!=null&&lcBusinesslist.size()>i) {
            FactoryBusiness lcBusinessi = lcBusinesslist.get(i);
            Arrivef=(float) lcBusinessi.getArrive()/lcBusinessi.getQtyTotal();
           // Adeliverf=(float) lcBusinessi.getAdeliver()/lcBusinessi.getQtyTotal();
            Ndeliverf=(float)lcBusinessi.getNdeliver()/lcBusinessi.getQtyTotal();
            Adeliverf=1-Ndeliverf-Arrivef;
            if (Arrivef>0){
                yPieVals.add(new PieEntry(Arrivef*100f,"已到达:"+lcBusinessi.getArrive()));
            }
            if (Adeliverf>0){
                yPieVals.add(new PieEntry(Adeliverf*100f,"已交付:"+lcBusinessi.getAdeliver()));
            }
            if (Ndeliverf>0){
                yPieVals.add(new PieEntry(Ndeliverf*100f,"未交付:"+lcBusinessi.getNdeliver()));
            }
            PieDataSet yDataSet = new PieDataSet(yPieVals, "\t\t物流状态");

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c:ColorTemplate.MATERIAL_COLORS)
                colors.add(c);
            yDataSet.setColors(colors);
            PieData piedata=new PieData(yDataSet);
            piedata.setValueTextSize(14);
            piedata.setValueTextColor(getResources().getColor(R.color.white));
            piedata.setValueFormatter(new PercentFormatter());
            mpiechart.setData(piedata);
            mpiechart.setUsePercentValues(true);
            mpiechart.setDragDecelerationFrictionCoef(0.95f);
            mpiechart.setCenterText(lcBusinessi.getShip_from_name()+"发货总数："+lcBusinessi.getQtyTotal());
            mpiechart.setDescription("");
            mpiechart.setExtraOffsets(5, 5, 5, 5);
            mpiechart.setTransparentCircleColor(Color.WHITE);
            mpiechart.animateXY(1000,1000);
            mpiechart.setEntryLabelColor(Color.WHITE);
            mpiechart.setEntryLabelTextSize(12f);
            mpiechart.setDrawEntryLabels(false);//取消显示图例注解在圆饼上
            //设置右上角注解条目样式
            Legend l = mpiechart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//            l.setXEntrySpace(7f);
//            l.setYEntrySpace(0f);
//            l.setYOffset(0f);
            mpiechart.invalidate();
        }else {
            mpiechart.setVisibility(View.GONE);
        }
    }
    private void  initPie1Data(int totalqty){
        xPieVals=new ArrayList<>();
        if (lcBusinesslist!=null&&lcBusinesslist.size()>0){
            float lastf=1f;
            for (int i=0;i<lcBusinesslist.size()-1;i++){
                float lcBusinessf=(float)lcBusinesslist.get(i).getQtyTotal()/totalqty;
                if (lcBusinessf>0){
                    //            xPieVals.add(new PieEntry(lcBusinessf*100f,"发货:"+lcBusinesslist.get(i).getQtyTotal(),i));
                    xPieVals.add(new PieEntry(lcBusinessf*100f,lcBusinesslist.get(i).getShip_from_name()+" 数量:"+lcBusinesslist.get(i).getQtyTotal()+" 占比:"+ FloatArithUtil.round(lcBusinessf*100f,1)+"%",i));

                }
                lastf=lastf-lcBusinessf;
            }
            //    xPieVals.add(new PieEntry(lastf*100f,"发货:"+lcBusinesslist.get(lcBusinesslist.size()-1).getQtyTotal(),lcBusinesslist.size()-1));
            xPieVals.add(new PieEntry(lastf*100f,lcBusinesslist.get(lcBusinesslist.size()-1).getShip_from_name()+" 数量:"+lcBusinesslist.get(lcBusinesslist.size()-1).getQtyTotal()+" 占比:"+FloatArithUtil.round(lastf*100f,1)+"%",lcBusinesslist.size()-1));
            PieDataSet xDataSet = new PieDataSet(xPieVals, "");
            xDataSet.setColors(Constants.MPBAR_COLORS);
            PieData piedata1=new PieData(xDataSet);
            piedata1.setValueTextSize(14);
            piedata1.setValueTextColor(getResources().getColor(R.color.white));
            piedata1.setValueFormatter(new PercentFormatter());
            mpiechart1.setData(piedata1);
            mpiechart1.setUsePercentValues(true);
            mpiechart1.setOnChartValueSelectedListener(this);
            mpiechart1.setDragDecelerationFrictionCoef(0.95f);
            mpiechart1.setCenterText("工厂发货占比图");
            mpiechart1.setDescription("");
          //  mpiechart1.setExtraOffsets(5, 5, 5, 5);
            mpiechart1.setTransparentCircleColor(Color.WHITE);
            mpiechart1.animateXY(1000,1000);
            mpiechart1.setEntryLabelColor(getResources().getColor(R.color.gray_holo_dark));
            mpiechart1.setEntryLabelTextSize(12f);
            mpiechart1.setDrawEntryLabels(false);
            //设置右上角注解条目样式
            Legend l = mpiechart1.getLegend();
            Integer[] colors=new Integer[l.getColors().length-1];
            for (int i=0;i<colors.length;i++){
                colors[i]=Integer.valueOf(l.getColors()[i]);
            }
            l.setEnabled(false);
            legendAdapter = new PieLegendAdapter(FactoryHorizontalChartActivity.this,Arrays.asList(colors));
            lv_piechart1_legends.setAdapter(legendAdapter);
            legendAdapter.setData(xPieVals,false);
            ListViewUtils.setListViewHeightBasedOnChildren(lv_piechart1_legends);
//            l.setWordWrapEnabled(true);
//            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//            l.setXEntrySpace(0f);
//            l.setYEntrySpace(0f);
//            l.setYOffset(0f);
            mpiechart1.invalidate();
        }else {
            mpiechart1.setVisibility(View.GONE);
        }

    }
    private void initview() {
        titelView= (SlidingTitleView) findViewById(R.id.slidingTitelView_managechartActivity);
        titelView.setMode(SlidingTitleView.MODE_BACK);
        titelView.setText(getString(R.string.factory_manage_info));
        tvStartTime= (TextView) findViewById(R.id.tv_start_time);
        tvEndTime= (TextView) findViewById(R.id.tv_end_time);
        mbarchart= (HorizontalBarChart) findViewById(R.id.barChart_managechartActivity);
        mpiechart= (PieChart) findViewById(R.id.pieChart_managechartActivity);
        mpiechart1= (PieChart) findViewById(R.id.pieChart1_managechartActivity);
        lv_piechart1_legends= (ListView) findViewById(R.id.lv_piechart1_legends);
        mClient=new OrderAsyncHttpClient(this,this);
        startTime=DateUtil.weeHours(new Date(),0);
        endTime=DateUtil.weeHours(new Date(),1);
        tvStartTime.setText("起"+DateUtil.formateWithoutTime(startTime));
        tvEndTime.setText(DateUtil.formateWithoutTime(endTime)+"止");
        getChartData();
    }

    private void getChartData() {
        params = new HashMap<String,String>();
        params.put("strUserId", SharedPreferencesUtils.getUserId());
        params.put("startDate", DateUtil.formateWithoutTime(startTime));
        params.put("endDate",DateUtil.formateWithoutTime(endTime));
        params.put("strLicense", "");
        mClient.sendRequest(Constants.URL.GetDateCestbonFleetCount, params, TAG_GetFactoryCount);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
      //  showToastMsg("查看"+lcBusinesslist.get((Integer) e.getData()).getORD_FROM_NAME()+"圆饼");
      //  initPieData((Integer) e.getData());

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")){
            tvStartTime.setText("请输入起始日期");
            tvEndTime.setText("请输入截止日期");
            startTime=DateUtil.weeHours(new Date(),0);
            endTime=DateUtil.weeHours(new Date(),1);
            return;
        }
        if (request_tag.equals(TAG_GetFactoryCount)){
            JSONObject jo= JSON.parseObject(msg);
            if(lcBusinesslist==null){
                tvStartTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                .setListener(new FactoryHorizontalChartActivity.DateHandler(tvStartTime.getId()))
                                .setInitialDate(startTime)
                                // .setMinDate(new Date()) 这样就导致取时间最小为当前时间
                                .setMaxDate(DateUtil.getDateTime(System.currentTimeMillis()))
                                .build()
                                .show();
                    }
                });
                tvEndTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                .setListener(new FactoryHorizontalChartActivity.DateHandler(tvEndTime.getId()))
                                .setInitialDate(endTime)
                                // .setMinDate(new Date()) 这样就导致取时间最小为当前时间
                                .setMaxDate(DateUtil.getDateTime(System.currentTimeMillis()))
                                .build()
                                .show();
                    }
                });
            }
            lcBusinesslist=new ArrayList<>();
            try {
                lcBusinesslist = JSON.parseArray(jo.getString("result"),FactoryBusiness.class);
            }catch (JSONException e){
                e.printStackTrace();
                lcBusinesslist=null;
            }
            if (lcBusinesslist !=null&& lcBusinesslist.size()>0){
                //冒泡排序
                for (int i=0;i<lcBusinesslist.size()-1;i++){
                    for (int j=0;j<lcBusinesslist.size()-1-i;j++){
                        if (lcBusinesslist.get(j).getQtyTotal()>lcBusinesslist.get(j+1).getQtyTotal()){
                            FactoryBusiness temp=lcBusinesslist.get(j);
                            lcBusinesslist.set(j,lcBusinesslist.get(j+1));
                            lcBusinesslist.set(j+1,temp);
                        }
                    }
                }
                setData(lcBusinesslist);
            }
        }

    }

    private void setData(List<FactoryBusiness> lcBusinesslist) {
        lcBusiness = new FactoryBusiness();
        int  qtyTotal=0;//各物流商汇总发货数
        labels=new ArrayList<>();
        barEntries=new ArrayList<>();
        for (int i=0;i<lcBusinesslist.size();i++){
            lcBusiness =lcBusinesslist.get(i);
            if (lcBusiness.getQtyTotal()>0) {
                labels.add(lcBusiness.getQtyTotal()+"/"+lcBusiness.getShip_from_name());
                barEntries.add(new BarEntry(i + 1f,lcBusiness.getQtyTotal(),i));
                qtyTotal += lcBusiness.getQtyTotal();
            }
        }
        initBarData(qtyTotal);
      //  initPieData(0);
        initPie1Data(qtyTotal);

    }
    class DateHandler extends SlideDateTimeListener {

        TextView tv;
        DateHandler(int which) {
            tv= (TextView) findViewById(which);
        }

        @Override
        public void onDateTimeCancel() {
            super.onDateTimeCancel();

         //  tv.setText(null);
        }
        @Override
        public void onDateTimeSet(Date date) {
            if (date != null) {
                // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                if (tv.getId()==R.id.tv_start_time){
                    startTime=DateUtil.weeHours(date,0);
                    tv.setText("起"+df.format(startTime));
                    getChartData();
                }else if (tv.getId()==R.id.tv_end_time){
                    endTime=DateUtil.weeHours(date,1);
                    tv.setText(df.format(endTime)+"止");
                    getChartData();

                }
                return;
            }
        }
    }
}
