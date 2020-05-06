/*
 * Copyright (C), 2005-2020, 深圳市珍爱网信息技术有限公司
 */
package com.jinghui.callback.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 回调工具类
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:30
 * @Version V1.0
 */
public class CallBackUtils {
    /**
     * 回调触发时间 暂时代码写死，可使用config方式等动态分配
     */
    private final static int CALL_BACK_ADDDAYS = 2;
    private final static int CALL_BACK_HOURS = 12;
    private final static int CALL_BACK_MINUTES = 00;
    private final static int CALL_BACK_SECONDS = 00;

    /**
     * 回调触发时间 过了，就直接顺延20s，不直接顺延到第二天
     */
    private final static int CALL_BACK_TIME_DEFAULT = 20;

    /**
     * 获取当前时间 到指定时间的 延时时间
     *
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public static int getDelaySeconds(int hours, int minutes, int seconds) {
        return getDelaySeconds(CALL_BACK_ADDDAYS, hours, minutes, seconds);
    }

    public static int getDelaySeconds(int addDays, int hours, int minutes, int seconds) {
        long currentTimes = System.currentTimeMillis();
        Date nowTime = new Date(currentTimes);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.DAY_OF_YEAR, addDays);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        long ms = calendar.getTime().getTime() - currentTimes;
        //相同秒内为0
        int delaySeconds = (int) (ms / 1000);
        //防止负数
        if (delaySeconds <= 0) {
            delaySeconds = CALL_BACK_TIME_DEFAULT;
        }
        return delaySeconds;
    }

    public static Date getDelayTime() {
        return getDelayTime(CALL_BACK_ADDDAYS, CALL_BACK_HOURS, CALL_BACK_MINUTES, CALL_BACK_SECONDS);
    }

    public static Date getDelayTime(int addDays, int hours, int minutes, int seconds) {
        long currentTimes = System.currentTimeMillis();
        Date nowTime = new Date(currentTimes);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.DAY_OF_YEAR, addDays);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前时间，距离 默认延时分配，还剩多少秒
     *
     * @return
     */
    public static int getDelaySeconds() {
        return getDelaySeconds(CALL_BACK_HOURS, CALL_BACK_MINUTES, CALL_BACK_SECONDS);
    }

    /**
     * 获取当前时间，距离指定时间 ，还剩多少秒
     * <pre>
     *   超过指定时间，则顺延 制定的秒数
     * </pre>
     *
     * @param appointTime
     * @return
     */
    public static int getDelaySeconds(Date appointTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointTime);
        calendar.add(Calendar.DAY_OF_YEAR, CALL_BACK_ADDDAYS);
        calendar.set(Calendar.HOUR_OF_DAY, CALL_BACK_HOURS);
        calendar.set(Calendar.MINUTE, CALL_BACK_MINUTES);
        calendar.set(Calendar.SECOND, CALL_BACK_MINUTES);

        long currentTimes = System.currentTimeMillis();
        long ms = calendar.getTime().getTime() - currentTimes;
        int delaySeconds = (int) (ms / 1000);
        //防止负数
        if (delaySeconds <= 0) {
            delaySeconds = CALL_BACK_TIME_DEFAULT;
        }
        return delaySeconds;
    }

    /**
     * 返回首字母小写beanName
     */
    public static String getBeanName(String beanName) {
        return (new StringBuilder()).append(Character.toLowerCase(beanName.charAt(0))).append(beanName.substring(1)).toString();
    }

    public static void main(String[] args) {
        System.out.println(getDelaySeconds());
        System.out.println(getDelaySeconds(new Date()));
    }
}
