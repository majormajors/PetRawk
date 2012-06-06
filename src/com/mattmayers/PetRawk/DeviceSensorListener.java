package com.mattmayers.PetRawk;

import orbotix.robot.base.DeviceAsyncData;
import orbotix.robot.base.DeviceMessenger.AsyncDataListener;
import orbotix.robot.base.DeviceSensorsAsyncData;
import orbotix.robot.util.SensorLowPassFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DeviceSensorListener implements SensorEventListener, AsyncDataListener {
    private Handler mHandler;
    
    public final static int MAGNET_DATA = 0;
    public final static int ACCELEROMETER_DATA = 1;
    
    public DeviceSensorListener(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int arg1) {
        Log.d("MagnetAccuracy", String.valueOf(arg1));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Message msg = null;
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                msg = mHandler.obtainMessage(ACCELEROMETER_DATA, event.values.clone());
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                msg = mHandler.obtainMessage(MAGNET_DATA, event.values.clone());
                break;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDataReceived(DeviceAsyncData arg0) {
        // TODO Auto-generated method stub
        
    }

}
