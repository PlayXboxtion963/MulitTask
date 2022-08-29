package com.plawyue.mulittask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> applist;
    List<DeviceInformation> Realapplist = new ArrayList<>();
    private List<DeviceInformation> mDatas = new ArrayList<>();
    private List<DeviceInformation> Sysdata = new ArrayList<>();
    private MyArrayAdapter mAdapter;


    Context mcontent;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int flagaaa=getIntent().getIntExtra("ishide",0);
        if(flagaaa==1){
            findViewById(R.id.button).setVisibility(View.GONE);
            findViewById(R.id.textView).setVisibility(View.GONE);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, 0);//left top right button
            findViewById(R.id.listview).setLayoutParams(lp);
        }
        makeStatusBarTransparent(MainActivity.this);
        mcontent=this;
        applist=new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_list_item_1, applist
        );
        ListView listView = (ListView) findViewById(R.id.listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPackages();
                mDatas=sortdata(mDatas);
                mAdapter = new MyArrayAdapter(mDatas, mcontent);
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(mAdapter);
                    }
                });

            }
        }).start();

        SharedPreferences QuickSettingdata= this.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=QuickSettingdata.edit();
        if(QuickSettingdata.contains("size")==false){
            edit.putInt("size",0);
            edit.commit();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceInformation deviceInformation=mDatas.get(i);
                openAppByPackageName(mcontent,deviceInformation.getDeviceAddress());
                finish();

            }
        });
        List<String> packagename = new ArrayList<>();
       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               saveinfo(mDatas.get(i).getDeviceAddress());
               view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
               Toast.makeText(mcontent,"已保存",Toast.LENGTH_SHORT).show();

               return true;
           }
       });
       Button mbutton=findViewById(R.id.button);
       mbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               clear(mcontent);
               view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
           }
       });
    }
    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.commit();
        Toast.makeText(context,"已清空",Toast.LENGTH_SHORT).show();

    }

    private  List<DeviceInformation>  sortdata(List<DeviceInformation> mDatas){
        int xin;
        int yuan;
        ArrayList<String> appnamearray=new ArrayList<>();
        ArrayList<String> temp=new ArrayList<>();
        for(int i=0;i!=mDatas.size();i++){
            appnamearray.add(mDatas.get(i).getDeviceName());
            temp.add(mDatas.get(i).getDeviceName());
        }

        Collections.sort(appnamearray,new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                /*
                 * *按ASCII表排序
                 * *可以看到return有个? :表达式。
                 * *解释一下
                 * *o1.compareToIgnoreCase(o2) == 0是先看看不区分大小写对比出来的值是否等于0,
                 * *如果=0，返回-o1.compareTo(o2)，前面加-是让小写在大写前面
                 * *如果不等于0，返回o1.compareToIgnoreCase(o2)
                 */
                return Collator.getInstance(Locale.CHINA).compare(o1,o2);
                //return (o1.compareToIgnoreCase(o2) == 0 ? -o1.compareTo(o2) : o1.compareToIgnoreCase(o2));
            }

        });
       List<DeviceInformation> returndata= new ArrayList<>();
       for(int i=0;i!=mDatas.size();i++){
           returndata.add(null);
       }
       for(int i=0;i!=mDatas.size();i++){
           xin=appnamearray.indexOf(temp.get(i));
           yuan=i;
           System.out.println("旧的"+i+"在新的第"+xin);
           System.out.println(appnamearray.get(i));
           returndata.set(xin,mDatas.get(yuan));
       }
       return returndata;
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getPackages() {
        // 获取已经安装的所有应用, PackageInfo　系统类，包含应用信息
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        Intent localIntent = new Intent("android.intent.action.MAIN", null);
        localIntent.addCategory("android.intent.category.LAUNCHER");

        List<ResolveInfo> appList = mcontent.getPackageManager().queryIntentActivities(localIntent, 0);
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo resolveInfo = appList.get(i);
            String packageStr = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.loadLabel(getPackageManager()).toString();
            Bitmap icon=drawableToBitamp(resolveInfo.activityInfo.applicationInfo.loadIcon(getPackageManager()));
            PackageInfo mPackageInfo = null;
            try {
                mPackageInfo = mcontent.getPackageManager().getPackageInfo(packageStr, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) { //非系统应用
                if(resolveInfo.activityInfo.enabled==false){
                   // icon=toGrayscale(drawableToBitamp(packageInfo.applicationInfo.loadIcon(getPackageManager())));
                }
                else{
                    DeviceInformation deviceInformation=new DeviceInformation(className,packageStr,icon);
                    mDatas.add(deviceInformation);
                }
               // mAdapter.notifyDataSetChanged();
            } else { // 系统应用
                if(resolveInfo.activityInfo.enabled=false){
                    // icon=toGrayscale(drawableToBitamp(packageInfo.applicationInfo.loadIcon(getPackageManager())));
                }
                else{
                    DeviceInformation deviceInformation=new DeviceInformation(className,packageStr,icon);
                    mDatas.add(deviceInformation);
                }
            }
        }
    }
    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    public  Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        //Set the matrix to affect the saturation of colors.
        //A value of 0 maps the color to gray-scale.
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private Bitmap drawableToBitamp(Drawable drawable)
    {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;

    }

    public static void openAppByPackageName(Context context , String packageName) {
        Log.d("CJT","openAppByPackageName --00-- "+packageName);
        if (checkApplication(context , packageName)) {
            Log.d("CJT","openAppByPackageName --11-- "+packageName);
            Intent localIntent = new Intent("android.intent.action.MAIN", null);
            localIntent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(localIntent, 0);
            for (int i = 0; i < appList.size(); i++) {

                ResolveInfo resolveInfo = appList.get(i);
                String packageStr = resolveInfo.activityInfo.packageName;
                String className = resolveInfo.activityInfo.name;

                //Log.d("CJT","openAppByPackageName --22-- packageName  ："+packageName + " -- packageStr : " + packageStr);

                if (packageStr.equals(packageName)) {
                   // Log.d("CJT","openAppByPackageName --7777777777777777-- packageName  ："+packageName + " -- packageStr : " + packageStr);
                    // 这个就是你想要的那个Activity
                    ComponentName cn = new ComponentName(packageStr, className);
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
                    context.startActivity(intent,bundle);
                    Log.d("CJT" , "openApp-----111---打开完成！！");

                }
            }
        }else{
            Toast.makeText(context , "未安装此应用" , Toast.LENGTH_LONG).show();
        }
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

    public static boolean checkApplication(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void saveinfo(String packagename){
        SharedPreferences QuickSettingdata= this.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=QuickSettingdata.edit();

        int alreadydata=QuickSettingdata.getInt("size",0);
        for(int k=0;k!=alreadydata;k++){
            if(QuickSettingdata.getString("type"+k,null).equals(packagename)){
                Toast.makeText(mcontent,"已存在于快捷设置",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        edit.putString("type"+String.valueOf(alreadydata),packagename);
        alreadydata=alreadydata+1;
        edit.putInt("size",alreadydata);
        System.out.println(alreadydata);
        edit.commit();
    }
}