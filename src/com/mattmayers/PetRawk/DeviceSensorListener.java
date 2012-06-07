package com.mattmayers.PetRawk;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DeviceSensorListener implements SensorEventListener {
    private Handler mHandler;
    
    public final static int MAGNETOMETER_DATA = 0;
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
            case Sensor.TYPE_LINEAR_ACCELERATION:
                msg = mHandler.obtainMessage(ACCELEROMETER_DATA, event.values.clone());
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                msg = mHandler.obtainMessage(MAGNETOMETER_DATA, event.values.clone());
                break;
        }
        
        if(msg != null){
            mHandler.sendMessage(msg);
        }
    }
}
