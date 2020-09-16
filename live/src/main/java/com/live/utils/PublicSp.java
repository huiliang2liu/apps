package com.live.utils;

import android.content.Context;

import com.io.sava.SharedPreferences;

public class PublicSp {
    private static final String NAME = "public_sp";
    private static final String REGION = "region";
    private static final String COUNTRY = "country";
    private static final String CITY = "city";
    private static final String IP = "ip";
    private static final String DEFAULT_REGION = "湖南";
    private static final String DEFAULT_COUNTRY = "中国";
    private static final String DEFAULT_CITY = "耒阳";

    public static SharedPreferences sp(Context context) {
        return new SharedPreferences(context, NAME, Context.MODE_PRIVATE);
    }

    public static void setRegion(Context context, String region) {
        sp(context).putString(REGION, region).commit();
    }

    public static String getRegion(Context context) {
        return sp(context).getString(REGION, DEFAULT_REGION);
    }

    public static void setCountry(Context context, String country) {
        sp(context).putString(COUNTRY, country).commit();
    }

    public static String getCountry(Context context) {
        return sp(context).getString(COUNTRY, DEFAULT_COUNTRY);
    }

    public static void setCity(Context context, String city) {
        sp(context).putString(CITY, city).commit();
    }

    public static String getCity(Context context) {
        return sp(context).getString(CITY, DEFAULT_CITY);
    }

    public static void setIp(Context context, String ip) {
        sp(context).putString(IP, ip).commit();
    }

    public static String getIp(Context context) {
        return sp(context).getString(IP, "");
    }
}
