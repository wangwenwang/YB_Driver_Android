package com.kaidongyuan.app.basemodule.utils.nomalutils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/10.
 */
public class DateUtil {
    public static String formateWithoutTime(Date date){
        if (date!=null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(date);
        }else {
            return "";
        }
    }

    public static String formateWithTime(Date date){
        if(date!=null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.format(date);
        }else return "";
    }
    public static Date getDateTime(long time){
        Calendar c=Calendar.getInstance();
        Date date=new Date(time);
        c.setTime(date);
        return date;
    }
    /**
     * 凌晨
     * @param date
     * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
     *       1 返回yyyy-MM-dd 23:59:59日期
     * @return
     */
    public static Date weeHours(Date date, int flag) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);

        if (flag == 0) {
            return cal.getTime();
        } else if (flag == 1) {
            //凌晨23:59:59
            cal.setTimeInMillis(cal.getTimeInMillis()+23*60*60*1000 + 59*60*1000 + 59*1000);
        }
        return cal.getTime();
    }


}
