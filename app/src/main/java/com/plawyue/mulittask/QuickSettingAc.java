package com.plawyue.mulittask;

import static com.plawyue.mulittask.MainActivity.openAppByPackageName;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class QuickSettingAc extends AppCompatActivity {
    Context mcontent=this;
    private List<DeviceInformation> mDatas = new ArrayList<>();
    private MyArrayAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_setting);
        List<String> packname = new ArrayList<>();
        SharedPreferences sp=this.getSharedPreferences("data", Context.MODE_PRIVATE);
        int size=sp.getInt("size",0);
        for(int i=0;i<size;i++) {
           packname.add ( sp.getString("type"+i,null));
        }
        ArrayList<String> applist=new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, applist
        );
        ListView listView = (ListView) findViewById(R.id.listview2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPackages(packname);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceInformation deviceInformation=mDatas.get(i);
                openAppByPackageName(mcontent,deviceInformation.getDeviceAddress());
                finish();
            }
        });
    }
    private void getPackages( List<String> packname) {
        for(int i=0;i!=packname.size();i++){
            String className= null;
            try {
                className = String.valueOf(getPackageManager().getApplicationInfo(packname.get(i),0).loadLabel(getPackageManager()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap icon = null;
            try {
                icon=drawableToBitamp(getPackageManager().getApplicationInfo(packname.get(i),0).loadIcon(getPackageManager()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            DeviceInformation deviceInformation=new DeviceInformation(className,packname.get(i),icon);
            mDatas.add(deviceInformation);
        }
    }
    private Bitmap drawableToBitamp(Drawable drawable)
    {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;

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
}