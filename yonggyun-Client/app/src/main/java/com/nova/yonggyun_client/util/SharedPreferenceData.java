package com.nova.yonggyun_client.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.StringTokenizer;

public class SharedPreferenceData {

    static final String PREF_FILE_KEY = "userinfo";

    // 사용법
    // SharedPreferenceData.setAttribute(getBaseContext() , "1" ,"일");
    // String data1 = SharedPreferenceData.getAttribute(getBaseContext() , "1");
    // 값 저장
    public static void setAttribute(Context context , String key , String value){
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_KEY , context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    // 값 읽기
    public static String getAttribute(Context context , String key){
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_KEY , context.MODE_PRIVATE);
        return prefs.getString(key,null);
    }

    // 데이터 삭제
    public static void removeAttribute(Context context , String key){
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_KEY , context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    // 전체 삭제
    public static void removeAllAttribute(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_KEY , context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }


}
