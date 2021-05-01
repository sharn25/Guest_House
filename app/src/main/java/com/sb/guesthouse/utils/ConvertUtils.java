package com.sb.guesthouse.utils;

import java.util.Date;

/**
 * Converter methods
 * @author sharn25
 * @since 27-02-2021
 * @version 0.0
 */
public class ConvertUtils {

    public static double stringToDouble(String text){
        try{
            return Double.parseDouble(text);
        }catch(Exception e){
            return 0.0;
        }
    }

    public static String doubleToString(double text){
        try{
            return Double.toString(text);
        }catch(Exception e){
            return "";
        }
    }

    public static Date stringToDate(String date, String format){
        return DateUtils.getDatefromString(date, format);
    }
}
