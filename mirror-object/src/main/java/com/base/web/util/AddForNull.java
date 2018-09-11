package com.base.web.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计数据补空
 */
public class AddForNull {

    public List<Map> addNull(List<Map> list, String selectTimeStr, String selectType) {
        if (!selectType.equals("3")) {
            //x轴上显示时间个数
            int weekLength = 0;
            Date nowTime = new Date();
            Date selectTime = null;
            Calendar selectTimeCal = Calendar.getInstance();
            Calendar nowTimeCal = Calendar.getInstance();
            String timeType = "yyyy";

            if (selectType.equals("0")) {
                timeType = "yyyy-MM";
            }
            DateFormat format = new SimpleDateFormat(timeType);

            try {
                selectTime = format.parse(selectTimeStr);
                selectTimeCal.setTime(selectTime);
                nowTimeCal.setTime(nowTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //获取当前时间是该月的第几周
            if (selectType.equals("0")) {
                //统计显示全部周数
                weekLength = selectTimeCal.getActualMaximum(Calendar.WEEK_OF_MONTH);

                //统计仅显示到当前周数
                if (nowTimeCal.get(Calendar.YEAR) == selectTimeCal.get(Calendar.YEAR)
                        && nowTimeCal.get(Calendar.MONTH) == selectTimeCal.get(Calendar.MONTH)) {
                    weekLength = nowTimeCal.getActualMaximum(Calendar.WEEK_OF_MONTH);
                }
            }

            //获取当前时间的月份
            if (selectType.equals("1")) {
                weekLength = nowTimeCal.get(Calendar.MONTH) + 1;
                if (nowTimeCal.get(Calendar.YEAR) > selectTimeCal.get(Calendar.YEAR)) {
                    weekLength = 12;
                }
            }

            //获取当前时间的季度
            if (selectType.equals("2")) {
                weekLength = getSeason(nowTime);
                if (nowTimeCal.get(Calendar.YEAR) > selectTimeCal.get(Calendar.YEAR)) {
                    weekLength = 4;
                }
            }

            //获取当前时间是一年中的第几周
            if (selectType.equals("4")) {
                weekLength = nowTimeCal.get(Calendar.WEEK_OF_YEAR);
                if (nowTimeCal.get(Calendar.YEAR) > selectTimeCal.get(Calendar.YEAR)) {
                    weekLength = 52;
                }
            }

            String[] timeArray;
            String[] salesArray;
            String[] pageViewArray =new String[]{};
            int weekNum;
            int selectItemIndex;
            StringBuffer newTimeArray = new StringBuffer();
            StringBuffer newSales = new StringBuffer();
            StringBuffer newPageView = new StringBuffer();

            for (Map selectItem : list) {
                selectItemIndex = 0;

                //读取查询数据
                timeArray = selectItem.get("createTime").toString().split(",");
                salesArray = selectItem.get("sales").toString().split(",");
                if(selectItem.get("pageView") != null) {
                    pageViewArray = selectItem.get("pageView").toString().split(",");
                    newPageView.setLength(0);
                }
                newTimeArray.setLength(0);
                newSales.setLength(0);

                for (weekNum = 1; weekNum <= weekLength; weekNum++) {

                    //拼接显示时间
                    if (selectType.equals("0") || selectType.equals("4")) {
                        newTimeArray.append("第" + weekNum + "周,");
                    }
                    if (selectType.equals("1")) {
                        newTimeArray.append(weekNum + "月,");
                    }
                    if (selectType.equals("2")) {
                        newTimeArray.append(weekNum + "季,");
                    }

                    // 拼接每周的销量和浏览量,空的补零
                    if (selectItemIndex < timeArray.length && timeArray[selectItemIndex]
                            .equals(String.valueOf(weekNum))) {

                        newSales.append(salesArray[selectItemIndex]+",");
                        if(newPageView.length() > 0) {
                            newPageView.append(pageViewArray[selectItemIndex] + ",");
                        }
                        selectItemIndex++;

                    } else {
                        newSales.append("0,");
                        if(newPageView.length() > 0) {
                            newPageView.append("0,");
                        }
                    }
                }
                selectItem.put("sales", newSales);
                if(newPageView.length() > 0) {
                    selectItem.put("pageView", newPageView);
                }
                selectItem.put("createTime", newTimeArray);
            }
        }
        return list;
    }

    /**
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static int getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

}
