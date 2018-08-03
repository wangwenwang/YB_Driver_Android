package com.example.administrator.ybdriver.ui.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.bean.LCBusiness;
import com.example.administrator.ybdriver.canstants.Constants;
import com.example.administrator.ybdriver.httpclient.OrderAsyncHttpClient;
import com.example.administrator.ybdriver.ui.base.BaseFragmentActivity;
import com.example.administrator.ybdriver.ui.widget.CharLabersAxisValueFormatter;
import com.example.administrator.ybdriver.ui.base.BaseActivity;
import com.example.administrator.ybdriver.ui.widget.IntegerValueFormatter;
import com.example.administrator.ybdriver.ui.widget.XLabersAxisValueFormatter;
import com.example.administrator.ybdriver.utils.SharedPreferencesUtils;
import com.github.mikephil.charting.charts.BarChart;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DateUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils;
import com.kaidongyuan.app.basemodule.widget.DateTimePicker.SlideDateTimeListener;
import com.kaidongyuan.app.basemodule.widget.DateTimePicker.SlideDateTimePicker;
import com.kaidongyuan.app.basemodule.widget.SlidingTitleView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 * 管理信息Activity
 */
public class ManageChartActivity extends BaseFragmentActivity implements OnChartValueSelectedListener,AsyncHttpCallback {
    private SlidingTitleView titelView;
    private BarChart mbarchart;
    private PieChart mpiechart;
    private TextView chartTitel;
    private ArrayList<BarEntry> barEntries=new ArrayList<>();
    private BarDataSet dataSet;
    public ArrayList<String>labels=new ArrayList<>();
    private ArrayList<IBarDataSet> iBarDataSets;
    private ArrayList<String> xPieVals=new ArrayList<>();
    private ArrayList<PieEntry>yPieVals=new ArrayList<>();
    private OrderAsyncHttpClient mClient;
    private HashMap<String,String> params;
    private Date charttime;
    private String TAG_GetCestbonCount="GetCestbonCount";
    private LCBusiness lcBusiness;
    private List<LCBusiness> lcBusinesslist;
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
        setContentView(R.layout.activity_manage_chart);
        initview();
        //  initLableData();
        //  initEntries();
        //  initshow();
    }

    private void initBarData(int qty) {

        //   iBarDataSets = new ArrayList<>();
        BarDataSet barDataSet=new BarDataSet(barEntries,"怡宝物流商");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
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

        // data.setValueTextColor(Color.YELLOW);
        mbarchart.setData(data);
        mbarchart.setOnChartValueSelectedListener(this);
        mbarchart.setDragEnabled(true);
        mbarchart.setScaleEnabled(true);
        //  mbarchart.setMinOffset(1f);
        mbarchart.setDrawGridBackground(false);
        mbarchart.setDrawBarShadow(false);
        mbarchart.setScaleYEnabled(false);
//        Legend mlegend=mbarchart.getLegend();
//        mlegend.setForm(Legend.LegendForm.CIRCLE);
//        mlegend.setFormSize(6f);
//        mlegend.setTextColor(Color.RED);
//        XAxis xAxis=mbarchart.getXAxis();
//        xAxis.addLimitLine(limitLine);
        mbarchart.animateY(2000);
        //设置X轴的标签样式内容
        XAxis xAxis=mbarchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        //取消X轴的竖向分割线
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new XLabersAxisValueFormatter(mbarchart,labels,1-data.getBarWidth()/2));
        xAxis.setLabelRotationAngle(90);
        xAxis.setAxisMinValue(0);
        //设置标识labers的最小间距为1f，防止重复出现
        xAxis.setGranularity(1f);
        xAxis.setAxisMaxValue(data.getXMax()+data.getBarWidth());
        YAxis yAxisleft=mbarchart.getAxisLeft();
        yAxisleft.setAxisMinValue(0);
        YAxis yAxisright=mbarchart.getAxisRight();
        yAxisright.setAxisMinValue(0);
        mbarchart.setDescription("汇总发货总数："+qty);
        mbarchart.setDescriptionColor(getResources().getColor(R.color.red));
        mbarchart.setDescriptionTextSize(12);
        mbarchart.setDescriptionPosition(mbarchart.getWidth()-mbarchart.getViewPortHandler().offsetRight(),mbarchart.getViewPortHandler().offsetBottom()-4);
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
            LCBusiness lcBusinessi = lcBusinesslist.get(i);
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
            mpiechart.setCenterText(lcBusinessi.getTms_fllet_name()+"发货总数："+lcBusinessi.getQtyTotal());
            mpiechart.setDescription("");
            mpiechart.setExtraOffsets(5, 5, 5, 5);
            mpiechart.setTransparentCircleColor(Color.WHITE);
            mpiechart.animateXY(1000,1000);
            mpiechart.setEntryLabelColor(Color.WHITE);
            mpiechart.setEntryLabelTextSize(12f);
            //设置右上角注解条目样式
            Legend l = mpiechart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            mpiechart.invalidate();
        }else {
            mpiechart.setVisibility(View.GONE);
        }
    }

    private void initview() {
        titelView= (SlidingTitleView) findViewById(R.id.slidingTitelView_managechartActivity);
        titelView.setMode(SlidingTitleView.MODE_BACK);
        titelView.setText("管理信息");
        chartTitel= (TextView) findViewById(R.id.tv_barChart);
        mbarchart= (BarChart) findViewById(R.id.barChart_managechartActivity);
        mpiechart= (PieChart) findViewById(R.id.pieChart_managechartActivity);
        mClient=new OrderAsyncHttpClient(this,this);
        //  charttime= DateUtil.getDateTime(System.currentTimeMillis()-1000L*60*60*24*22);//初始化设置查询前一天的统计图数据
        getChartData();
    }

    private void getChartData() {
        params = new HashMap<String,String>();
        params.put("strUserId", SharedPreferencesUtils.getUserId());
        params.put("chartDate", DateUtil.formateWithoutTime(charttime));
        // params.put("chartDate","");
        params.put("strLicense", "");
        mClient.sendRequest(Constants.URL.GetCestbonCount, params, TAG_GetCestbonCount);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        showToastMsg("查看"+lcBusinesslist.get((Integer) e.getData()).getTms_fllet_name()+"圆饼");
        initPieData((Integer) e.getData());

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
        if (msg.equals("error")){
            chartTitel.setText("全日期发货数量汇总");
            charttime=null;
          //  getChartData();
            return;
        }
        if (request_tag.equals(TAG_GetCestbonCount)){
            JSONObject jo= JSON.parseObject(msg);
            if(lcBusinesslist==null){
                chartTitel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                .setListener(new DateHandler(chartTitel.getId()))
                                .setInitialDate(charttime)
                                // .setMinDate(new Date()) 这样就导致取时间最小为当前时间
                                .setMaxDate(DateUtil.getDateTime(System.currentTimeMillis()))
                                .build()
                                .show();
                    }
                });
            }
            lcBusinesslist=new ArrayList<>();
            lcBusinesslist = JSON.parseArray(jo.getString("result"),LCBusiness.class);
            if (lcBusinesslist !=null&& lcBusinesslist.size()>0){
                setData(lcBusinesslist);
            }
        }

    }

    private void setData(List<LCBusiness> lcBusinesslist) {
        lcBusiness = new LCBusiness();
        int  qtyTotal=0;//各物流商汇总发货数
        labels=new ArrayList<>();
        barEntries=new ArrayList<>();
        for (int i=0;i<lcBusinesslist.size();i++){
            lcBusiness =lcBusinesslist.get(i);
            if (lcBusiness.getQtyTotal()>0) {
                labels.add(lcBusiness.getTms_fllet_name());
                barEntries.add(new BarEntry(i + 1f, lcBusiness.getQtyTotal(),i));
                qtyTotal += lcBusiness.getQtyTotal();
            }
        }
        initBarData(qtyTotal);
        initPieData(0);
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
                if (tv.getId()==R.id.tv_barChart){
//                    date.setHours(0);
//                    date.setMinutes(0);
//                    date.setSeconds(0);
                    tv.setText(df.format(date)+"发货汇总");
                   // tv.setTextColor(getResources().getColor(R.color.white));
                    charttime=date;//设置为XXXX-XX-XX 00:00:00
                    getChartData();
                }
                return;
            }
        }
    }
}
