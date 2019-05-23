package com.example.horizontionview.Util;

import android.app.Activity;
import android.content.Context;

import com.example.horizontionview.MainActivity;

public class DensityUtil {
    /**
     *根据手机的分辨率 从dip-->px
     */
    public static int dip2px(Context context ,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     *px--->dp
     */
    public static int px2dip(Context context,int pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
