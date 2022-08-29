package com.plawyue.mulittask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class MyArrayAdapter extends BaseAdapter {

    private final List<DeviceInformation> mDatas;
    private final Context mContext;

    public MyArrayAdapter(List<DeviceInformation> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.device_item_layout,null);
        }
        TextView nameTv = view.findViewById(R.id.device_name);
        TextView addressTv = view.findViewById(R.id.device_address);
        ImageView appicon=view.findViewById(R.id.appicon);

        DeviceInformation deviceInformation = mDatas.get(i);
        try {
            appicon.setImageBitmap(deviceInformation.getimage());
            nameTv.setText(deviceInformation.getDeviceName());
        }catch (Exception e){

        }

        //addressTv.setText(deviceInformation.getDeviceAddress());
        return view;
    }


}
