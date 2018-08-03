package com.example.administrator.ybdriver.ui.widget;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Created by Administrator on 2016/8/18.、
 */
public class IntegerValueFormatter implements ValueFormatter {

  private List<String> mylabels;

    public IntegerValueFormatter(List<String> mylabels) {
        this.mylabels = mylabels;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        Integer intXValue = (int) entry.getX();
       if (mylabels!=null&&mylabels.size()>(intXValue-1)){
           return  mylabels.get(intXValue-1);
       }else {
           Integer intValue = (int) entry.getY();
           return String.valueOf(intValue);
       }
    }
}
