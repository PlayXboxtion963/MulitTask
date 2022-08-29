package com.plawyue.mulittask;

import android.graphics.Bitmap;

public class DeviceInformation {
    private String deviceName;
    private String deviceAddress;
    private Bitmap iconofapp;

    public DeviceInformation(String deviceName, String deviceAddress,Bitmap icon) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.iconofapp=icon;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public Bitmap getimage(){return  iconofapp;}
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
