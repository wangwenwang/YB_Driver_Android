package com.example.administrator.ybdriver.ui.widget;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.List;

/**
 * Created by Administrator on 2016/8/18.
 */
public class XLabersAxisValueFormatter implements AxisValueFormatter {
    private BarLineChartBase<?> chart;
    private List<String>laberstrs;
    private float offset;

    public XLabersAxisValueFormatter(BarLineChartBase<?> chart, List<String> laberstrs,float offset) {
        this.chart = chart;
        this.laberstrs = laberstrs;
        this.offset=offset;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        //考虑负数的偏移量
        if (value<0.5){
            return "";
        }
        int item= (int) (value-0.5);
        if (item<0){
            return "";
        }else if (laberstrs!=null&&item<laberstrs.size()){
            return laberstrs.get(item);
        }else {
            return "";
        }
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
