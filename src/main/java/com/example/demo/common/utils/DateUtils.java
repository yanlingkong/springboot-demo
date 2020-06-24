package com.example.demo.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Auther: liuc
 * @Date: 18-12-18 11:48
 * @email i@liuchaoboy.com
 * @Description:
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils{
    /** 时间格式(yyyy-MM-dd) */
    public final static String DATE_PATTERN = "yyyy-MM-dd";

    /** 时间格式(yyyy/MM/dd) */
    public final static String DATE_PATTERN_SLASH = "yyyy/MM/dd";

    /** 时间格式(yyyy-MM-dd HH:mm:ss) */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 时间格式(yyyy年M月dd日 ah:mm:ss) 代码生成器使用 */
    public final static String DATE_TIME_CHN_PATTERN = "yyyy年M月dd日 ah:mm:ss";


    /**
     * 获取当前日期
     *
     * @return 今日日期
     */
    public static Date today(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime();
    }

    public static String now(String pattern){
        return format(now(), pattern);
    }


    public static Date now(){
        return new Date();
    }

    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 两个日期相减，得到差值天数
     * @param minus		被减数
     * @param Minuend	减数
     * @return
     */
    public static int minusDate(Date minus, Date Minuend){
        Calendar minusCal=Calendar.getInstance();
        Calendar minuendCal=Calendar.getInstance();
        minusCal.setTime(getMinDateOfDay(minus));
        minuendCal.setTime(getMinDateOfDay(Minuend));

        long minusInMillis = minusCal.getTimeInMillis();
        long MinuendInMillis = minuendCal.getTimeInMillis();

        return (int) ((minusInMillis-MinuendInMillis)/(1000*60*60*24));
    }


    /**
     *
     * @param date
     * @return 获取某一天的最小时间
     */
    public static Date getMinDateOfDay(Date date){
        return getFullDate(date,0,0,0,0);
    }

    /**
     * 给传入的一个时间对象设置小时 、 分钟、 秒的值
     * 如果传入的参数值过大，则返回     23:59:59 999
     * 如果传入的参数值过小，则返回     00:00:00 000
     * @param date   需要返回的时间
     * @param hour    小时   24小时制
     * @param minute   分钟
     * @param second   秒
     * @param mills    毫秒值秒
     * @return
     */
    public static Date getFullDate(Date date,int hour,int minute,int second,int mills){
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        if(hour>23){
            hour=23;
        }else if(hour<0){
            hour=0;
        }
        cd.set(Calendar.HOUR_OF_DAY, hour);

        if(minute>59){
            minute = 59;
        }else if(minute<0){
            minute = 0;
        }
        cd.set(Calendar.MINUTE,minute);

        if(second>59){
            second=59;
        }else if(second<0){
            second=0;
        }
        cd.set(Calendar.SECOND, minute);


        if(mills>999){
            mills=999;
        }else if(second<0){
            mills=0;
        }
        cd.set(Calendar.MILLISECOND, minute);

        return cd.getTime();
    }
}
