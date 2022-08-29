package com.plawyue.mulittask;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ComponentName cn = new ComponentName("com.plawyue.mulittask", "com.plawyue.mulittask.MainActivity");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptions activityOptions= getActivityOptions();
        int min=300;
        int max=500;
        Random random = new Random();
        int top = random.nextInt(max)%(max-min+1) + min;
        int bottom=random.nextInt(1800)%(1800-1500+1) + 1500;
        activityOptions.setLaunchBounds(new Rect(100,top,1000,bottom));
        Bundle bundle=activityOptions.toBundle();
        intent.setComponent(cn);
        startActivity(intent,bundle);
        finish();

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
}