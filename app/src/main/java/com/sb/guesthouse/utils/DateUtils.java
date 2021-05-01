package com.sb.guesthouse.utils;

import android.os.CpuUsageInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Methods related to the date operations
 * @author sharn25
 * @since 02-03-2021
 * @version 0.0
 */
public class DateUtils {
    private static final String TAG = "DateUtils";
    public static String getDate(String format){
        Date c = Calendar.getInstance().getTime();
        LogUtil.l(TAG,"Current Time => " + c, true);
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getFormatedDate(String date, String format) {
        try {
            Date df = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            SimpleDateFormat df2 = new SimpleDateFormat(format, Locale.getDefault());
            String formatedDate = df2.format(df);
            return formatedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public static Date getDatefromString(String date, String format){
        try {
            Date df = new SimpleDateFormat(format).parse(date);
            return df;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDays(String Date1, String Date2){
        try {
            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("dd-MMM-yyyy");

            //Setting dates
            date1 = dates.parse(Date1);
            date2 = dates.parse(Date2);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            String dayDifference = Long.toString(differenceDates + 1);

            LogUtil.l(TAG,"Days: " + dayDifference,true);
            return dayDifference;
        }
        catch (Exception exception) {
            LogUtil.e(TAG, "exception " + exception, true);
            return "0 Days";
        }
    }
}
