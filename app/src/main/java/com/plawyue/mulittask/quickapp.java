package com.plawyue.mulittask;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.quicksettings.TileService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class quickapp extends TileService {
    public quickapp() {
    }
    @Override
    public void onClick() {
        System.out.println("ddddddddddddddddddddddddddd");
        ComponentName cn = new ComponentName("com.plawyue.mulittask", "com.plawyue.mulittask.QuickSettingAc");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptions activityOptions= getActivityOptions();

        activityOptions.setLaunchBounds(new Rect(100,1300,1000,1800));
        Bundle bundle=activityOptions.toBundle();
        intent.setComponent(cn);
        startActivity(intent,bundle);
        collapseStatusBar(this);
    }

    private static ActivityOptions getActivityOptions() {
        ActivityOptions options =ActivityOptions.makeBasic();
        try {
            Method method=ActivityOptions.class.getMethod("setLaunchWindowingMode",int.class);
            method.invoke(options,5);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return options;
    }
    public static void collapseStatusBar(Context context) {
        try {
            @SuppressLint("WrongConstant") Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;

            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}