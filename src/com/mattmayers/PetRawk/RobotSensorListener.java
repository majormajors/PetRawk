package com.mattmayers.PetRawk;

import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import orbotix.robot.base.DeviceAsyncData;
import orbotix.robot.base.DeviceMessenger.AsyncDataListener;
import orbotix.robot.base.DeviceSensorsAsyncData;
import orbotix.robot.sensor.AccelerometerData;
import orbotix.robot.sensor.DeviceSensorsData;
import orbotix.robot.sensor.MagnetometerData;

public class RobotSensorListener implements AsyncDataListener {
    Handler mHandler;

    private final static String TAG = "RobotSensorListener";

    public final static int ACCELEROMETER_DATA = 3;
    public final static int MAGNETOMETER_DATA  = 4;

    public RobotSensorListener(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onDataReceived(DeviceAsyncData data) {
        Log.i(TAG, "Data Received!");
        if(data instanceof DeviceSensorsAsyncData){
            List<DeviceSensorsData> data_list = ((DeviceSensorsAsyncData)data).getAsyncData();

            if(data_list != null){
                Log.i(TAG, "Data List has " + data_list.size() + " things!");
                for(DeviceSensorsData datum : data_list){
                    AccelerometerData accel = datum.getAccelerometerData();
                    if(accel != null){
                        float[] accelData = {
                            (float)accel.getFilteredAcceleration().x,
                            (float)accel.getFilteredAcceleration().y,
                            (float)accel.getFilteredAcceleration().z
                        };
                        Message msg = mHandler.obtainMessage(ACCELEROMETER_DATA, accelData);
                        mHandler.sendMessage(msg);
                    }
                    MagnetometerData magnet = datum.getMagnetometerData();
                    if(magnet != null){
                        float[] magnetData = {
                            (float)magnet.getMagnetometerDataFiltered().x,
                            (float)magnet.getMagnetometerDataFiltered().y,
                            (float)magnet.getMagnetometerDataFiltered().z
                        };
                        Message msg = mHandler.obtainMessage(MAGNETOMETER_DATA, magnetData);
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }
    }
}
